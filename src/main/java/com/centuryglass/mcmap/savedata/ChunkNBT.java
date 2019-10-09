/**
 * @file  ChunkNBT.java
 * 
 * Extracts Minecraft region information from NBT-formatted byte data.
 * 
 * https://minecraft.gamepedia.com/NBT_format provides documentation for the
 * NBT data format.
 */
package com.centuryglass.mcmap.savedata;

import com.centuryglass.mcmap.util.ExtendedValidate;
import com.centuryglass.mcmap.worldinfo.Biome;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import com.centuryglass.mcmap.worldinfo.Structure;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import org.apache.commons.lang.Validate;

/**
 * ChunkNBT extracts compressed NBT chunk data arrays, and parses the extracted
 * information to generate worldinfo.ChunkData objects.
 */
public class ChunkNBT
{
    // For roughly 97% of chunks, inflated chunk data will take up no more than
    // 14x as much space as compressed data. Using this to set inflation buffer
    // size increases performance by reducing the number of reallocations 
    // needed.
    private static final int BUF_MULT = 14;
    
    // The vast majority of chunk data is useless to us. SKIPPED_TAGS defines
    // the starts of all tag names that should be skipped to save time and
    // space.
    static final ArrayList<String> SKIPPED_TAGS;
    static
    {
        SKIPPED_TAGS = new ArrayList<>();
        SKIPPED_TAGS.add("He"); // HeightMap
        SKIPPED_TAGS.add("Bl"); // BlockStates, BlockLight
        SKIPPED_TAGS.add("Sk"); // SkyLight
        SKIPPED_TAGS.add("Up"); // UpgradeData
        SKIPPED_TAGS.add("Se"); // Sections
        SKIPPED_TAGS.add("Li"); // LiquidTicks
        SKIPPED_TAGS.add("Til"); // Tile Entities
        SKIPPED_TAGS.add("Ent"); // Entities
        SKIPPED_TAGS.add("Chi"); // Children
        SKIPPED_TAGS.add("ToB"); // ToBeTicked
        SKIPPED_TAGS.add("Car"); // CarvingMasks
        SKIPPED_TAGS.add("Pos"); // PostProcessing
    }
    
    /** 
     *  Extract and store compressed NBT data.
     *
     * @param compressedData  An array of compressed NBT byte data.
     */
    public ChunkNBT(byte[] compressedData)
    {
        Validate.notNull(compressedData, "Data cannot be null.");
        Validate.isTrue(compressedData.length != 0,
                "Data cannot be length 0.");

        // Inflate ZLib compressed chunk data:
        final int bufferSize = compressedData.length * BUF_MULT;
        byte[] extractedData = new byte[bufferSize];
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData, 0, compressedData.length);
        while (! inflater.needsInput())
        {
            if (inflater.getTotalOut() >= extractedData.length)
            {
                extractedData = Arrays.copyOf(extractedData,
                        extractedData.length + bufferSize);
            }
            try
            {
                int inflated = inflater.inflate(extractedData,
                        inflater.getTotalOut(),
                        extractedData.length - inflater.getTotalOut());
                if (inflated == 0)
                {
                    break;
                }
            }
            catch(DataFormatException e)
            {
                System.err.println("Invalid zlib data!");
                return;
            }
        }
        if (inflater.getTotalOut() != extractedData.length)
        {
            extractedData = Arrays.copyOf(extractedData,
                    inflater.getTotalOut());
        }

        // Declare abstract classes for reading and storing chunk data:
            
        abstract class Parser
        {
            // Reads some specific data type from extracted data and places it
            // within a JsonObjectBuilder:
            public abstract void parseData(String name, 
                    JsonObjectBuilder builder) throws IOException;
            // Reads some specific data type from extracted data and places it
            // within a JsonArrayBuilder:
            public abstract void parseData(JsonArrayBuilder builder)
                    throws IOException;
            // Skips over a specific data tag type
            public abstract void skipData() throws IOException;
        }
        
        // Initialize data extraction objects:
        FileByteBuffer chunkStream = new FileByteBuffer(extractedData);
        JsonBuilderFactory factory = Json.createBuilderFactory(null); 
        
        // Store function classes for putting any NBT data type into
        // objectBuilders and arrayBuilders:
        Map<NBTTag, Parser> parsers = new HashMap<>();
        
