/**
 * @file  ChunkNBT.h
 *
 * @brief  Extracts and parses chunk NBT data.
 */

#pragma once
#include "ChunkData.h"
#include "Point.h"
#include <vector>
#include <iostream>

class ChunkNBT
{
public:
    /**
     * @brief  Extract and access compressed NBT data.
     *
     * @param compressedData  An array of compressed NBT byte data.
     */
    ChunkNBT(const std::vector<unsigned char>& compressedData);

    virtual ~ChunkNBT() { }

    /**
     * @brief  Gets data about this map chunk.
     *
     * @param pos  The map coordinate to be saved with the chunk.
     *
     * @return     The chunk data object.
     */
    ChunkData getChunkData(const Point pos);

private:
    std::vector<unsigned char> extractedData;
    size_t dataIndex = 0;

    /**
     * @brief  Reads extracted byte data into an arbitrary integer data type,
     *         incrementing dataIndex by the number of bytes read.
     *
     * @tparam T      The type of value to extract.
     *
     * @return        sizeof(N) bytes of data from the buffer, or zero if out
     *                of bounds.
     */
    template <typename T>
    T readBytes()
    {
        T value = 0;
        const size_t numBytes = sizeof(value);
        for (int offset = (numBytes - 1) * 8; offset >= 0; offset -= 8)
        {
            if (dataIndex >= extractedData.size())
            {
                std::cerr << "Failed to read " << numBytes 
                        << " bytes of NBT chunk data.\n";
                return 0;
            }
            value |= (static_cast<T>(extractedData[dataIndex]) << offset);
            dataIndex++;
        }
        return value;
    }
};
