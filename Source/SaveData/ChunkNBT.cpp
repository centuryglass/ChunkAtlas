#include "ChunkNBT.h"
#include "NBTTag.h"
#include "Debug.h"
#include <fstream>
#include <iostream>
#include <string>
#include <map>
#include <stack>
#include <functional>
#include <cstdint>
#include <cassert>
#include <zlib.h>

static const constexpr char* tempDirPath = "/tmp/MCMap";
static const constexpr size_t bufferSize = 8192;

namespace Keys
{
    static const std::string inhabitedTime = "InhabitedTime";
    static const std::string lastUpdate    = "LastUpdate";
    static const std::string biome         = "Biomes";
    static const std::string structure     = "Structures";
    static const std::string structureRefs = "References";
}

static void printZerror(const int errorCode, z_stream& zStream)
{
    if (zStream.msg != nullptr)
    {
        std::cerr << "Error: " << zStream.msg << ". ";
    }
    std::cerr << "ZLib error code: ";
    switch (errorCode)
    {
        case Z_ERRNO:
            perror("Standard error");
            return;
        case Z_STREAM_ERROR:
            std::cerr << "Invalid compression level.\n";
            return;
        case Z_DATA_ERROR:
            std::cerr << "Invalid or incomplete deflate data.\n";
            return;
        case Z_MEM_ERROR:
            std::cerr << "Out of memory.\n";
            return;
        case Z_VERSION_ERROR:
            std::cerr << "zlib version mismatch.\n";
            return;
    }
}

// Extract and access compressed NBT data.
ChunkNBT::ChunkNBT(const std::vector<unsigned char>& compressedData)
{
    if (compressedData.empty())
    {
        return;
    }

    // Setup zlib structure data:
    z_stream zlibStream;
    zlibStream.zalloc = Z_NULL;
    zlibStream.zfree = Z_NULL;
    zlibStream.opaque = Z_NULL;
    zlibStream.avail_in = 0;
    zlibStream.next_in = Z_NULL;
    int zResult = inflateInit(&zlibStream);
    if (zResult != Z_OK)
    {
        printZerror(zResult, zlibStream);
    }

    // Setup input buffer:
    zlibStream.avail_in = compressedData.size();
    zlibStream.next_in = const_cast<unsigned char*>(compressedData.data());

    // Setup output buffer:
    unsigned char outBuffer[bufferSize];
    size_t outBufferFree = bufferSize;

    // inflate until all output is processed:
    while (zResult != Z_STREAM_END)
    {
        zlibStream.avail_out = bufferSize;
        zlibStream.next_out = outBuffer;
        zResult = inflate(&zlibStream, Z_NO_FLUSH);
        if (zResult == Z_STREAM_ERROR)
        {
            printZerror(zResult, zlibStream);
        }
        switch(zResult)
        {
            case Z_NEED_DICT:
                zResult = Z_DATA_ERROR;
            case Z_DATA_ERROR:
            case Z_MEM_ERROR:
                printZerror(zResult, zlibStream);
        }
        zlibStream.next_in = const_cast<unsigned char*>(compressedData.data())
                + zlibStream.total_in;
        zlibStream.avail_in = compressedData.size() - zlibStream.total_in;

        const size_t inflateSize = bufferSize - zlibStream.avail_out;
        extractedData.reserve(extractedData.size() + inflateSize);
        for (int i = 0; i < inflateSize; i++)
        {
            extractedData.push_back(outBuffer[i]);
        }
    }
    inflateEnd(&zlibStream);
    DBG_V("Inflated " << extractedData.size() << " bytes of data from "
            << compressedData.size() << " input bytes.\n");
}


