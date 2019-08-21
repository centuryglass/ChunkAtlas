/**
 * com.centuryglass.mcmap.savedata is responsible for directly reading and
 * interpreting Minecraft region data files.
 * 
 *  The savedata package should be accessed through the MCAFile class, used to
 * load each "r.x.z.mca" file in the region file directory. MCAFile processes
 * these files and returns a list of worldinfo.ChunkData objects describing
 * each 16 x 16 block chunk within that region file.
 */
package com.centuryglass.mcmap.savedata;