
public class ChunkGenerator {
    static void calculateChunk(int[][][] map, int[][][] data, Chunk chunk) {
	getDepth(map, data, chunk);
	getWidth(map, data, chunk);
	getHeight(map, data, chunk);
	cleanChunk(map, data, chunk);
    }

    private static void getDepth(int[][][] map, int[][][] data, Chunk chunk) {
	String metadata = SchematicConverter.getMetadata(map[chunk.x][chunk.y][chunk.z], data[chunk.x][chunk.y][chunk.z]);
	int id = map[chunk.x][chunk.y][chunk.z];
	int i = 1;

	for (int z = chunk.z; z + i < map[0][0].length; i++) {
	    if (map[chunk.x][chunk.y][z + i] != id || !SchematicConverter.getMetadata(id, data[chunk.x][chunk.y][z + i]).equals(metadata)) {
		chunk.setDepth(i);
		return;
	    }
	}
	chunk.setDepth(i);
    }

    private static void getWidth(int[][][] map, int[][][] data, Chunk chunk) {
	String metadata = SchematicConverter.getMetadata(map[chunk.x][chunk.y][chunk.z], data[chunk.x][chunk.y][chunk.z]);
	int id = map[chunk.x][chunk.y][chunk.z];
	int i = 1;

	for (int x = chunk.x; x + i < map.length; i++) {
	    for (int z = chunk.z; z < chunk.z + chunk.depth; z++) {
		if (map[x + i][chunk.y][z] != id || !SchematicConverter.getMetadata(id, data[x + i][chunk.y][chunk.z]).equals(metadata)) {
		    chunk.setWidth(i);
		    return;

		}
	    }
	}
	chunk.setWidth(i);
    }

    private static void getHeight(int[][][] map, int[][][] data, Chunk chunk) {
	String metadata = SchematicConverter.getMetadata(map[chunk.x][chunk.y][chunk.z], data[chunk.x][chunk.y][chunk.z]);
	int id = map[chunk.x][chunk.y][chunk.z];
	int i = 1;

	for (int y = chunk.y; y + i < map[0].length; i++) {
	    for (int x = chunk.x; x < chunk.x + chunk.width; x++) {
		for (int z = chunk.z; z < chunk.z + chunk.depth; z++) {
		    if (map[x][y + i][z] != id || !SchematicConverter.getMetadata(id, data[chunk.x][y + i][chunk.z]).equals(metadata)) {
			chunk.setHeight(i);
			return;
		    }
		}
	    }
	}
	chunk.setHeight(i);
    }

    private static void cleanChunk(int[][][] map, int[][][] data, Chunk chunk) {
	for (int x = chunk.x; x < chunk.x + chunk.width; x++) {
	    for (int y = chunk.y; y < chunk.y + chunk.height; y++) {
		for (int z = chunk.z; z < chunk.z + chunk.depth; z++) {
		    map[x][y][z] = -1;
		}
	    }

	}
    }
}
