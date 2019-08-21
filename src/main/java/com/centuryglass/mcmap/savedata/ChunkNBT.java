
package com.centuryglass.mcmap.savedata;

import com.centuryglass.mcmap.worldinfo.Biome;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import com.centuryglass.mcmap.worldinfo.Structure;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


public class ChunkNBT
{
    private static final String TMP_DIR_PATH = "/tmp/MCMap";
    private static final int BUF_SIZE = 8192;

    private class Keys
    {
        public static final String INHABITED_TIME = "InhabitedTime";
        public static final String LAST_UPDATE    = "LastUpdate";
        public static final String BIOME          = "Biomes";
        public static final String STRUCTURES     = "Structures";
        public static final String STRUCT_REFS    = "References";
    }

    /** 
     *  Extract and access compressed NBT data.
     *
     * @param compressedData  An array of compressed NBT byte data.
     */
    public ChunkNBT(byte[] compressedData)
    {
        if (compressedData == null || compressedData.length == 0)
        {
            return;
        }

        extractedData = new byte[BUF_SIZE];
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData, 0, compressedData.length);
        while (! inflater.needsInput())
        {
            if (inflater.getTotalOut() >= extractedData.length)
            {
                extractedData = Arrays.copyOf(extractedData,
                        extractedData.length + BUF_SIZE);
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
                extractedData = null;
                return;
            }
        }
        if (inflater.getTotalOut() != extractedData.length)
        {
            extractedData = Arrays.copyOf(extractedData,
                    inflater.getTotalOut());
        }
    }

    /**
     *  Gets data about this map chunk.
     *
     * @param pos  The map coordinate to be saved with the chunk.
     *
     * @return     The chunk data object.
     */
    public ChunkData getChunkData(Point pos)
    {
        ByteStream chunkStream = new ByteStream(extractedData);
        
        // Data access convenience functions:
        // Read a name string from NBT data:
        Function<Boolean, String> readName = (isNamed)->
        {
            if (! isNamed)
            {
                return "";
            }
            try
            {
                short nameLength = chunkStream.readShort();
                if (nameLength > 0)
                {
                    byte[] stringBytes = chunkStream.readBytes(nameLength);
                    return new String(stringBytes);
                }
            }
            catch (IOException e)
            {
                System.err.println("ChunkData.getChunkData(Point):"
                        + " this IOException shouldn't be possible.");
                System.err.println("Exception: " + e.getMessage());
                System.exit(1);
            }
            return "";
        };
        
        // Skip past a basic value with a known byte size, named or unnamed.
        BiConsumer<Integer, Boolean> skipValue = (numBytes, isNamed)->
        {
            try
            {
                int toSkip = numBytes;
                if (isNamed)
                {
                    toSkip += chunkStream.readShort();
                }
                chunkStream.skip(toSkip);         
            }
            catch (IOException e)
            {
                System.err.println("ChunkData.getChunkData(Point): "
                        + "IOException when skipping a" 
                        + (isNamed ? " named " : "n unnamed ")
                        + "value of size " + numBytes + ".");
                System.err.println("Exception: " + e.getMessage());
                System.exit(1);
            }
            
        };
        
        Deque<String> openTags = new ArrayDeque();
        // Holds shared data in an object that Consumer functions can access.
        class DataState
        {
            public long lastUpdate;
            public long inhabitedTime;
            public boolean inBiomeList;
            public boolean inStructureRefs;
            public Structure currentStruct = Structure.UNKNOWN;
            public String currentStructName;
            
            public DataState()
            {
                currentStruct = Structure.UNKNOWN;
                currentStructName = "";
            }
        }
        DataState dataState = new DataState();
        ArrayList<Biome> biomes = new ArrayList();
        Set<Structure> structures = new TreeSet();
        
        Map<NBTTag, Consumer<Boolean>> parseTag = new HashMap();
        // Define how each NBT tag type is parsed:
        parseTag.put(NBTTag.END, (isNamed)->
        {
            if (openTags.isEmpty())
            {
                return;
            }
            if (dataState.inStructureRefs)
            {
                if (openTags.peek().equals(Keys.STRUCT_REFS))
                {
                    dataState.inStructureRefs = false;
                }
                else if (openTags.peek().equals(dataState.currentStructName))
                {
                    dataState.currentStruct = Structure.UNKNOWN;
                    dataState.currentStructName = "";
                }
                openTags.pop();
            }
        });
        parseTag.put(NBTTag.BYTE, (isNamed)->
        {
            String name = readName.apply(isNamed);
            try
            {
                int byteVal = Byte.toUnsignedInt(chunkStream.readByte());
                if (dataState.inBiomeList)
                {
                    biomes.add(Biome.fromCode(byteVal));
                }
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
            } 
        });
        parseTag.put(NBTTag.SHORT, (isNamed)->
        {
            skipValue.accept(2, isNamed);
            if (dataState.inBiomeList)
            {
                System.err.println("Biome short?!?");
                System.exit(1);
            }
        });
        parseTag.put(NBTTag.INT, (isNamed)->
        {
            String name = readName.apply(isNamed);
            try
            {
                long intVal = Integer.toUnsignedLong(chunkStream.readInt());
                if (dataState.inBiomeList)
                {
                    biomes.add(Biome.fromCode((int) intVal));
                }
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
            } 
        });
        parseTag.put(NBTTag.LONG, (isNamed)->
        {
            String name = readName.apply(isNamed);
            long longVal;
            try
            {
                longVal = chunkStream.readLong();
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
                return;
            }     
            if(dataState.inStructureRefs 
                    && dataState.currentStruct != Structure.UNKNOWN 
                    && !isNamed)
            {
                structures.add(dataState.currentStruct);
            }
            if (name.isEmpty())
            {
                return;
            }
            if (name.equals(Keys.LAST_UPDATE))
            {
                dataState.lastUpdate = longVal;
            }
            else if (name.equals(Keys.INHABITED_TIME))
            {
                dataState.inhabitedTime = longVal;
            }
        });
        parseTag.put(NBTTag.FLOAT, (isNamed)->
        {
            skipValue.accept(4, isNamed);
        });
        parseTag.put(NBTTag.DOUBLE, (isNamed)->
        {
            skipValue.accept(8, isNamed);
        });
        parseTag.put(NBTTag.BYTE_ARRAY, (isNamed)->
        {
            String name = readName.apply(isNamed);
            if (name.equals(Keys.BIOME))
            {
                dataState.inBiomeList = true;
            }
            int length;
            try
            {
                length = chunkStream.readInt();
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
                return;
            }
            for (int i = 0; i < length; i++)
            {
                parseTag.get(NBTTag.BYTE).accept(false);
            }
            dataState.inBiomeList = false;
        });
        parseTag.put(NBTTag.STRING, (isNamed)->
        {
            try
            {
                for (int i = 0; i < (isNamed ? 2 : 1); i++)
                {
                    short length = chunkStream.readShort();
                    chunkStream.skip(length);
                }
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
        });
        parseTag.put(NBTTag.LIST, (isNamed)->
        {
            String name = readName.apply(isNamed);
            if (name.equals(Keys.BIOME))
            {
                dataState.inBiomeList = true;
            }
            try
            {
                NBTTag type = NBTTag.values()[Byte.toUnsignedInt(
                        chunkStream.readByte())];
                int length = chunkStream.readInt();
                for (int i = 0; i < length; i++)
                {
                    parseTag.get(type).accept(false);
                }
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            dataState.inBiomeList = false;
        });
        parseTag.put(NBTTag.COMPOUND, (isNamed)->
        {
            String name = readName.apply(isNamed);
            if (! openTags.isEmpty() && openTags.peek().equals(Keys.STRUCTURES)
                    && name.equals(Keys.STRUCT_REFS))
            {
                dataState.inStructureRefs = true;
            }
            openTags.push(name);
        });
        parseTag.put(NBTTag.INT_ARRAY, (isNamed)->
        {
            String name = readName.apply(isNamed);
            if (name.equals(Keys.BIOME))
            {
                dataState.inBiomeList = true;
            }
            int length;
            try
            {
                length = chunkStream.readInt();
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
                return;
            }
            for (int i = 0; i < length; i++)
            {
                parseTag.get(NBTTag.INT).accept(false);
            }
            dataState.inBiomeList = false;
        });
        parseTag.put(NBTTag.LONG_ARRAY, (isNamed)->
        {
            String name = readName.apply(isNamed);
            if (dataState.inStructureRefs 
                    && dataState.currentStructName.isEmpty())
            {
                dataState.currentStruct = Structure.parse(name);
                if (dataState.currentStruct != Structure.UNKNOWN)
                {
                    dataState.currentStructName = name;
                }
                else
                {
                    System.err.println("Found unknown structure " + name);
                }
            }
            try
            {
                int length = chunkStream.readInt();
                for (int i = 0; i < length; i++)
                {
                    parseTag.get(NBTTag.LONG).accept(false);
                }
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            dataState.currentStruct = Structure.UNKNOWN;
            dataState.currentStructName = "";
        });

        // Process tags until the buffer is empty:
        do
        {
            NBTTag type;
            try
            {
                type = NBTTag.values()[Byte.toUnsignedInt(
                        chunkStream.readByte())];
                parseTag.get(type).accept(true);
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        while(! openTags.isEmpty() && chunkStream.available() > 0);
        ChunkData chunk = new ChunkData(pos, (int) dataState.inhabitedTime,
                (int) dataState.lastUpdate);
        for (Biome biome : biomes)
        {
            chunk.addBiome(biome);
        }
        for (Structure structure : structures)
        {
            chunk.addStructure(structure);
        }
        return chunk;
    }

    // Uncompressed chunk data array:
    private byte[] extractedData;
    // Current index within uncompressed data:
    private int dataIndex;
}
