import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class StatisticsGenerator {
    Map<String, Integer> stats = new HashMap<>();
    public List<Case> cases;
    Document xmlDoc;
    String xmlFilename;

    public void updateStatisticsWithCase(Case aCase, String attribute) {
        List<String> items = aCase.getAttributeValues(attribute);
        for (String item : items) {
            stats.merge(item, 1, Integer::sum); // Update statistics map
        }
    }

    public Document JsonToXML(JsonService jsonService, StatisticsGenerator statsGenerator, String path, String attribute) throws Exception {
        cases = jsonService.checkAndParseJson(path, attribute);
        stats = statsGenerator.generateStatistics(cases, attribute);
        xmlDoc = statsGenerator.createXmlDocument(stats);
        xmlFilename = path + "/statistics_by_" + attribute + ".xml";
        statsGenerator.writeXmlToFile(xmlDoc, xmlFilename);
        return xmlDoc;
    }

    public Map<String, Integer> generateStatistics(List<Case> cases, String attribute) {
        for (Case caseItem : cases) {
            List<String> items = caseItem.getAttributeValues(attribute);
            for (String item : items) {
                stats.merge(item, 1, Integer::sum);
            }
        }
        return stats;
    }

    public Document createXmlDocument(Map<String, Integer> stats) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // Root element
        Element rootElement = doc.createElement("statistics");
        doc.appendChild(rootElement);

        // Sort the entries by count
        List<Map.Entry<String, Integer>> list = new ArrayList<>(stats.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Create and append child elements
        for (Map.Entry<String, Integer> entry : list) {
            Element item = doc.createElement("item");
            rootElement.appendChild(item);

            Element value = doc.createElement("value");
            value.appendChild(doc.createTextNode(entry.getKey()));
            item.appendChild(value);

            Element count = doc.createElement("count");
            count.appendChild(doc.createTextNode(entry.getValue().toString()));
            item.appendChild(count);
        }

        return doc;
    }

    public void writeXmlToFile(Document doc, String filename) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filename));
        transformer.transform(source, result);
    }
}