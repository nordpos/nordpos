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
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import javax.swing.Icon;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import com.openbravo.pos.ticket.UserInfo;
import com.openbravo.pos.util.Hashcypher;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author adrianromero
 */
public class AppUser {

    private static Logger logger = Logger.getLogger("com.openbravo.pos.forms.AppUser");

    private static SAXParser m_sp = null;
    private static HashMap<String, String> m_oldclasses; // This is for backwards compatibility purposes
    
    private String m_sId;
    private String m_sName;
    private String m_sCard;
    private String m_sPassword;
    private String m_sRole;
    private Icon m_Icon;
    
    private Set<String> m_apermissions;
    
    static {        
        initOldClasses();
    }
    
    /** Creates a new instance of AppUser */
    public AppUser(String id, String name, String password, String card, String role, Icon icon) {
        m_sId = id;
        m_sName = name;
        m_sPassword = password;
        m_sCard = card;
        m_sRole = role;
        m_Icon = icon;
        m_apermissions = null;
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
    
    public boolean authenticate() {
        return m_sPassword == null || m_sPassword.equals("") || m_sPassword.startsWith("empty:");
    }
    public boolean authenticate(String sPwd) {
        return Hashcypher.authenticate(sPwd, m_sPassword);
    }
    
    public void fillPermissions(DataLogicSystem dlSystem) {
        
        // inicializamos los permisos
        m_apermissions = new HashSet<String>();
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
        String newclass = m_oldclasses.get(classname);
        return newclass == null 
                ? classname 
                : newclass;
    }
    
    private static void initOldClasses() {
        m_oldclasses = new HashMap<String, String>();
        
        // update permissions from 0.0.24 to 2.20    
        m_oldclasses.put("net.adrianromero.tpv.panelsales.JPanelTicketSales", "com.openbravo.pos.sales.JPanelTicketSales");
        m_oldclasses.put("net.adrianromero.tpv.panelsales.JPanelTicketEdits", "com.openbravo.pos.sales.JPanelTicketEdits");
        m_oldclasses.put("net.adrianromero.tpv.panels.JPanelPayments", "com.openbravo.pos.panels.JPanelPayments");
        m_oldclasses.put("net.adrianromero.tpv.panels.JPanelCloseMoney", "com.openbravo.pos.panels.JPanelCloseMoney");
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportClosedPos", "/com/openbravo/reports/closedpos.bs");

//        m_oldclasses.put("payment.cash", "");
//        m_oldclasses.put("payment.cheque", "");
//        m_oldclasses.put("payment.paper", "");
//        m_oldclasses.put("payment.tichet", "");
//        m_oldclasses.put("payment.magcard", "");
//        m_oldclasses.put("payment.free", "");
//        m_oldclasses.put("refund.cash", "");
//        m_oldclasses.put("refund.cheque", "");
//        m_oldclasses.put("refund.paper", "");
//        m_oldclasses.put("refund.magcard", "");

        m_oldclasses.put("Menu.StockManagement", "com.openbravo.pos.forms.MenuStockManagement");
        m_oldclasses.put("net.adrianromero.tpv.inventory.ProductsPanel", "com.openbravo.pos.inventory.ProductsPanel");
        m_oldclasses.put("net.adrianromero.tpv.inventory.ProductsWarehousePanel", "com.openbravo.pos.inventory.ProductsWarehousePanel");
        m_oldclasses.put("net.adrianromero.tpv.inventory.CategoriesPanel", "com.openbravo.pos.inventory.CategoriesPanel");
        m_oldclasses.put("net.adrianromero.tpv.panels.JPanelTax", "com.openbravo.pos.inventory.TaxPanel");
        m_oldclasses.put("net.adrianromero.tpv.inventory.StockDiaryPanel", "com.openbravo.pos.inventory.StockDiaryPanel");
        m_oldclasses.put("net.adrianromero.tpv.inventory.StockManagement", "com.openbravo.pos.inventory.StockManagement");
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportProducts", "/com/openbravo/reports/products.bs");      
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportCatalog", "/com/openbravo/reports/productscatalog.bs");
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportInventory", "/com/openbravo/reports/inventory.bs");
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportInventory2", "/com/openbravo/reports/inventoryb.bs");
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportInventoryBroken", "/com/openbravo/reports/inventorybroken.bs");
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportInventoryDiff", "/com/openbravo/reports/inventorydiff.bs");

        m_oldclasses.put("Menu.SalesManagement", "com.openbravo.pos.forms.MenuSalesManagement");
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportUserSales", "/com/openbravo/reports/usersales.bs");
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportClosedProducts", "/com/openbravo/reports/closedproducts.bs");
        m_oldclasses.put("net.adrianromero.tpv.reports.JReportTaxes", "/com/openbravo/reports/taxes.bs");
        m_oldclasses.put("net.adrianromero.tpv.reports.JChartSales", "/com/openbravo/reports/chartsales.bs");

        m_oldclasses.put("Menu.Maintenance", "com.openbravo.pos.forms.MenuMaintenance");
        m_oldclasses.put("net.adrianromero.tpv.admin.PeoplePanel", "com.openbravo.pos.admin.PeoplePanel");
        m_oldclasses.put("net.adrianromero.tpv.admin.RolesPanel", "com.openbravo.pos.admin.RolesPanel");
        m_oldclasses.put("net.adrianromero.tpv.admin.ResourcesPanel", "com.openbravo.pos.admin.ResourcesPanel");
        m_oldclasses.put("net.adrianromero.tpv.inventory.LocationsPanel", "com.openbravo.pos.inventory.LocationsPanel");
        m_oldclasses.put("net.adrianromero.tpv.mant.JPanelFloors", "com.openbravo.pos.mant.JPanelFloors");
        m_oldclasses.put("net.adrianromero.tpv.mant.JPanelPlaces", "com.openbravo.pos.mant.JPanelPlaces");
        m_oldclasses.put("com.openbravo.possync.ProductsSync", "com.openbravo.possync.ProductsSyncCreate");
        m_oldclasses.put("com.openbravo.possync.OrdersSync", "com.openbravo.possync.OrdersSyncCreate");

        m_oldclasses.put("Menu.ChangePassword", "Menu.ChangePassword");
        m_oldclasses.put("net.adrianromero.tpv.panels.JPanelPrinter", "com.openbravo.pos.panels.JPanelPrinter");
        m_oldclasses.put("net.adrianromero.tpv.config.JPanelConfiguration", "com.openbravo.pos.config.JPanelConfiguration");
        
//        m_oldclasses.put("button.print", "");
//        m_oldclasses.put("button.opendrawer", "");
        
        // update permissions from 2.00 to 2.20       
        m_oldclasses.put("com.openbravo.pos.reports.JReportCustomers", "/com/openbravo/reports/customers.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportCustomersB", "/com/openbravo/reports/customersb.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportClosedPos", "/com/openbravo/reports/closedpos.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportClosedProducts", "/com/openbravo/reports/closedproducts.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JChartSales", "/com/openbravo/reports/chartsales.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventory", "/com/openbravo/reports/inventory.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventory2", "/com/openbravo/reports/inventoryb.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventoryBroken", "/com/openbravo/reports/inventorybroken.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventoryDiff", "/com/openbravo/reports/inventorydiff.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportPeople", "/com/openbravo/reports/people.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportTaxes", "/com/openbravo/reports/taxes.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportUserSales", "/com/openbravo/reports/usersales.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportProducts", "/com/openbravo/reports/products.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportCatalog", "/com/openbravo/reports/productscatalog.bs");
        
        // update permissions from 2.10 to 2.20
        m_oldclasses.put("com.openbravo.pos.panels.JPanelTax", "com.openbravo.pos.inventory.TaxPanel");
        
    }
    
    private class ConfigurationHandler extends DefaultHandler {       
        @Override
        public void startDocument() throws SAXException {}
        @Override
        public void endDocument() throws SAXException {}    
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
            if ("class".equals(qName)){
                m_apermissions.add(mapNewClass(attributes.getValue("name")));
            }
        }      
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {}
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {}
    }     
    
    
}
