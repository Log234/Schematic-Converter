package log234;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

	schematics = FileManager.getFiles();

	for (File schematic : schematics) {
	    io.println("\nCurrent schematic: " + schematic.getName());
	    
	    io.println("What do you want to name the blueprint?");
	    String name = io.readLine("Name:");
	    
	    int[][][] map = FileManager.parseSchematic(schematic);
	    ArrayList<Chunk> chunks = generateChunks(map);
	    convertIDs(chunks);
	    String result = convertToJson(chunks, name);
	    FileManager.saveFile(result);
	}
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
