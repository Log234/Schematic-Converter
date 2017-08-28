
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    int schematicSize;

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
	    int[][][][] maps = FileManager.parseSchematic(schematic);
	    int[][][] map = maps[0];
	    int[][][] data = maps[1];

	    io.println("Calculating chunks...");
	    ArrayList<Chunk> chunks = generateChunks(map, data);
	    io.println("Blocks: " + schematicSize + ", Chunks: " + chunks.size());
	    List<ArrayList<Chunk>> workers = divideWork(chunks);
	    io.println("Converting to JSON...");
	    String[] result = convertToJson(workers, name);
	    FileManager.saveFile(result);
	}
    }

    private ArrayList<Chunk> generateChunks(int[][][] map, int[][][] data) {
	io.println(
		"Choose a height offset. This is so that you can adjust for schematics that includes part of the ground or that are supposed to fly above ground.");
	int yOffset = io.readInt("Offset:", -100, 100);
	ArrayList<Chunk> chunks = new ArrayList<Chunk>();

	for (int y = 0; y < map[0].length; y++) {
	    for (int x = 0; x < map.length; x++) {
		for (int z = 0; z < map[0][0].length; z++) {
		    if (blockIDs.containsKey(map[x][y][z])) {
			String metadata = getMetadata(map[x][y][z], data[x][y][z]);
			if (!metadata.equals("FOOT")) {
			    Chunk chunk = new Chunk(blockIDs.get(map[x][y][z]) + metadata, x, y, z);
			    ChunkGenerator.calculateChunk(map, data, chunk);
			    chunk.y += yOffset;
			    chunks.add(chunk);
			    schematicSize += chunk.getSize();
			}
		    }
		}
	    }
	}

	return chunks;
    }

    static String getMetadata(int block, int data) {
	switch (block) {
	// Beds
	case 26:
	    if (getBit(data, 3)) {
		if (getBit(data, 0))
		    return "z-";
		if (getBit(data, 1))
		    return "x-";
		if (getBit(data, 2))
		    return "z+";
		return "x+";
	    } else {
		return "FOOT";
	    }

	    // Torches
	case 50:
	case 75:
	case 76:
	    switch (data) {
	    case 1:
		return "z+";

	    case 2:
		return "z-";

	    case 3:
		return "x-";

	    case 4:
		return "x+";

	    case 5:
		return "y+";
	    }

	    // Furnaces
	case 61:
	case 62:
	    switch (data) {
	    case 2:
	    default:
		return "x-";
	    case 3:
		return "x+";
	    case 4:
		return "z+";
	    case 5:
		return "z-";
	    }

	default:
	    return "";
	}
    }

    public static boolean getBit(int data, int position) {
	return ((data >> position) & 1) == 1;
    }

    private List<ArrayList<Chunk>> divideWork(ArrayList<Chunk> chunks) {
	// Sort chunks by size (From big to small)
	Collections.sort(chunks, (o1, o2) -> Integer.compare(o2.getSize(), o1.getSize()));

	ArrayList<ArrayList<Chunk>> workers = new ArrayList<ArrayList<Chunk>>();

	io.println("How many workers do you wish to divide the schematics between?");
	int division = io.readInt(1, 100);

	if (division == Integer.MIN_VALUE) {
	    workers.add(chunks);
	    return workers;
	}
	int[] work = new int[division];

	for (int i = 0; i < division; i++) {
	    workers.add(new ArrayList<Chunk>());
	}

	io.println("Dividing work...");

	for (Chunk chunk : chunks) {
	    int minimum = work[0];
	    int index = 0;

	    for (int i = 1; i < division; i++) {
		if (work[i] < minimum) {
		    minimum = work[i];
		    index = i;
		}
	    }
	    chunk.x -= index;
	    workers.get(index).add(chunk);
	    work[index] += chunk.getSize();
	}

	// Sort chunks by height (From low to high)
	for (ArrayList<Chunk> workerChunks : workers) {
	    Collections.sort(workerChunks);
	}

	return workers;
    }

    @SuppressWarnings("unchecked")
    private String[] convertToJson(List<ArrayList<Chunk>> workers, String name) {
	String[] files = new String[workers.size()];

	for (int i = 0; i < workers.size(); i++) {
	    ArrayList<Chunk> chunks = workers.get(i);

	    JSONObject obj = new JSONObject();
	    JSONObject localization = new JSONObject();

	    if (workers.size() == 1) {
		localization.put("en-US", name);
	    } else {
		localization.put("en-US", name + " part " + (i + 1));
	    }

	    obj.put("localization", localization);

	    JSONArray chunkList = new JSONArray();
	    for (Chunk chunk : chunks) {
		chunkList.add(chunk.getJSON());
	    }

	    obj.put("blocks", chunkList);
	    files[i] = obj.toJSONString();
	}
	return files;

    }

}
