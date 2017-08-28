


import org.json.simple.JSONObject;

public class Chunk implements Comparable<Chunk> {
    // ID
    String blockID = null;
    
    // Location
    int x = -1;
    int y = -1;
    int z = -1;
    
    // Size
    int height = 1;
    int width = 1;
    int depth = 1;
    
    Chunk(String id, int x, int y, int z) {
	blockID = id;
	this.x = x;
	this.y = y;
	this.z = z;
    }
    
    void setHeight (int height) {
	this.height = height;
    }
    
    void setWidth (int width) {
	this.width = width;
    }
    
    void setDepth (int depth) {
	this.depth = depth;
    }
    
    int getSize() {
	return height * width * depth;
    }
    
    @SuppressWarnings("unchecked")
    JSONObject getJSON() {
	JSONObject obj = new JSONObject();
	obj.put("x", x);
	obj.put("y", y);
	obj.put("z", z);
	obj.put("typename", blockID);
	
	if (height != 1) {
	    obj.put("height", height);
	}
	
	if (width != 1) {
	    obj.put("width", width);
	}
	
	if (depth != 1) {
	    obj.put("depth", depth);
	}
	
	return obj;
    }

    @Override
    public int compareTo(Chunk chunkB) {
	return this.y - chunkB.y;
    }
}
