import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private final JsonService jsonService;
    private final FileCreator fileCreator;
    private final StatisticsGenerator statsGenerator;


    public Main(JsonService jsonService, FileCreator fileCreator, StatisticsGenerator statsGenerator) {
        this.jsonService = jsonService;
        this.fileCreator = fileCreator;
        this.statsGenerator = statsGenerator;
    }

    public static void main(String[] args) throws Exception {
        StatisticsGenerator statsGenerator = new StatisticsGenerator();
        JsonService jsonService = new JsonService(statsGenerator);
        FileCreator fileCreator = new FileCreator();
        Main app = new Main(jsonService, fileCreator, statsGenerator);

        app.run();
    }

    public void run() throws Exception {
        Scanner scanner = new Scanner(System.in);
        Document xmlDoc = null;

        System.out.println("Choose an option:");
        System.out.println("1 - Create JSON files in a directory and parse them");
        System.out.println("2 - Parse existing JSON files in a directory");

        int choice = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter the directory path:");
        String path = scanner.nextLine();

        System.out.println("Enter the attribute name:");
        String attribute = scanner.nextLine();

        String result = switch (choice) {
            case 1 -> {
                System.out.println("Enter the number of files to create:");
                int numFiles = scanner.nextInt();
                try {
                    fileCreator.createMultipleJsonFiles(path, numFiles);
                    System.out.println(numFiles + " files have been created successfully in " + path);
                    // Parsing the newly created files to confirm or process the data
                    xmlDoc = statsGenerator.JsonToXML(jsonService, statsGenerator, path, attribute);
                    yield "Statistics have been generated.";
                } catch (IOException e) {
                    yield "Error has been occured: " + e.getMessage();
                }
            }
            case 2 -> {
                try {
                    statsGenerator.JsonToXML(jsonService, statsGenerator, path, attribute);
                    yield "Statistics have been generated.";
                } catch (IOException e) {
                    yield "Error parsing files: " + e.getMessage();
                }
            }
            default -> "Invalid option";
        };

        System.out.println(result);
        List<Case> cases = jsonService.checkAndParseJson(path, attribute);
        Map<String, Integer> stats = statsGenerator.generateStatistics(cases, attribute);

        // Optionally print XML to console
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult consoleResult = new StreamResult(System.out);
        transformer.transform(new DOMSource(xmlDoc), consoleResult);
        scanner.close();
    }
}