        // Reads miscellaneous data from the chunk data stream:
        class Reader
        {
            // Read a character string:
            public String readString() throws IOException
            {
                short strLength = chunkStream.readShort();
                if (strLength > 0)
                {
                    byte[] stringBytes = chunkStream.readBytes(strLength);
                    return new String(stringBytes);
                }
                return "";           
            }
            // Read an array of any type:
            public JsonArrayBuilder readArray(NBTTag type) throws IOException
            {
                JsonArrayBuilder array = factory.createArrayBuilder();
                int length = chunkStream.readInt();
                for (int i = 0; i < length; i++)
                {
                    parsers.get(type).parseData(array);
                }
                return array;
            }
            // Read an NBT tag byte:
            public NBTTag readTag() throws IOException
            {
                int tagIdx = Byte.toUnsignedInt(chunkStream.readByte());
                return NBTTag.values()[tagIdx];
            }
            // Read an object/compound:
            public JsonObjectBuilder readObject() throws IOException
            {
                JsonObjectBuilder builder = factory.createObjectBuilder();
                NBTTag tag;
                do
                {
                    if (chunkStream.remaining() == 0)
                    {
                        return builder;
                    }
                    tag = readTag();
                    if (tag == NBTTag.END)
                    {
                        return builder;
                    }
                    boolean skipTag = false;
                    String name = readString();
                    for (String skipped : SKIPPED_TAGS)
                    {
                        if (name.startsWith(skipped))
                        {
                            skipTag = true;
                            break;
                        }
                    }
                    if (skipTag)
                    {
                        /*
                        System.out.println("Skipping " +name + ", type="
                                + tag.toString() + " available= "
                                + chunkStream.available());
                        */
                        parsers.get(tag).skipData();
                    }
                    else
                    {
                        /*
                        System.out.println("Reading " + name + ", type="
                                + tag.toString() + " available= "
                                + chunkStream.available());
                        */
                        parsers.get(tag).parseData(name, builder);
                    }
                }
                while (tag != NBTTag.END);
                return builder;
            }
        }
        final Reader reader = new Reader();
        
