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

import com.openbravo.pos.util.AltEncrypter;
import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class AppConfig implements AppProperties {

    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private Properties m_propsconfig;
    private File configfile;

    public AppConfig(String[] args) {
        if (args.length == 0) {
            init(getDefaultConfig());
        } else {
            init(new File(args[0]));
        }
    }

    public AppConfig(File configfile) {
        init(configfile);
    }

    private void init(File configfile) {
        this.configfile = configfile;
        m_propsconfig = new Properties();

        logger.log(Level.INFO, "Reading configuration file: {0}", configfile.getAbsolutePath());
    }

    private File getDefaultConfig() {
        return new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + ".properties");
    }

    @Override
    public String getProperty(String sKey) {
        return m_propsconfig.getProperty(sKey);
    }

    @Override
    public String getHost() {
        return getProperty("machine.hostname");
    }

    @Override
    public String getDBDriver() {
        return getProperty("db.driver");
    }

    @Override
    public String getDBDriverLib() {
        return getProperty("db.driverlib");
    }

    @Override
    public String getDBUser() {
        return getProperty("db.user");
    }

    @Override
    public String getDBPassword() {
        String sPassword = getProperty("db.password");
        String sUser = getDBUser();
        if (sUser != null && sPassword != null && sPassword.startsWith("crypt:")) {
            // the password is encrypted
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sUser);
            sPassword = cypher.decrypt(sPassword.substring(6));
        }

        return sPassword;
    }

    @Override
    public String getDBURL() {
        return getProperty("db.URL");
    }

    @Override
    public File getConfigFile() {
        return configfile;
    }

    public void setProperty(String sKey, String sValue) {
        if (sValue == null) {
            m_propsconfig.remove(sKey);
        } else {
            m_propsconfig.setProperty(sKey, sValue);
        }
    }

    private String getLocalHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (java.net.UnknownHostException eUH) {
            return "localhost";
        }
    }

    public boolean delete() {
        loadDefault();
        return configfile.delete();
    }

    public void load() {

        loadDefault();

        try {
            InputStream in = new FileInputStream(configfile);
            if (in != null) {
                m_propsconfig.load(in);
                in.close();
            }
        } catch (IOException e) {
            loadDefault();
        }

    }

    public void save() throws IOException {

        OutputStream out = new FileOutputStream(configfile);
        if (out != null) {
            m_propsconfig.store(out, AppLocal.APP_NAME + ". Configuration file.");
            out.close();
        }
    }

    private void loadDefault() {

        m_propsconfig = new Properties();

        String dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;

        m_propsconfig.setProperty("db.type", "Derby Client");
        m_propsconfig.setProperty("db.driverlib", new File(new File(dirname), "lib-jdbc/derbyclient.jar").getAbsolutePath());
        m_propsconfig.setProperty("db.driver", "org.apache.derby.jdbc.ClientDriver");
        m_propsconfig.setProperty("db.URL", "jdbc:derby://localhost:1527/" + AppLocal.APP_ID + "-db;create=true");
        m_propsconfig.setProperty("db.user", "APP");
        m_propsconfig.setProperty("db.password", "app");

        m_propsconfig.setProperty("machine.hostname", getLocalHostName());
        m_propsconfig.setProperty("machine.leftpanel", "open");

        Locale l = Locale.getDefault();
        m_propsconfig.setProperty("user.language", l.getLanguage());
        m_propsconfig.setProperty("user.country", l.getCountry());
        m_propsconfig.setProperty("user.variant", l.getVariant());

        m_propsconfig.setProperty("swing.defaultlaf", System.getProperty("swing.defaultlaf", "org.pushingpixels.substance.api.skin.CremeSkin"));

        m_propsconfig.setProperty("machine.printer", "screen");
        m_propsconfig.setProperty("machine.printer.2", "Not defined");
        m_propsconfig.setProperty("machine.printer.3", "Not defined");
        m_propsconfig.setProperty("machine.fiscalprinter", "Not defined");
        m_propsconfig.setProperty("machine.display", "screen");
        m_propsconfig.setProperty("machine.scale", "Not defined");
        m_propsconfig.setProperty("machine.screenmode", "window"); // fullscreen / window
        m_propsconfig.setProperty("machine.ticketsbag", "standard");
//        m_propsconfig.setProperty("machine.scanner", "Not defined");
        m_propsconfig.setProperty("machine.pludevice", "Not defined");
        m_propsconfig.setProperty("machine.labelprinter", "Not defined");

        m_propsconfig.setProperty("payment.gateway", "external");
        m_propsconfig.setProperty("payment.magcardreader", "Not defined");
        m_propsconfig.setProperty("payment.testmode", "false");
        m_propsconfig.setProperty("payment.commerceid", "");
        m_propsconfig.setProperty("payment.commercepassword", "password");

        m_propsconfig.setProperty("machine.printername", "(Default)");

        // Receipt printer paper set to 72mmx200mm
        m_propsconfig.setProperty("paper.receipt.x", "10");
        m_propsconfig.setProperty("paper.receipt.y", "287");
        m_propsconfig.setProperty("paper.receipt.width", "190");
        m_propsconfig.setProperty("paper.receipt.height", "546");
        m_propsconfig.setProperty("paper.receipt.mediasizename", "A4");

        // Normal printer paper for A4
        m_propsconfig.setProperty("paper.standard.x", "72");
        m_propsconfig.setProperty("paper.standard.y", "72");
        m_propsconfig.setProperty("paper.standard.width", "451");
        m_propsconfig.setProperty("paper.standard.height", "698");
        m_propsconfig.setProperty("paper.standard.mediasizename", "A4");

        m_propsconfig.setProperty("machine.uniqueinstance", "false");
        
        m_propsconfig.setProperty("server.webapp.startup", "disable");
        m_propsconfig.setProperty("server.webapp.port", "8135");
        m_propsconfig.setProperty("server.webapp.context", "/");
        
        m_propsconfig.setProperty("server.database.startup", "enable");
        
    }
}