// Gets data about this map chunk.
ChunkData ChunkNBT::getChunkData(const Point pos)
{
    const auto readByte = [this]()
    {
        return readBytes<std::int8_t>();
    };

    const auto readShort = [this]()
    {
        return readBytes<std::int16_t>();
    };

    const auto readInt = [this]()
    {
        return readBytes<std::int32_t>();
    };

    const auto readLong = [this]()
    {
        return readBytes<std::int64_t>();
    };

    const auto readFloat = [this]()
    {
        assert(sizeof(float) == sizeof(std::int32_t));
        std::int32_t intData = readBytes<std::int32_t>();
        float floatValue = *reinterpret_cast<float*>(&intData);
        return floatValue;
    };

    const auto readDouble = [this]()
    {
        assert(sizeof(double) == sizeof(std::int64_t));
        std::int64_t intData = readBytes<std::int64_t>();
        double doubleValue = *reinterpret_cast<double*>(&intData);
        return doubleValue;
    };

    const auto readName = [this, &readByte, &readShort](const bool isNamed)
    {
        if (! isNamed)
        {
            return std::string();
        }
        int nameLength = readShort();
        std::string name;
        name.resize(nameLength, '!');
        for (int i = 0; i < nameLength; i++)
        {
            char charVal = readByte();
            name.replace(i, 1, 1, charVal);
            if (charVal == '\0')
            {
                return name;
            }
        }
        return name;
    };

    std::stack<std::string> openTags;
    long lastUpdate = 0;
    long inhabitedTime = 0;
    bool inBiomeList = false;
    bool inStructureRefs = false;
    Structure currentStruct = Structure::unknown;
    std::string currentStructName;
    std::vector<Biome> biomes;
    std::set<Structure> structures;

    std::map<NBTTag, std::function<void(const bool)>> parseTag;

    // Define how each tag type is parsed:
    parseTag[NBTTag::tEnd] = 
    [this, &openTags, &inBiomeList, &inStructureRefs, &currentStruct,
            &currentStructName]
    (const bool isNamed)
    {
        if(openTags.empty())
        {
            return;
        }
        else if (inStructureRefs && (openTags.top() == Keys::structureRefs))
        {
            inStructureRefs = false;
        }
        else if (inStructureRefs && (openTags.top() == currentStructName))
        {
            currentStruct = Structure::unknown;
            currentStructName = "";
        }
        openTags.pop();
    };

    parseTag[NBTTag::tByte] = 
    [this, &readName, &readByte, &inBiomeList, &biomes]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        int byteVal = readByte();
        if (inBiomeList)
        {
            while (byteVal < 0)
            {
                byteVal += 128;
            }
            biomes.push_back(static_cast<Biome>(byteVal));
        }
    };

    parseTag[NBTTag::tShort] =
    [this,  &readName, &readShort, &inBiomeList, &biomes]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        std::int16_t shortVal = readShort();
        if (inBiomeList)
        {
            biomes.push_back(static_cast<Biome>(shortVal));
        }
    };

    parseTag[NBTTag::tInt] =
    [this, &readName, &readInt, &inBiomeList, &biomes]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        std::int32_t intVal = readInt();
        if (inBiomeList)
        {
            while (intVal < 0)
            {
                intVal += 128;
            }
            biomes.push_back(static_cast<Biome>(intVal));
        }
    };

    parseTag[NBTTag::tLong] =
    [this, &readName, &readLong, &lastUpdate, &inhabitedTime]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        std::int64_t longVal = readLong();
        if (name.empty())
        {
            return;
        }
        if (name == Keys::lastUpdate)
        {
            lastUpdate = longVal;
        }
        else if (name == Keys::inhabitedTime)
        {
            inhabitedTime = longVal;
        }
    };

    parseTag[NBTTag::tFloat] =
    [this, &readName, &readFloat]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        float floatVal = readFloat();
    };

    parseTag[NBTTag::tDouble] =
    [this, &readName, &readDouble]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        double doubleVal = readDouble();
    };

    parseTag[NBTTag::tByteArray] =
    [this, &readName, &readInt, &parseTag, &inBiomeList]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        if (name == Keys::biome)
        {
            inBiomeList = true;
        }
        int length = readInt();
        for (int i = 0; i < length; i++)
        {
            parseTag[NBTTag::tByte](false);
        }
        inBiomeList = false;
    };

    parseTag[NBTTag::tString] =
    [this, &readName]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        std::string value = readName(true);
    };

    parseTag[NBTTag::tList] =
    [this, &readName, &readByte, &readInt, &parseTag, &inBiomeList]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        if (name == Keys::biome)
        {
            inBiomeList = true;
        }
        NBTTag type = static_cast<NBTTag>(readByte());
        int length = readInt();
        for (int i = 0; i < length; i++)
        {
            parseTag[type](false);
        }
        inBiomeList = false;
    };

    parseTag[NBTTag::tCompound] =
    [this, &openTags, &readName, &currentStructName, &currentStruct,
            &inStructureRefs]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        if (inStructureRefs && currentStructName.empty())
        {
            currentStruct = parseStructure(name);
            if (currentStruct != Structure::unknown)
            {
                currentStructName = name;
            }
        }
        else if (!openTags.empty() && openTags.top() == Keys::structure
                && name == Keys::structureRefs)
        {
            inStructureRefs = true;
        }
        openTags.push(name);
    };

    parseTag[NBTTag::tIntArray] =
    [this, &readName, &readByte, &readInt, &parseTag, &inBiomeList]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        if (name == Keys::biome)
        {
            inBiomeList = true;
        }
        int length = readInt();
        for (int i = 0; i < length; i++)
        {
            parseTag[NBTTag::tInt](false);
        }
        inBiomeList = false;
    };

    parseTag[NBTTag::tLongArray] =
    [this, &readName, &readByte, &readInt, &parseTag]
    (const bool isNamed)
    {
        std::string name = readName(isNamed);
        if (name == Keys::biome)
        {
            std::cout << "Found longArray biome list.\n";
        }
        int length = readInt();
        for (int i = 0; i < length; i++)
        {
            parseTag[NBTTag::tLong](false);
        }
    };

    do
    {
        NBTTag type = static_cast<NBTTag>(readByte());
        if(inStructureRefs && currentStruct != Structure::unknown 
                && type != NBTTag::tEnd)
        {
            structures.insert(currentStruct);
        }
        parseTag[type](true);
    }
    while(! openTags.empty() && dataIndex < extractedData.size());
    ChunkData chunk(pos, inhabitedTime, lastUpdate);
    for (const Biome biome : biomes)
    {
        chunk.addBiome(biome);
    }
    for (const Structure structure : structures)
    {
        chunk.addStructure(structure);
    }
    return chunk;
}
