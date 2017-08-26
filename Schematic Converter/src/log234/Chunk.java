package log234;

import org.json.simple.JSONObject;

public class Chunk {
    // ID
    int intBlockId = -1;
    String strBlockId = null;
    
    // Location
    int x = -1;
    int y = -1;
    int z = -1;
    
    // Size
    int height = -1;
    int width = -1;
    int depth = -1;
    
    Chunk(int id, int x, int y, int z) {
	intBlockId = id;
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
    
    void setDepth (int length) {
	this.depth = length;
    }
    
    @SuppressWarnings("unchecked")
    JSONObject getJSON() {
	JSONObject obj = new JSONObject();
	obj.put("x", x);
	obj.put("y", y);
	obj.put("z", z);
	obj.put("typename", strBlockId);
	
	if (height != -1) {
	    obj.put("height", height);
	}
	
	if (width != -1) {
	    obj.put("width", width);
	}
	
	if (depth != -1) {
	    obj.put("depth", depth);
	}
	
	return obj;
    }
}