        // Define how each NBT tag type is parsed:
        // Byte data:
        parsers.put(NBTTag.BYTE, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, chunkStream.readByte());
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(chunkStream.readByte());
            }
            @Override
            public void skipData() throws IOException
            {
                chunkStream.skipByte();
            }
        });
        // Short data:
        parsers.put(NBTTag.SHORT, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, chunkStream.readShort());
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(chunkStream.readShort());
            }
            @Override
            public void skipData() throws IOException
            {
                chunkStream.skipShort();
            }
        });
        // Integer data:
        parsers.put(NBTTag.INT, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, chunkStream.readInt());
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(chunkStream.readInt());
            }
            @Override
            public void skipData() throws IOException
            {
                chunkStream.skipInt();
            }
        });
        // Long data:
        parsers.put(NBTTag.LONG, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, chunkStream.readLong());
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(chunkStream.readLong());
            }
            @Override
            public void skipData() throws IOException
            {
                chunkStream.skipLong();
            }
        });
        // Float data:
        parsers.put(NBTTag.FLOAT, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, chunkStream.readFloat());
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(chunkStream.readFloat());
            }
            @Override
            public void skipData() throws IOException
            {
                chunkStream.skipFloat();
            }
        });
        // Double data:
        parsers.put(NBTTag.DOUBLE, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, chunkStream.readDouble());
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(chunkStream.readDouble());
            }
            @Override
            public void skipData() throws IOException
            {
                chunkStream.skipDouble();
            }
        });
        // Byte array data:
        parsers.put(NBTTag.BYTE_ARRAY, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, reader.readArray(NBTTag.BYTE));
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(reader.readArray(NBTTag.BYTE));
            }
            @Override
            public void skipData() throws IOException
            {
                int length = chunkStream.readInt();
                long numSkipped = chunkStream.skip(length);
                if (numSkipped != length)
                {
                    throw new IOException("ChunkNBT: Tried to skip byte array "
                            + "of length " + length + ", but only " + numSkipped
                            + " bytes skipped.");
                }
            }
        });
        // String data:
        parsers.put(NBTTag.STRING, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, reader.readString());
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(reader.readString());
            }
            @Override
            public void skipData() throws IOException
            {
                short length = chunkStream.readShort();
                long numSkipped = chunkStream.skip(length);
                if (numSkipped != length)
                {
                    throw new IOException("ChunkNBT: Tried to skip string "
                            + "of length " + length + ", but only " + numSkipped
                            + " bytes skipped.");
                }
            }     
        });
        // List data:
        parsers.put(NBTTag.LIST, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                NBTTag type = reader.readTag();
                builder.add(name, reader.readArray(type));
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                NBTTag type = NBTTag.values()[Byte.toUnsignedInt(
                        chunkStream.readByte())];
                builder.add(reader.readArray(type));
            }
            @Override
            public void skipData() throws IOException
            {
                NBTTag type = reader.readTag();
                int length = chunkStream.readInt();
                Parser parser = parsers.get(type);
                for (int i = 0; i < length; i++)
                {
                    try
                    {
                        parser.skipData();
                    }
                    catch (IOException e)
                    {
                        System.err.println("ChunkNBT: Tried to skip " + length
                                + " tags of type " + type.toString()
                                + ", but failed at index " + i);
                        throw e;
                    }
                }
            }  
        });
        // Object data:
        parsers.put(NBTTag.COMPOUND, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, reader.readObject());
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(reader.readObject());
            }
            @Override
            public void skipData() throws IOException
            {
                NBTTag tag = null;
                Parser nameSkipper = parsers.get(NBTTag.STRING);
                do
                {
                    if (tag != null)
                    {
                        nameSkipper.skipData();
                        parsers.get(tag).skipData();
                    }
                    if (chunkStream.remaining() == 0)
                    {
                        return;
                    }
                    tag = reader.readTag();
                }
                while (tag != NBTTag.END);
            }
        });
        // Integer array data:
        parsers.put(NBTTag.INT_ARRAY, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, reader.readArray(NBTTag.INT));
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(reader.readArray(NBTTag.INT));
            }
            @Override
            public void skipData() throws IOException
            {
                int length = chunkStream.readInt() * 4;
                long numSkipped = chunkStream.skip(length);
                if (numSkipped != length)
                {
                    throw new IOException("ChunkNBT: Tried to skip int array "
                            + "of length " + length + ", but only " + numSkipped
                            + " bytes skipped.");
                }
            }
        });
        // Long array data:
        parsers.put(NBTTag.LONG_ARRAY, new Parser()
        {
            @Override
            public void parseData(String name, JsonObjectBuilder builder)
                    throws IOException
            {
                builder.add(name, reader.readArray(NBTTag.LONG));
            }
            @Override
            public void parseData(JsonArrayBuilder builder) throws IOException
            {
                builder.add(reader.readArray(NBTTag.LONG));
            }
            @Override
            public void skipData() throws IOException
            {
                int length = chunkStream.readInt() * 8;
                long numSkipped = chunkStream.skip(length);
                if (numSkipped != length)
                {
                    throw new IOException("ChunkNBT: Tried to skip int array "
                            + "of length " + length + ", but only " + numSkipped
                            + " bytes skipped.");
                }
            }
        });
        
        // Extract and store all JSON data:
        try
        {
            if (chunkStream.remaining() > 0)
            {
                chunkJSON = reader.readObject().build();
            }
        }
        catch (IOException e)
        {
            System.err.println("ChunkNBT: error encountered while parsing"
                    + " NBT data:");
            System.err.println(e.getMessage());
            System.exit(0);
        }
    }
    
    // All JSON keys needed to extract chunk data:
    private class Keys
    {
        public static final String LEVEL_DATA     = "Level";
        public static final String X_POS          = "xPos";
        public static final String Z_POS          = "zPos";
        public static final String INHABITED_TIME = "InhabitedTime";
        public static final String LAST_UPDATE    = "LastUpdate";
        public static final String BIOMES         = "Biomes";
        public static final String STRUCTURES     = "Structures";
        public static final String STRUCT_REFS    = "References";
        public static final String STRUCT_STARTS  = "Starts";
        public static final String STRUCT_BOUNDS  = "BB";   
    }
    
    /**
     * Gets data about this map chunk.
     *
     * @return  The chunk data object.
     */
    public ChunkData getChunkData()
    {
        if (chunkJSON == null)
        {
            return new ChunkData(new Point(0, 0),
                    ChunkData.ErrorFlag.INVALID_NBT);
        }
        JsonObject levelData = chunkJSON.getJsonObject("").getJsonObject(
                Keys.LEVEL_DATA);
        if (levelData == null)
        {
            return new ChunkData(new Point(0, 0),
                    ChunkData.ErrorFlag.INVALID_NBT);
        }
        Point pos = new Point(
                levelData.getInt(Keys.X_POS),
                levelData.getInt(Keys.Z_POS));
        long inhabitedTime = levelData.getJsonNumber(Keys.INHABITED_TIME)
                .longValue();
        long lastUpdate = levelData.getJsonNumber(Keys.LAST_UPDATE).longValue();
        ChunkData chunk = new ChunkData(pos, inhabitedTime, lastUpdate);
        // Read biome data:
        JsonArray biomeList = levelData.getJsonArray(Keys.BIOMES);
        if (biomeList == null)
        {
            return new ChunkData(pos, ChunkData.ErrorFlag.INVALID_NBT);
        }
        for (int i = 0; i < biomeList.size(); i++)
        {
            int biomeCode = Byte.toUnsignedInt((byte) biomeList.getInt(i));
            final Biome biome = Biome.fromCode(biomeCode);
            if (biome == null)
            {
                System.err.println("ChunkNBT.getChunkData: Found illegal "
                        + "biome code " + biomeCode);
            }
            else
            {
                chunk.addBiome(Biome.fromCode(biomeCode));
            }
        }
        // Read structure data:
        JsonObject structureData = levelData.getJsonObject(Keys.STRUCTURES);
        if (structureData != null)
        {
            JsonObject newStructures = structureData.getJsonObject(
                    Keys.STRUCT_STARTS);
            if (newStructures != null)
            {
                for (Map.Entry<String, JsonValue> entry
                        : newStructures.entrySet())
                {
                    Structure structure = Structure.parse(entry.getKey());
                    chunk.addStructure(structure);
                    JsonObject structObject = (JsonObject) entry.getValue();
                    if (structObject.containsKey(Keys.STRUCT_BOUNDS))
                    {
                        JsonArray bounds = structObject.getJsonArray(
                                Keys.STRUCT_BOUNDS);
                        // bounds = { xMin(0), yMin(1), zMin(2),
                        //            xMax(3), yMax(4), zMax(5) }
                        int xMin = bounds.getInt(0) / 16;
                        int zMin = bounds.getInt(2) / 16;
                        int xMax = bounds.getInt(3) / 16;
                        int zMax = bounds.getInt(5) / 16;
                        for (int x = xMin; x <= xMax; x++)
                        {
                            for (int z = zMin; z <= zMax; z++)
                            {
                                if(x != pos.x || z != pos.y)
                                {
                                    chunk.addStructureRef(pos, structure);
                                }
                            }
                        }
                    }   
                }
            }
            JsonObject structureRefs = structureData.getJsonObject(
                    Keys.STRUCT_REFS);
            if (structureRefs != null)
            {
                for (Map.Entry<String, JsonValue> entry
                        : structureRefs.entrySet())
                {
                    ByteBuffer pointBuf = ByteBuffer.allocate(8);
                    Structure structure = Structure.parse(entry.getKey());
                    JsonArray referenceList = (JsonArray) entry.getValue();
                    for (int i = 0; i < referenceList.size(); i++)
                    {
                        pointBuf.putLong(referenceList.getJsonNumber(i).longValue());
                        pointBuf.position(0);
                        chunk.addStructureRef(new Point(pointBuf.getInt(),
                                pointBuf.getInt()), structure);
                        pointBuf.position(0);
                    }
                }
            }
        }
        return chunk;
    }
    
    /**
     * Saves chunk data to a JSON file.
     * 
     * @param path  A path string where the file will be saved.
     */
    public final void saveToFile(String path)
    {
        ExtendedValidate.notNullOrEmpty(path, "JSON path");
        OutputStream jsonOut;
        try
        {
            jsonOut = new FileOutputStream(path);         
        }
        catch (FileNotFoundException e)
        {
            System.err.println("ChunkNBT: Error writing to " + path + ":");
            System.err.println(e.getMessage());
            return;
        }
        try (JsonWriter writer = Json.createWriter(jsonOut))
        {
            writer.writeObject(chunkJSON);
        }
    }

    // Uncompressed chunk data:
    JsonObject chunkJSON;
}
