package configuration.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class Config {

    private String filePath;

    public Config(String filePath) {
        this.filePath = filePath;
    }

    public String getValue(String key) {
        String json = readFileAsString(filePath);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        return JsonPath.read(document, "$." + key);
    }

    public List<String> getValues(String request) {
        String json = readFileAsString(filePath);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        return JsonPath.read(document, "$." + request);
    }

    private String readFileAsString(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            return null;
        }
    }

}