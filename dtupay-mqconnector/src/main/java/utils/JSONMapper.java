package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JSONMapper {
    public static String JSONToExceptionMessage(String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(message);
        if (node.has("error"))
            return node.get("error").asText();
        return null;
    }

    public static String[] JSONToArray(String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(message);
        return mapper.readValue(node.traverse(), new TypeReference<String[]>(){});
    }

    public static String arrayToJSON(String... collection) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(out, collection);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final byte[] data = out.toByteArray();
        return new String(data);
    }

    public static boolean JSONToBoolean(String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(message);
        return mapper.readValue(node.traverse(), new TypeReference<Boolean>(){});
    }
}
