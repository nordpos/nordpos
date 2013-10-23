//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2011 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.sales;

import com.openbravo.data.loader.LocalRes;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author adrian
 */
public class PropertiesConfig {

    private static final Logger logger = Logger.getLogger(PropertiesConfig.class.getName());
    private static SAXParser m_sp = null;
    private static XMLReader m_sr = null;    
    private Properties props = new Properties();
    
    private InputStream InputStream;    

    public PropertiesConfig(String configXML) {

        if (configXML != null) {
            try {
                if (m_sp == null) {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setValidating(false);
                spf.setNamespaceAware(true);
                SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                InputStream = getClass().getResourceAsStream("/com/openbravo/pos/templates/Schema.Ticket.xsd");
                spf.setSchema(schemaFactory.newSchema(new Source[]{new StreamSource(InputStream)}));
                m_sp = spf.newSAXParser();
                m_sr = m_sp.getXMLReader();
                m_sr.setContentHandler(new ConfigurationHandler());
                }
                m_sp.parse(new InputSource(new StringReader(configXML)), new ConfigurationHandler());
                m_sr.parse(new InputSource(new StringReader(configXML)));
            } catch (ParserConfigurationException ePC) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.parserconfig"), ePC);
            } catch (SAXException eSAX) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.xmlfile"), eSAX);
            } catch (IOException eIO) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.iofile"), eIO);
            }
        }
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return props.getProperty(key, defaultvalue);
    }

    private class ConfigurationHandler extends DefaultHandler {

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String value = attributes.getValue("value");
            if (value != null) {
                props.setProperty(qName, attributes.getValue("value"));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }
}
