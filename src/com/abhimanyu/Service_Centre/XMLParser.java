package com.abhimanyu.Service_Centre;

import java.util.List;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Xml;


/**
 * Created by Abhimanyu Jain on 30-12-2014.
 */
public class XMLParser {

    // We don't use namespaces
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "start");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Entry {
        public String name;
        public String address;
        public String phone;
        public String city;
        public String email;
        public String company_id;
        public ArrayList<String> products;

        private Entry(String name, String address, String  phone, String city, String email, String company_id, ArrayList<String> products) {
            this.name = name;
            this.address = address;
            this.phone = phone;
            this.city = city;
            this.email = email;
            this.company_id = company_id;
            this.products = products;
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String name = null;
        String address = null;
        String phone = null;
        String city = null;
        String email = null;
        String company_id = null;
        ArrayList<String> products = new ArrayList<String>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nam = parser.getName();
            if (nam.equals("name")) {
                name = readName(parser);
            } else if (nam.equals("address")) {
                address = readAddress(parser);
                System.out.println("address = "+address);
            } else if (nam.equals("phone")) {
                phone = readPhone(parser);
                System.out.println("phone = "+phone);
            } else if (nam.equals("city")) {
                city = readCity(parser);
                System.out.println("city = "+city);
            } else if (nam.equals("email")) {
                email = readEmail(parser);
                System.out.println("email = "+email);
            } else if (nam.equals("company_id")) {
                company_id = readCompany_id(parser);
            } else if (nam.equals("products")) {
                products = readProducts(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(name, address, phone, city, email, company_id, products);
    }

    // Processes title tags in the feed.
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    // Processes link tags in the feed.
    private String readAddress(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "address");
        String address = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "address");
        return address;
    }

    // Processes summary tags in the feed.
    private String readPhone(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "phone");
        String phone = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "phone");
        return phone;
    }

    // Processes summary tags in the feed.
    private String readCity(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "city");
        String city = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "city");
        return city;
    }

    // Processes summary tags in the feed.
    private String readEmail(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "email");
        String email = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "email");
        return email;
    }

    // Processes summary tags in the feed.
    private String readCompany_id(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "company_id");
        String company_id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "company_id");
        return company_id;
    }

    // Processes summary tags in the feed.
    private ArrayList<String> readProducts(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<String> products = new ArrayList<String>();
        System.out.println("readProducts");
        parser.require(XmlPullParser.START_TAG, ns, "products");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                    System.out.println("stop2 "+parser.getName());
                continue;
            }
            String name = parser.getName();
            products.add(name);
            // Starts by looking for the entry tag
            if (!name.isEmpty()) {
                parser.next();
                parser.next();

            } else {
                System.out.println("skip");
                skip(parser);
            }
        }
        return products;
    }



    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}