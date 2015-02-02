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
package com.openbravo.pos.forms;

import com.nordpos.server.derby.ServerDatabase;
import com.nordpos.server.jetty.AppContextBuilder;
import com.nordpos.server.jetty.ServerApp;
import com.openbravo.format.Formats;
import com.openbravo.pos.instance.InstanceQuery;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class StartPOS {

    private static final Logger logger = Logger.getLogger(StartPOS.class.getName());

    private static final int DEFAULT_WEBAPPSEVER_PORT = 8135;
    private static final String DEFAULT_WEBAPPSEVER_FOLDER = new File("webapps/").getAbsolutePath();

    private StartPOS() {
    }

    public static boolean registerApp() {
        try {
            // vemos si existe alguna instancia        
            InstanceQuery i = new InstanceQuery();
            i.getAppMessage().restoreWindow();
            return false;
        } catch (RemoteException | NotBoundException e) {
            return true;
        }
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public static void main(final String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                if (!registerApp()) {
                    System.exit(1);
                }

                AppConfig config = new AppConfig(args);
                config.load();

                // set Locale.
                String slang = config.getProperty("user.language");
                String scountry = config.getProperty("user.country");
                String svariant = config.getProperty("user.variant");
                if (slang != null && !slang.equals("") && scountry != null && svariant != null) {
                    Locale.setDefault(new Locale(slang, scountry, svariant));
                }

                // Set the format patterns
                Formats.setIntegerPattern(config.getProperty("format.integer"));
                Formats.setDoublePattern(config.getProperty("format.double"));
                Formats.setCurrencyPattern(config.getProperty("format.currency"));
                Formats.setPercentPattern(config.getProperty("format.percent"));
                Formats.setDatePattern(config.getProperty("format.date"));
                Formats.setTimePattern(config.getProperty("format.time"));
                Formats.setDateTimePattern(config.getProperty("format.datetime"));

                // Set the look and feel.
                try {
                    Object laf = Class.forName(config.getProperty("swing.defaultlaf")).newInstance();
                    if (laf instanceof LookAndFeel) {
                        UIManager.setLookAndFeel((LookAndFeel) laf);
                    } else if (laf instanceof SubstanceSkin) {
                        SubstanceLookAndFeel.setSkin((SubstanceSkin) laf);
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    logger.log(Level.WARNING, "Cannot set look and feel", e);
                }

                String font = config.getProperty("swing.font.name");
                if(font != null && !font.isEmpty()) {
                    setUIFont(new javax.swing.plaf.FontUIResource(font, Font.PLAIN, Integer.parseInt(config.getProperty("swing.font.size"))));
                }
                
                if (config.getProperty("server.webapp.startup") != null ? config.getProperty("server.webapp.startup").equals("enable") : false) {
                    StartUpWebAppServer(
                            config.getProperty("server.webapp.port") != null ? Integer.parseInt(config.getProperty("server.webapp.port")) : DEFAULT_WEBAPPSEVER_PORT,
                            config.getProperty("server.webapp.context") != null ? config.getProperty("server.webapp.context") : "/"
                    );
                }

                if (!config.getProperty("server.database.startup").equals("disable")) {
                    StartUpDatabaseServer();
                }

                String screenmode = config.getProperty("machine.screenmode");

                if ("fullscreen".equals(screenmode)) {
                    JRootKiosk rootkiosk = new JRootKiosk();
                    rootkiosk.initFrame(config);
                } else {
                    JRootFrame rootframe = new JRootFrame();
                    rootframe.initFrame(config);
                }
            }

            private void StartUpWebAppServer(int port, String context) {
                ContextHandlerCollection contexts = new ContextHandlerCollection();
                final File folder = new File(DEFAULT_WEBAPPSEVER_FOLDER);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                List handlerList = new ArrayList();
                for (final File fileEntry : folder.listFiles()) {
                    if (fileEntry.isDirectory()) {
                        handlerList.add(new AppContextBuilder().buildWebAppContext(context, DEFAULT_WEBAPPSEVER_FOLDER, fileEntry.getName()));
                    }
                }
                Handler[] handlers = new Handler[handlerList.size()];
                for (int i = 0; i < handlers.length; i++) {
                    handlers[i] = (Handler) handlerList.get(i);
                }

                ServerApp server = new ServerApp(port);
                contexts.setHandlers(handlers);
                server.setHandler(contexts);

                try {
                    server.start();
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Cannot run Jetty Server", ex);
                }
            }

            private void StartUpDatabaseServer() {
                ServerDatabase server = new ServerDatabase();

                try {
                    server.start();
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Cannot run Derby Server", ex);
                }
            }
        }
        );
    }
}
