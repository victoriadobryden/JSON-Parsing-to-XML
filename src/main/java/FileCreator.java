import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class FileCreator {

    public void createMultipleJsonFiles(String directoryPath, int numberOfFiles) throws IOException {
        for (int i = 0; i < numberOfFiles; i++) {
            createHugeFile(directoryPath + "/dataFile_" + i + ".json");
        }
    }
    private static void createHugeFile(String path) throws IOException {
        JsonFactory factory = new JsonFactory();
        File file = new File(path);
        JsonGenerator generator = factory.createGenerator(new FileWriter(file));
        Random random = new Random();

        generator.writeStartArray();

        for (int i = 0; i < 1000; i++) {
            generator.writeStartObject();
            generator.writeStringField("date", "2024-01-" + String.format("%02d", random.nextInt(28) + 1));
            generator.writeStringField("placeOfEvent", "Place " + random.nextInt(100));
            generator.writeNumberField("investigatorId", random.nextInt(10) + 1);
            generator.writeArrayFieldStart("namesOfVictims");
            for (int j = 0; j < random.nextInt(5) + 1; j++) {
                generator.writeString("Victim " + random.nextInt(1000));
            }
            generator.writeEndArray();
            generator.writeArrayFieldStart("charges");
            for (int k = 0; k < random.nextInt(3) + 1; k++) {
                generator.writeString("Charge " + random.nextInt(100));
            }
            generator.writeEndArray();
            generator.writeEndObject();
        }

        generator.writeEndArray(); // end of JSON array
        generator.close(); // close the generator to flush the content to file and free resources
    }


}
