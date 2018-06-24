package berlin.yuna.natsserver.util;

import berlin.yuna.natsserver.config.NatsServerConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
public class MetadataGenerator {

    //Ugly meta data generation for properties, makes it easier to get same description as from NatsServerConfig
    @Test
    public void generateMetadata() throws IOException {
        String sourceType = NatsServerConfig.class.getTypeName();

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"hints\": [],");
        json.append("\"groups\": [");
        json.append("{");
        json.append("\"sourceType\": \"").append(sourceType).append("\",");
        json.append("\"name\": \"nats.server\",");
        json.append("\"type\": \"").append(sourceType).append("\"");
        json.append("}");
        json.append("],");
        json.append("\"properties\": [");
        for (NatsServerConfig config : NatsServerConfig.values()) {
            String description = config.getDescription();
            String type = parseType(config.getDescription());
            json.append("{");
            json.append("\"sourceType\": ").append(sourceType).append(",");

            if (config.getDefaultValue() != null) {
                json.append("\"defaultValue\":");
                if (type.endsWith("String")) {
                    json.append("\"");
                }
                json.append(config.getDefaultValue());
                if (type.endsWith("String")) {
                    json.append("\"");
                }
                json.append(",");
            }

            json.append("\"name\": \"nats.server.").append(config.name().toLowerCase()).append("\",");
            json.append("\"type\": \"").append(type).append("\",");
            json.append("\"description\": \"").append(parseDescription(description)).append("\"");
            json.append("}");
            json.append(",");
        }
        json.delete(json.length() - 1, json.length());
        json.append("]");
        json.append("}");
        String metaDataFilePath = NatsServerConfig.class.getClassLoader().getResource("META-INF/spring-configuration-metadata.json").getPath();
        metaDataFilePath = metaDataFilePath.replace("target/classes", "src/main/resources");
        Files.write(Paths.get(metaDataFilePath), formatJson(json.toString()).getBytes());
        System.out.println(json);
    }

    private String formatJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(new JsonParser().parse(json));
    }

    private String parseDescription(final String description) {
        return description.substring(description.indexOf(']') + 1).trim().replace("\"", "\\\"");
    }

    private String parseType(final String description) {
        String goType = description.substring(1, description.indexOf(']')).trim();
        switch (goType) {
            case "INT":
                return "java.lang.Integer";
            case "SIZE":
                return "java.lang.Long";
            case "BOOL":
                return "java.lang.Boolean";
            default:
                return "java.lang.String";
        }
    }
}
