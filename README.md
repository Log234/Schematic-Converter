# Schematic-Converter

A tool for converting Minecraft Schematics into the blueprint format used by Colony Survival.

Download: https://github.com/Log234/Schematic-Converter/releases

### How to use it
#### First-time setup
0. Make sure you have Java installed
1. Download the latest version (The .jar file): https://github.com/Log234/Schematic-Converter/releases
2. Place it in an empty folder
3. Double-click the file to run it
4. This will generate a file named BlockIDs.json, you may edit this file to customize which blocks the program should convert between. Minecraft IDs on the left, Colony survival IDs on the right.

#### Usage
1. To start the program, simply double-click the .jar file
2. A file selection window will open. Here you may select one or more Minecraft Schematics files to convert into Colony Survival blueprints
3. The program will then ask you for a name for the blueprint. This is the name that will be showed in game for the en-US localization.
4. Now the program will ask you for a height offset. This allows you to choose move the blueprint up or down by a number of blocks, in case the blueprint includes a cellar, or is supposed to hang up in the air.
5. Now you are asked how many workers you will want to divide the blueprint between. This is in case you have a big blueprint that would take a long time to build for a single colonist. (More details on this later)
6. And finally it asks you where to save the file, please navigate to where your blueprints folder and give the file a name. (Your blueprints folder is generally located in "Steam\SteamApps\common\Colony Survival\gamedata\mods\Scarabol\Construction\blueprints")

(The program will slightly change the format of the file to fit the naming scheme used by Scarabol's Construction Mod)

#### Division of a blueprint on multiple workers
If you chose to divide the blueprint on more than one colonist there will be several files stored in the blueprints folder.
They will be named <span><</span>1The filename you chose<span>></span>1_ptX.json, where X is the part number.
Each of those files are a part of your blueprint, and they fit together like a puzzle.

Here is how to use them:
1. When you get in-game, you will notice that you have several new blueprints named <span><</span>1The blueprint name you chose<span>></span>1 part x. Where x is the part number.
2. Request them to be built, the same way as all other blueprints.
3. Here comes the important bit: To build the blueprint, place all the blueprint parts side-by-side, from part 1 to however many pieces you decided to split them into. If you place them in the right order and leave no gap in-between them, they should produce the full blueprint.
  
If you have any questions, don't hesitate asking me in the Colony Survival Discord server.
