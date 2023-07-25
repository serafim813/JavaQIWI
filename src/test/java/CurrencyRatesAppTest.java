import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class CurrencyRatesAppTest {
    private InputStream originalSystemIn;
    private final String expectedRate = "61,2475";
    private final String expectedCurrencyName = "Доллар США";

    @Before
    public void setUp() {
        originalSystemIn = System.in;
    }

    @After
    public void tearDown() {
        System.setIn(originalSystemIn);
    }

    @Test
    public void testValidCurrencyAndDate() throws Exception {
        String code = "USD";
        String dateStr = "2022-10-08";

        // Prepare the input stream with the user's input
        String input = code + System.lineSeparator() + dateStr + System.lineSeparator();
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        CurrencyRatesApp.main(new String[]{"--code=" + code, "--date=" + dateStr});

        // Restore the original System.in
        System.setIn(new ByteArrayInputStream(originalSystemIn.toString().getBytes()));
    }

    @Test
    public void testInvalidCurrencyCode() throws Exception {
        String code = "XYZ"; // Assuming XYZ is an invalid currency code
        String dateStr = "2022-10-08";

        // Prepare the input stream with the user's input
        String input = code + System.lineSeparator() + dateStr + System.lineSeparator();
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        CurrencyRatesApp.main(new String[]{"--code=" + code, "--date=" + dateStr});

        // Restore the original System.in
        System.setIn(new ByteArrayInputStream(originalSystemIn.toString().getBytes()));
    }

    @Test
    public void testInvalidDateFormat() throws Exception {
        String code = "USD";
        String dateStr = "2022/10/08"; // Invalid date format

        // Prepare the input stream with the user's input
        String input = code + System.lineSeparator() + dateStr + System.lineSeparator();
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        CurrencyRatesApp.main(new String[]{"--code=" + code, "--date=" + dateStr});

        // Restore the original System.in
        System.setIn(new ByteArrayInputStream(originalSystemIn.toString().getBytes()));
    }

    @Test
    public void testMissingArguments() throws Exception {
        String code = "USD";
        String dateStr = "2022-10-08";

        // Prepare the input stream with the user's input
        String input = code + System.lineSeparator() + dateStr + System.lineSeparator();
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        CurrencyRatesApp.main(new String[]{"--code=" + code, "--date=" + dateStr});

        // Restore the original System.in
        System.setIn(new ByteArrayInputStream(originalSystemIn.toString().getBytes()));
    }

    @Test
    public void testFetchDataFromApi() throws Exception {
        String apiUrl = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=2022-10-08"; // Correct date format

        String xmlData = CurrencyRatesApp.fetchDataFromApi(apiUrl);

        // Since the API response may vary, we'll check if the expected data (USD currency) is present in the actual response.
        assertTrue(xmlData.contains("USD"));
        assertTrue(xmlData.contains("Доллар США"));
        assertTrue(xmlData.contains("61,2475"));
    }

    @Test
    public void testGetCurrencyInfo() throws Exception {
        String xmlData = "<?xml version=\"1.0\" encoding=\"windows-1251\"?><ValCurs><Valute><CharCode>USD</CharCode><Value>61,2475</Value><Name>Доллар США</Name></Valute></ValCurs>";
        String code = "USD";

        String rate = CurrencyRatesApp.getCurrencyInfo(xmlData, code, "Value");
        String currencyName = CurrencyRatesApp.getCurrencyInfo(xmlData, code, "Name");

        assertEquals(expectedRate, rate);
        assertEquals(expectedCurrencyName, currencyName);
    }

    @Test
    public void testGetCurrencyInfoWithInvalidCode() throws Exception {
        String xmlData = "<?xml version=\"1.0\" encoding=\"windows-1251\"?><ValCurs><Valute><CharCode>USD</CharCode><Value>61,2475</Value><Name>Доллар США</Name></Valute></ValCurs>";
        String code = "EUR"; // Assuming EUR is an invalid currency code

        String rate = CurrencyRatesApp.getCurrencyInfo(xmlData, code, "Value");
        String currencyName = CurrencyRatesApp.getCurrencyInfo(xmlData, code, "Name");

        assertNull(rate);
        assertNull(currencyName);
    }
}
