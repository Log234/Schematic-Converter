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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

import javafx.scene.paint.Color;
import lightbulb.LightbulbTerminal;

public class SchematicConverter implements Runnable {
    LightbulbTerminal io;
    List<File> schematics;

    @Override
    public void run() {
	io = Main.terminal;
	io.setPrintLines(true, Color.CORNFLOWERBLUE);
	io.setDefaultStrPrefix("Schematics Converter >");

	getFile();

	for (File schematic : schematics) {
	    io.println("\nCurrent schematic: " + schematic.getName());
	    
	    io.println("What do you want to name the blueprint?");
	    String name = io.readLine("Name:");
	    
	    int[][][] map = parseSchematic(schematic);
	    ArrayList<Chunk> chunks = generateChunks(map);
	    convertIDs(chunks);
	    String result = convertToJson(chunks, name);
	    saveFile(result);
	}
    }

    private void getFile() {
	HashMap<String, List<String>> mapping = new HashMap<String, List<String>>();
	ArrayList<String> extension = new ArrayList<String>();
	extension.add("*.schematic");
	mapping.put("Minecraft schematic", extension);

	io.println("Select one or more schematics files to be converted:");
	List<File> files = io.getFile(true, "Schematics file:", mapping);

	if (files != null && files.get(0) != null) {
	    schematics = files;
	} else {
	    io.println("Cannot continue without a file!");
	    System.exit(0);
	}
    }

    private void saveFile(String result) {
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
	    io.println("Could not save file!");
	    io.println(e.getLocalizedMessage());
	}
	io.println("Saved!");
    }

    private int[][][] parseSchematic(File schematic) {
	Tag<?> readTag = null;

	try {
	    NBTInputStream in = new NBTInputStream(new BufferedInputStream(new FileInputStream(schematic)));

	    readTag = in.readTag();
	    in.close();

	} catch (IOException e) {
	    io.println(e.getLocalizedMessage());
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
    
    private ArrayList<Chunk> generateChunks(int[][][] map) {
	ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	
	for (int y = 0; y < map[0].length; y++) {
	    for (int z = 0; z < map[0][0].length; z++) {
		for (int x = 0; x < map.length; x++) {
		    Chunk chunk = new Chunk(map[x][y][z], x, y, z);
		    chunks.add(chunk);
		}
	    }
	}
	
	return chunks;
    }
    
    private void convertIDs(ArrayList<Chunk> chunks) {
	for (Chunk chunk: chunks) {
	    switch (chunk.intBlockId) {
	    case 0:
		break;

	    case 2:
		chunk.strBlockId = "grass";
		break;

	    case 3:
		chunk.strBlockId = "dirt";
		break;

	    case 4:
		chunk.strBlockId = "stonebricks";
		break;

	    case 5:
		chunk.strBlockId = "planks";
		break;

	    case 17:
		chunk.strBlockId = "logtemperate";
		break;

	    case 53:
		chunk.strBlockId = "planks";
		break;

	    case 54:
		chunk.strBlockId = "crate";
		break;

	    case 58:
		chunk.strBlockId = "workbench";
		break;

	    case 61:
		chunk.strBlockId = "stonebricks";
		break;

	    case 67:
		chunk.strBlockId = "stonebricks";
		break;

	    case 109:
		chunk.strBlockId = "stonebricks";
		break;

	    case 126:
		chunk.strBlockId = "planks";
		break;

	    default:
		break;
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private String convertToJson(ArrayList<Chunk> chunks, String name) {
	JSONObject obj = new JSONObject();
	JSONObject localization = new JSONObject();
	localization.put("en-US", name);
	obj.put("localization", localization);

	JSONArray chunkList = new JSONArray();
	for (Chunk chunk: chunks) {
	    chunkList.add(chunk.getJSON());
	}
	
	obj.put("blocks", chunkList);

	return obj.toString();

    }

}
