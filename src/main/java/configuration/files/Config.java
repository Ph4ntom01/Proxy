package configuration.files;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import configuration.constants.Folder;

public class Config {

    private String file;
    private String path;

    public Config(String file) {
        this.file = "/" + file;
        path = Folder.RESOURCES.getName() + Folder.CONFIG.getName() + this.file;
    }

    public String getValue(String key) {
        String value = "";
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) obj;
            value = (String) jsonObject.get(key);
        } catch (IOException | ParseException e) {
        }
        return value;
    }

    public JSONArray getArray(String arrayKey) {
        JSONArray array = null;
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) obj;
            array = (JSONArray) jsonObject.get(arrayKey);
        } catch (IOException | ParseException e) {
        }
        return array;
    }

}