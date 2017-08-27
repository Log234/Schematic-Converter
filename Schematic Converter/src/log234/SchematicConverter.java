package log234;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javafx.scene.paint.Color;
import lightbulb.LightbulbTerminal;

public class SchematicConverter implements Runnable {
    LightbulbTerminal io;
    List<File> schematics;
    HashMap<Integer, String> blockIDs;

    @Override
    public void run() {
	io = Main.terminal;
	io.setPrintLines(true, Color.CORNFLOWERBLUE);
	io.setDefaultStrPrefix("Schematics Converter >");
	FileManager.io = io;

	blockIDs = FileManager.getBlockIDs();
	schematics = FileManager.getFiles();

	for (File schematic : schematics) {
	    io.println("\nCurrent schematic: " + schematic.getName());

	    io.println("What do you want to name the blueprint?");
	    String name = io.readLine("Name:");

	    io.println("Parsing map...");
	    int[][][] map = FileManager.parseSchematic(schematic);
	    io.println("Calculating chunks...");
	    ArrayList<Chunk> chunks = generateChunks(map);
	    io.println("Converting to JSON...");
	    String result = convertToJson(chunks, name);
	    FileManager.saveFile(result);
	}
    }

    private ArrayList<Chunk> generateChunks(int[][][] map) {
	ArrayList<Chunk> chunks = new ArrayList<Chunk>();

	for (int y = 0; y < map[0].length; y++) {
	    for (int x = 0; x < map.length; x++) {
		for (int z = 0; z < map[0][0].length; z++) {
		    if (blockIDs.containsKey(map[x][y][z])) {
			Chunk chunk = new Chunk(blockIDs.get(map[x][y][z]), x, y, z);
			ChunkGenerator.calculateChunk(map, chunk);
			chunk.y--;
			chunks.add(chunk);
		    }
		}
	    }
	}

	return chunks;
    }

    @SuppressWarnings("unchecked")
    private String convertToJson(ArrayList<Chunk> chunks, String name) {
	JSONObject obj = new JSONObject();
	JSONObject localization = new JSONObject();
	localization.put("en-US", name);
	obj.put("localization", localization);

	JSONArray chunkList = new JSONArray();
	for (Chunk chunk : chunks) {
	    chunkList.add(chunk.getJSON());
	}

	obj.put("blocks", chunkList);

	return obj.toString();

    }

}
