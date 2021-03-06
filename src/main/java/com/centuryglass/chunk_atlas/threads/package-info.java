/**
 * com.centuryglass.chunk_atlas.threads is responsible for dividing up the work
 * needed to process Minecraft region files and generate map data. This package
 * should only need to be used directly by
 * com.centuryglass.chunk_atlas.MapCreator.
 * 
 *  When reading region data and generating map files, ChunkAtlas creates
 * objects in the threads package to handle different tasks simultaneously.
 * ReaderThread objects extract data from a subset of all region files, while a
 * single MapperThread object passes the resulting data to a MapCollector
 * object, and a ProgressThread object tracks and prints out the number of
 * region files processed.
 */
package com.centuryglass.chunk_atlas.threads;
