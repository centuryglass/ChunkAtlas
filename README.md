# MCMap
Finds all loaded chunks in a minecraft region directory and saves them to an image file.

### Building MCMap
MCMap was created in NetBeans 11.1. The easiest way to rebuild the project is to open the project directory in Netbeans and select "Build Project" in the Run menu.

### Using MCMap
MCMap will work as both an executable jar and a Minecraft Spigot server plugin. To run MCMap directly from the command line, use `java -jar MCMap-0.1.jar [OPTIONS]`. Running `java -jar MCMap-0.1.jar --help` will list descriptions of all available options.

To run MCMap as a server plugin, place MCMap-0.1.jar in your server's plugins directory. Maps are generated automatically whenever the server restarts. 
