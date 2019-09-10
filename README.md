# MCMap
Finds all loaded chunks in a minecraft region directory and saves them to an image file.

This is the original C++ implementation of the MCMap project. C++ was selected because
it is convenient and efficient, but the main project was ported to Java so it can also
function as a Minecraft Spigot server plugin. The Java branch has many features and
improvements that the C++ branch does not. These features may someday be back-ported to
the C++ branch, but it's fairly unlikely.

### Compiling MCMap
Make sure you have libpng, png++, and a C++ compiler that supports C++17. The makefile will only work in Linux or similar systems, but figuring out a cross-platform build system is probably pretty simple. If everything is set up correctly, running `make` should be enough to build the MCMap program.

### Using MCMap
`MCMap regionFolder imagePath.png [mapEdge][chunkPixels]`

- regionFolder: The path to a minecraft region folder. This should contain a bunch of .mca files.
- imagePath: The path where the image will be saved in PNG file format.
- mapEdge: Optional width/height in chunks of the world map, 3200 by default.
- chunkPixels: Optional width/height in pixels of each drawn chunk, 2 by default.
