package log234;

public class ChunkGenerator {
    static void calculateChunk(int[][][] map, Chunk chunk) {
	getDepth(map, chunk);
	getHeight(map, chunk);
	getWidth(map, chunk);
	cleanChunk(map, chunk);
    }

    private static void getDepth(int[][][] map, Chunk chunk) {
	int id = map[chunk.x][chunk.y][chunk.z];
	int i = 1;

	for (int z = chunk.z; z + i < map[0][0].length; i++) {
	    if (map[chunk.x][chunk.y][z + i] != id) {
		chunk.setDepth(i);
		return;
	    }
	}
	chunk.setDepth(i);
    }

    private static void getHeight(int[][][] map, Chunk chunk) {
	int id = map[chunk.x][chunk.y][chunk.z];
	int i = 1;

	for (int y = chunk.y; y + i < map[0].length; i++) {
	    for (int z = chunk.z; z < chunk.z + chunk.width; z++) {
		if (map[chunk.x][y + i][z] != id) {
		    chunk.setHeight(i);
		    return;
		}
	    }
	}
	chunk.setHeight(i);
    }

    private static void getWidth(int[][][] map, Chunk chunk) {
	int id = map[chunk.x][chunk.y][chunk.z];
	int i = 1;

	for (int x = chunk.x; x + i < map.length; i++) {
	    for (int y = chunk.y; y < chunk.y + chunk.height; y++) {
		for (int z = chunk.z; z < chunk.z + chunk.depth; z++) {
		    if (map[x + i][y][z] != id) {
			chunk.setWidth(i);
			return;
		    }
		}
	    }
	}
	chunk.setWidth(i);
    }

    private static void cleanChunk(int[][][] map, Chunk chunk) {
	for (int x = chunk.x; x < chunk.x + chunk.width; x++) {
	    for (int y = chunk.y; y < chunk.y + chunk.height; y++) {
		for (int z = chunk.z; z < chunk.z + chunk.depth; z++) {
		    map[x][y][z] = -1;
		}
	    }

	}
    }
}
