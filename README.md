![ChunkAtlas](./banner.png?raw=true "ChunkAtlas: Create and view Minecraft server maps.")

ChunkAtlas finds all loaded chunks in a Minecraft region directory and uses them to create different types of maps. ChunkAtlas can function as either a standalone command line application, or as a Minecraft server plugin. Optionally, ChunkAtlas will upload map images to a [Node.js Express web server](https://github.com/centuryglass/ChunkAtlas-Viewer), which provides a browser-based HTML5 viewer for the uploaded map data. See the project [GitHub Pages](https://centuryglass.github.io/ChunkAtlas/) to view a demo of the web viewer with example map data.

### Building ChunkAtlas
ChunkAtlas uses Maven to load dependencies and compile the project. With Maven installed, running `mvn install` in the main project directory will build the project. Alternatively, open the project directory in Netbeans 11.1 or greater, and select "Build Project" in the Run menu.

### Using ChunkAtlas
ChunkAtlas will work as both an executable jar and a Minecraft Spigot server plugin. To run ChunkAtlas directly from the command line, use `java -jar ChunkAtlas-0.4-jar-with-dependencies.jar [OPTIONS]`. Running `java -jar ChunkAtlas-0.4-jar-with-dependencies.jar --help` will list descriptions of all available options.

To run ChunkAtlas as a Minecraft server plugin, place ChunkAtlas-0.4-jar-with-dependencies.jar into your server's plugins directory. Maps are generated automatically whenever the server restarts. 
