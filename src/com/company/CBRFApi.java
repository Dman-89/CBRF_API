package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class CBRFApi {

    public static void main(String[] args) throws UnsupportedEncodingException {

        String output;
        if ((output = getAndReadData("http://www.cbr.ru/scripts/XML_daily.asp")) == null)
            throw new RuntimeException("Something went wrong");
        Document document;
        if ((document = loadXMLFromString(output)) == null)
            throw new RuntimeException("Something went wrong");

        // Getting root element
        Node root = document.getDocumentElement();
        NodeList currencies = root.getChildNodes();

        // just for further reference
        Node usDollar;
        Node euro;
        for (int i = 0; i < currencies.getLength(); i++) {
            Node currency = currencies.item(i);
            String key = currency.getAttributes().getNamedItem("ID").getNodeValue();

            if (key.equals("R01235") || key.equals("R01239")) {
                if (key.equals("R01235"))
                    usDollar = currency;
                else if (key.equals("R01239"))
                    euro = currency;

                NodeList currencySpecs = currency.getChildNodes();
                System.out.print(currencySpecs.item(1).getTextContent());
                System.out.print(" " + currencySpecs.item(3).getTextContent());
                System.out.print(" " + currencySpecs.item(4).getTextContent());
                System.out.println();
            }
      }
//        // print all currencies
//        for (int i = 0; i < currencies.getLength(); i++) {
//            Node currency = currencies.item(i);
//            if (currency.getNodeType() != Node.TEXT_NODE) {
//                NodeList currencySpecs = currency.getChildNodes();
//                System.out.print(currencySpecs.item(1).getTextContent());
//                System.out.print(" " + currencySpecs.item(3).getTextContent());
//                System.out.print(" " + currencySpecs.item(4).getTextContent());
//                System.out.println();
//            }
//        }

    }

    public static String getAndReadData(String link) {

        String output = "";

        try {

            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.2.2; en-us; SAMSUNG GT-I9505 Build/JDQ39) " +
                    "AppleWebKit/535.19 (KHTML, like Gecko) Version/1.0 Chrome/18.0.1025.308 Mobile Safari/535.19.");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), Charset.forName("CP1251"));

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                output += line;
            }

            conn.disconnect();

            return output;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Document loadXMLFromString(String xml)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xml));
            // Creating DOM document tree
            return builder.parse(inputSource);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}