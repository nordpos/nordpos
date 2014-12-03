//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
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
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppUser;
import com.nordpos.device.ticket.TicketPrinterException;
import com.openbravo.pos.util.ThumbNailBuilder;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JPanelButtons extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(JPanelButtons.class.getName());

    private static SAXParser m_sp = null;

//    private Properties props;
    private Map<String, String> events;

    private ThumbNailBuilder tnbmacro;

    private JPanelTicket panelticket;

    /**
     * Creates new form JPanelButtons
     */
    public JPanelButtons(String sConfigRes, JPanelTicket panelticket) {
        initComponents();

        // Load categories default thumbnail
        tnbmacro = new ThumbNailBuilder(16, 16, 12, "com/openbravo/images/greenled.png");

        this.panelticket = panelticket;

        events = new HashMap<String, String>();

        if (sConfigRes != null) {
            try {
                if (m_sp == null) {
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    m_sp = spf.newSAXParser();
                }
                m_sp.parse(new InputSource(new StringReader(sConfigRes)), new ConfigurationHandler());

            } catch (ParserConfigurationException ePC) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.parserconfig"), ePC);
            } catch (SAXException eSAX) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.xmlfile"), eSAX);
            } catch (IOException eIO) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.iofile"), eIO);
            }
        }

    }

    public void setPermissions(AppUser user) {
        for (Component c : this.getComponents()) {
            String sKey = c.getName();
            if (sKey == null || sKey.equals("")) {
                c.setEnabled(true);
            } else {
                c.setEnabled(user.hasPermission(c.getName()));
            }
        }
    }

    public String getEvent(String key) {
        return events.get(key);
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
            if ("button".equals(qName)) {

                // The button title text
                String titlekey = attributes.getValue("titlekey");
                if (titlekey == null) {
                    titlekey = attributes.getValue("name");
                }
                String title = titlekey == null
                        ? attributes.getValue("title")
                        : AppLocal.getIntString(titlekey);

                // adding the button to the panel
                JButton btn = new JButtonFunc(attributes.getValue("key"),
                        attributes.getValue("image"),
                        title);

                btn.setMargin(new java.awt.Insets(0, 4, 0, 4));
                btn.setPreferredSize(new java.awt.Dimension(100, 44));

                // The template resource or the code resource
                final String template = attributes.getValue("template");
                if (template == null) {
                    final String code = attributes.getValue("code");
                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            panelticket.evalScriptAndRefresh(code);
                        }
                    });
                } else {
                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            try {
                                panelticket.printTicket(template);
                            } catch (TicketPrinterException e) {
                            }
                        }
                    });
                }
                add(btn);

            } else if ("event".equals(qName)) {
                events.put(attributes.getValue("key"), attributes.getValue("code"));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }

    private class JButtonFunc extends JButton {

        public JButtonFunc(String sKey, String sImage, String title) {

            setName(sKey);
            setText(title);
            setIcon(new ImageIcon(tnbmacro.getThumbNail(panelticket.getResourceAsImage(sImage))));
            setFocusPainted(false);
            setFocusable(false);
            setRequestFocusEnabled(false);
            setMargin(new Insets(8, 14, 8, 14));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
