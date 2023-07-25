import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CurrencyRatesApp {
    private static final String API_URL = "http://www.cbr.ru/scripts/XML_daily.asp";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter currency code (e.g., USD): ");
        String code = scanner.nextLine();

        System.out.print("Enter date in the format yyyy-MM-dd (e.g., 2022-10-08): ");
        String dateStr = scanner.nextLine();

        scanner.close();

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = inputDateFormat.parse(dateStr);
            String formattedDate = outputDateFormat.format(date);
            String apiUrl = API_URL + "?date_req=" + formattedDate;

            String xmlData = fetchDataFromApi(apiUrl);

            String rate = getCurrencyInfo(xmlData, code, "Value");
            String currencyName = getCurrencyInfo(xmlData, code, "Name");

            if (rate != null && currencyName != null) {
                System.out.println(code + " (" + currencyName + "): " + rate);
            } else {
                System.out.println("Currency code not found in the data.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String fetchDataFromApi(String apiUrl) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String getCurrencyInfo(String xmlData, String code, String tagName) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(xmlData.getBytes("windows-1251")));
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("Valute");

        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node node = nodeList.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String valuteCode = element.getElementsByTagName("CharCode").item(0).getTextContent();
                if (valuteCode.equals(code)) {
                    return element.getElementsByTagName(tagName).item(0).getTextContent();
                }
            }
        }
        return null;
    }
}
