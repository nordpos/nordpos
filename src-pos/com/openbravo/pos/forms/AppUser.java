//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008-2009 Openbravo, S.L.
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
package com.openbravo.pos.forms;

import com.openbravo.data.loader.LocalRes;
import com.openbravo.pos.ticket.UserInfo;
import com.openbravo.pos.util.Hashcypher;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class AppUser {

    private static final Logger logger = Logger.getLogger(AppUser.class.getName());

    private static SAXParser m_sp = null;

    private final String m_sId;
    private final String m_sName;
    private final String m_sCard;
    private String m_sPassword;
    private final String m_sRole;
    private final Icon m_Icon;
    private final Properties properties;

    private Set<String> m_apermissions;

    public AppUser(String id, String name, String password, String card, String role, Icon icon, Properties properties) {
        m_sId = id;
        m_sName = name;
        m_sPassword = password;
        m_sCard = card;
        m_sRole = role;
        m_Icon = icon;
        m_apermissions = null;
        this.properties = properties;
    }

    public Icon getIcon() {
        return m_Icon;
    }

    public String getId() {
        return m_sId;
    }

    public String getName() {
        return m_sName;
    }

    public void setPassword(String sValue) {
        m_sPassword = sValue;
    }

    public String getPassword() {
        return m_sPassword;
    }

    public String getRole() {
        return m_sRole;
    }

    public String getCard() {
        return m_sCard;
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean authenticate() {
        return m_sPassword == null || m_sPassword.equals("") || m_sPassword.startsWith("empty:");
    }

    public boolean authenticate(String sPwd) {
        return Hashcypher.authenticate(sPwd, m_sPassword);
    }

    public void fillPermissions(DataLogicSystem dlSystem) {

        // inicializamos los permisos
        m_apermissions = new HashSet<>();
        // Y lo que todos tienen permisos
        m_apermissions.add("com.openbravo.pos.forms.JPanelMenu");
        m_apermissions.add("Menu.Exit");

        String sRolePermisions = dlSystem.findRolePermissions(m_sRole);

        if (sRolePermisions != null) {
            try {
                if (m_sp == null) {
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    m_sp = spf.newSAXParser();
                }
                m_sp.parse(new InputSource(new StringReader(sRolePermisions)), new ConfigurationHandler());

            } catch (ParserConfigurationException ePC) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.parserconfig"), ePC);
            } catch (SAXException eSAX) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.xmlfile"), eSAX);
            } catch (IOException eIO) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.iofile"), eIO);
            }
        }

    }

    public boolean hasPermission(String classname) {

        return (m_apermissions == null) ? false : m_apermissions.contains(classname);
    }

    public UserInfo getUserInfo() {
        return new UserInfo(m_sId, m_sName);
    }

    private static String mapNewClass(String classname) {
        return classname;
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
            if ("class".equals(qName)) {
                m_apermissions.add(mapNewClass(attributes.getValue("name")));
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
