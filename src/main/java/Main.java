import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<Employee> parseXML(String filename) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> empList = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("data.xml"));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Employee emp = new Employee();
                NodeList childNodes = node.getChildNodes();

                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node cNode = childNodes.item(j);
                    if (cNode instanceof Element) {
                        String content = cNode.getLastChild().getTextContent();

                        switch (cNode.getNodeName()) {
                            case "id":
                                emp.setId(Long.parseLong(content));
                                break;
                            case "firstName":
                                emp.setFirstName(content);
                                break;
                            case "lastName":
                                emp.setLastName(content);
                                break;
                            case "country":
                                emp.setCountry(content);
                                break;
                            case "age":
                                emp.setAge(Integer.parseInt(content));
                                break;
                        }
                    }
                }
                empList.add(emp);
            }
        }
        return empList;


    }

    // Создаём метод для получения списка сотрудников. Конструктор принимает массив сторк, и имя файла
    public static List<Employee> parseCSV(String[] columnMapping, String filename) {
        // Создаём объект CSVReader, предназначен для чтения CSV-файла из Java-кода. Передаём в его конструктор файловый
        // ридер
        try (CSVReader csvReader = new CSVReader(new FileReader(filename))) {
            //  ColumnPositionMappingStrategy используется CsvToBean  для импорта CSV-данных,
            // если требуется сопоставление полей CSV с полями Java класса.
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            // указываем тип
            strategy.setType(Employee.class);
            // тип колонок
            strategy.setColumnMapping(columnMapping);
            // создаём экземпляр CsvToBean с использованием билдера CsvToBeanBuilder, используем обьект стратегии
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            // возвращаем список сотрудников
            return csv.parse();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    public static void writeString(String list, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(list);
            file.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> listCSV = parseCSV(columnMapping, fileName);

        String jsonCSV = listToJson(listCSV);

        writeString(jsonCSV, "data.json");

        List<Employee> listXML = parseXML("data.xml");

        String jsonStringXML = listToJson(listXML);

        writeString(jsonStringXML, "data2.json");

    }
}
