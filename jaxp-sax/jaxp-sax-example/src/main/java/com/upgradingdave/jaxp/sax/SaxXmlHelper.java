package com.upgradingdave.jaxp.sax;

import com.upgradingdave.jaxp.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaxXmlHelper implements XmlHelper {

    private static final Logger log = LoggerFactory.getLogger(SaxXmlHelper.class);

    File xmlFile;

    public SaxXmlHelper(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Override
    public List<String> findValues(String tagName) {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);

        try {

            SAXParser saxParser = spf.newSAXParser();

            XMLReader xmlReader = saxParser.getXMLReader();
            FileInputStream fileInputStream = new FileInputStream(xmlFile);

            SAXTagValue saxTagValue = new SAXTagValue(tagName);
            xmlReader.setContentHandler(saxTagValue);
            xmlReader.parse(new InputSource(fileInputStream));

            return saxTagValue.getTagValues();

        } catch (ParserConfigurationException e) {
            log.error("Unable to create sax parser due to config issue", e);
        } catch (SAXException e) {
            log.error("Unable to create sax parser", e);
        } catch (FileNotFoundException e) {
            log.error("Unable to find file {}", xmlFile, e);
        } catch (IOException e) {
            log.error("Problem finding or reading file {}", xmlFile, e);
        }

        return null;

    }

    class SAXTagValue extends DefaultHandler {

        private String tagName;
        private List<String> tagValues;
        private boolean insideTag;
        private StringBuffer tagValue;

        public SAXTagValue(String tagName) {

            this.tagName = tagName;

            tagValues = new ArrayList<String>();
            insideTag = false;

        }

        public List<String> getTagValues() {
            return tagValues;
        }

        @Override
        public void startElement (String uri, String localName, String qName, Attributes attributes)
                throws SAXException {

            if(localName.equals(tagName)) {

                insideTag = true;
                tagValue = new StringBuffer();

            }

        }

        @Override
        public void endElement (String uri, String localName, String qName) throws SAXException {

            if(localName.equals(tagName)) {

                insideTag = false;
                tagValues.add(tagValue.toString());

            }
        }

        @Override
        public void characters (char ch[], int start, int length) throws SAXException  {

            if(insideTag) {

                for(int i=start; i<start+length; i++) {
                    tagValue.append(ch[i]);
                }

            }
        }
    }
}
