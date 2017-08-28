
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

import lightbulb.LightbulbTerminal;
import lightbulb.LightbulbTerminal.Severity;

public class FileManager {
    static LightbulbTerminal io;

    static void verifyFiles() {
	File blockIDs = new File("BlockIDs.json");
	if (!blockIDs.exists()) {
	    try {
		URL inputUrl = Main.class.getResource("src/BlockIDs.json");
		if (inputUrl == null) {
		    inputUrl = Main.class.getResource("BlockIDs.json");
		}
		FileUtils.copyURLToFile(inputUrl, blockIDs);
	    } catch (IOException e) {
		io.log(Severity.ERROR, "Unable to create BlockIDs file!");
		io.log(Severity.ERROR, e.getLocalizedMessage());
		io.pausedExit(0);
	    }
	    io.println("Created a file for customizing how to translate the block IDs: BlockIDs.json");
	    io.pausedExit(0);
	}
    }

    static HashMap<Integer, String> getBlockIDs() {
	HashMap<Integer, String> blockIDs = new HashMap<Integer, String>();
	verifyFiles();

	JSONParser parser = new JSONParser();

	try {
	    JSONObject obj = (JSONObject) parser.parse(new FileReader(new File("BlockIDs.json")));
	    for (Object objElem : obj.entrySet()) {
		@SuppressWarnings("unchecked")
		Entry<String, String> entry = (Entry<String, String>) objElem;
		int intID = Integer.parseInt(entry.getKey());
		String strID = entry.getValue();

		if (!strID.equals("")) {
		    blockIDs.put(intID, strID);
		}
	    }

	} catch (IOException e) {
	    io.log(Severity.ERROR, "An error occurred while trying to read BlockIDs.json");
	    io.log(Severity.ERROR, e.getLocalizedMessage());
	} catch (ParseException e) {
	    io.log(Severity.ERROR, "An error occurred while trying to parse BlockIDs.json");
	    io.log(Severity.ERROR, e.getLocalizedMessage());
	}

	return blockIDs;
    }

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
	    io.pausedExit(0);
	    return null;
	}
    }

    static void saveFile(String[] result) {
	HashMap<String, List<String>> mapping = new HashMap<String, List<String>>();
	ArrayList<String> extension = new ArrayList<String>();
	extension.add("*.json");
	mapping.put("CS Blueprint", extension);

	if (result.length == 1) {
	    io.println("Select where to save the blueprint:");
	} else {
	    io.println("Select where to save the blueprints:");
	}
	File path = io.saveFile("Blueprint: ", mapping);

	if (path == null) {
	    io.log(Severity.ERROR, "Save location not specified!");
	    io.pausedExit(0);
	}

	if (result.length == 1) {
	    io.println("Saving the file...");

	    Path filePath = Paths.get(path.getAbsolutePath());
	    String folder = filePath.getParent().toString();
	    String name = filePath.getFileName().toString();
	    name = name.toLowerCase().replace(' ', '_');

	    FileWriter fw;
	    try {
		fw = new FileWriter(new File(folder + "/" + name));
		fw.write(result[0]);
		fw.flush();
		fw.close();
		io.println("Saved: " + folder + "/" + name);
	    } catch (IOException e) {
		io.log(Severity.ERROR, "Could not save file!");
		io.log(Severity.ERROR, e.getLocalizedMessage());
		io.pausedExit(0);
	    }
	} else {
	    io.println("Saving the files...");

	    Path filePath = Paths.get(path.getAbsolutePath());
	    String folder = filePath.getParent().toString();
	    String name = FilenameUtils.removeExtension(filePath.getFileName().toString());
	    name = name.toLowerCase().replace(' ', '_');

	    FileWriter fw;
	    try {
		for (int i = 0; i < result.length; i++) {
		    fw = new FileWriter(new File(folder + "/" + name + "_pt" + (i + 1) + ".json"));
		    fw.write(result[i]);
		    fw.flush();
		    fw.close();
		    io.println("Saved: " + folder + "/" + name + "_pt" + (i + 1) + ".json");
		}
	    } catch (IOException e) {
		io.log(Severity.ERROR, "Could not save file!");
		io.log(Severity.ERROR, e.getLocalizedMessage());
		io.pausedExit(0);
	    }
	}
	io.println("Saved!");
    }

    static int[][][][] parseSchematic(File schematic) {
	int[][][] map;
	int[][][] data;
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
	short depth = (short) content.get("Length").getValue();

	io.println("Dimensions - Width: " + width + ", Length: " + depth + ", Height: " + height);
	map = new int[width][height][depth];
	data = new int[width][height][depth];

	byte[] dataBlocks = (byte[]) content.get("Data").getValue();

	byte[] blocks = (byte[]) content.get("Blocks").getValue();

	int index = 0;
	for (int y = 0; y < height; y++) {
	    for (int z = 0; z < depth; z++) {
		for (int x = 0; x < width; x++) {
		    map[x][y][z] = blocks[index];
		    data[x][y][z] = dataBlocks[index++];
		}
	    }
	}

	int[][][][] maps = { map, data };

	return maps;
    }
}
