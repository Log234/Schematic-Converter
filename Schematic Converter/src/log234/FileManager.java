package log234;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

import lightbulb.LightbulbTerminal;
import lightbulb.LightbulbTerminal.Severity;

public class FileManager {
    static LightbulbTerminal io;
    


    static List<File> getFiles() {
	HashMap<String, List<String>> mapping = new HashMap<String, List<String>>();
	ArrayList<String> extension = new ArrayList<String>();
	extension.add("*.schematic");
	mapping.put("Minecraft schematic", extension);

	io.println("Select one or more schematics files to be converted:");
	List<File> files = io.getFile(true, "Schematics file:", mapping);

	if (files != null && files.get(0) != null) {
	    return files;
	} else {
	    io.log(Severity.ERROR, "Cannot continue without a file!");
	    System.exit(0);
	    return null;
	}
    }
    
    static void saveFile(String result) {
	HashMap<String, List<String>> mapping = new HashMap<String, List<String>>();
	ArrayList<String> extension = new ArrayList<String>();
	extension.add("*.json");
	mapping.put("CS Blueprint", extension);

	io.println("Select where to save the blueprint:");
	File path = io.saveFile("Blueprint: ", mapping);

	io.println("Saving the file...");
	FileWriter fw;
	try {
	    fw = new FileWriter(path);
	    fw.write(result);
	    fw.flush();
	    fw.close();
	} catch (IOException e) {
	    io.log(Severity.ERROR, "Could not save file!");
	    io.log(Severity.ERROR, e.getLocalizedMessage());
	}
	io.println("Saved!");
    }
    
    static int[][][] parseSchematic(File schematic) {
	Tag<?> readTag = null;

	try {
	    NBTInputStream in = new NBTInputStream(new BufferedInputStream(new FileInputStream(schematic)));

	    readTag = in.readTag();
	    in.close();

	} catch (IOException e) {
	    io.log(Severity.ERROR, "Failed to parse the schematics file: " + schematic.getAbsolutePath());
	    io.log(Severity.ERROR, e.getLocalizedMessage());
	}

	CompoundMap content = (CompoundMap) readTag.getValue();
	short width = (short) content.get("Width").getValue();
	short height = (short) content.get("Height").getValue();
	short length = (short) content.get("Length").getValue();

	io.println("Width: " + width + ", Height: " + height + ", Length: " + length);

	int[][][] map = new int[width][height][length];

	byte[] blocks = (byte[]) content.get("Blocks").getValue();
	ArrayList<Integer> blockTypes = new ArrayList<Integer>();

	int index = 0;
	for (int y = 0; y < height; y++) {
	    for (int z = 0; z < length; z++) {
		for (int x = 0; x < width; x++) {
		    map[x][y][z] = blocks[index++];
		    if (!blockTypes.contains(map[x][y][z])) {
			blockTypes.add(map[x][y][z]);
		    }
		}
	    }
	}

	Collections.sort(blockTypes);

	io.println("Block IDs:");
	for (int id : blockTypes) {
	    io.println(id);
	}

	return map;
    }
}
