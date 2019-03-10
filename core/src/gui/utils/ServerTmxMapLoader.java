package gui.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.maps.tiled.BaseTmxMapLoader.getTileIds;

public class ServerTmxMapLoader {

    private int width, height;

    public HashMap[][] load(String fileName) {
        String cwd = new File("").getAbsolutePath();
        XmlReader xmlReader = new XmlReader();
        FileHandle file = new FileHandle(new File(cwd + File.separator + fileName.replace("/", File.separator)));
        XmlReader.Element root = xmlReader.parse(file);
        width = Integer.parseInt(root.getAttribute("width"));
        height = Integer.parseInt(root.getAttribute("height"));
        System.out.println(width + " : " + height);
        XmlReader.Element tileset = root.getChildByName("tileset");
        int firstgid = Integer.parseInt(tileset.getAttribute("firstgid"));
        FileHandle tilesetFile = new FileHandle(new File(cwd + File.separator + "worlds" +  File.separator + tileset.getAttribute("source")));
        XmlReader.Element tilesetRoot = xmlReader.parse(tilesetFile);
        ArrayList<HashMap<String, Object>> tiles = new ArrayList<>();
        for (XmlReader.Element child : tilesetRoot.getChildrenByName("tile")) {
            HashMap<String, Object> properties = new HashMap<>();
            for (XmlReader.Element property : child.getChildByName("properties").getChildrenByName("property")) {
                String type = property.getAttribute("type");
                switch (type) {
                    case "bool":
                        if (property.getAttribute("value").equals("true"))
                            properties.put(property.getAttribute("name"), true);
                        else
                            properties.put(property.getAttribute("name"), false);
                        break;
                    case "int":
                        int value = Integer.parseInt(property.getAttribute("value"));
                        properties.put(property.getAttribute("name"), value);
                        break;
                }
            }
            tiles.add(properties);
        }
        int[] ids = getTileIds(root.getChildByName("layer"), width, height);
        HashMap[][] map = new HashMap[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int id = ids[y * width + x];
                map[x][y] = tiles.get(id - firstgid);
            }
        }
        return map;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
