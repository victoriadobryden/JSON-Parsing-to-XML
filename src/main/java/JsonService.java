import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonService {
    private final ObjectMapper mapper;
    private final StatisticsGenerator statsGenerator;

    public JsonService(StatisticsGenerator statsGenerator) {
        this.mapper = new ObjectMapper();
        this.statsGenerator = statsGenerator;
    }

    public List<Case> parseCases(File jsonFile, String attribute) throws IOException {
        List<Case> cases = new ArrayList<>();
        JsonFactory factory = mapper.getFactory();
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile));
             JsonParser parser = factory.createParser(br)) {

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IOException("Expected data to start with an Array");
            }

            while (parser.nextToken() != JsonToken.END_ARRAY) {
                Case aCase = mapper.readValue(parser, Case.class);
                cases.add(aCase);
            }
        }
        return cases;
    }


    public List<Case> checkAndParseJson(String path, String attribute) throws IOException {
        List<Case> allCases = new ArrayList<>();
        File jsonFolder = new File(path);
        if (jsonFolder.exists() && jsonFolder.isDirectory()) {
            File[] jsonFiles = jsonFolder.listFiles((dir, name) -> name.endsWith(".json"));
            if (jsonFiles != null) {
                for (File jsonFile : jsonFiles) {
                    allCases.addAll(parseCases(jsonFile, attribute));
                }
            } else {
                System.out.println("No JSON files found in the directory.");
            }
        } else {
            System.out.println("The provided path does not exist or is not a directory.");
        }
        return allCases;
    }
}

