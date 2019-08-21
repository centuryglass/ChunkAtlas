/**
 * com.centuryglass.mcmap.mapping is responsible for converting Minecraft data
 * into map images.
 * 
 *  Each map variant is created by a different Mapper subclass. Mappers are
 * given Minecraft world data in the form of worldinfo.ChunkData objects. 
 * Mappers are responsible for choosing an appropriate color for that chunk,
 * which is then applied to a MapImage object. If mappers need to make
 * additional changes that aren't easily applied per chunk, they can directly
 * access their MapImages shortly before the maps are saved by overriding the
 * Mapper.finalProcessing(MapImage) method.
 * 
 *  To use the mapping package, either create individual Mapper subclass
 * objects, or create a MapCollector object to generate all map types at once.
 */
package com.centuryglass.mcmap.mapping;