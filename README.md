# ChunkAtlas
Finds all loaded chunks in a minecraft region directory and saves them to an image file.

### Building ChunkAtlas
ChunkAtlas was created in NetBeans 11.1. The easiest way to rebuild the project is to open the project directory in Netbeans and select "Build Project" in the Run menu.

### Using ChunkAtlas
ChunkAtlas will work as both an executable jar and a Minecraft Spigot server plugin. To run ChunkAtlas directly from the command line, use `java -jar ChunkAtlas-0.1.jar [OPTIONS]`. Running `java -jar ChunkAtlas-0.1.jar --help` will list descriptions of all available options.

To run ChunkAtlas as a server plugin, place ChunkAtlas-0.1.jar in your server's plugins directory. Maps are generated automatically whenever the server restarts. 
