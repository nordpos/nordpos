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

import com.openbravo.basic.BasicException;
import com.openbravo.beans.JFlowPanel;
import com.openbravo.beans.JPasswordDialog;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.BatchSentence;
import com.openbravo.data.loader.BatchSentenceResource;
import com.openbravo.data.loader.Session;
import com.nordpos.device.plu.DeviceInputOutput;
import com.nordpos.device.plu.DeviceInputOutputFactory;
import com.nordpos.device.ticket.DeviceTicketFactory;
import com.nordpos.device.ticket.TicketParser;
import com.nordpos.device.ticket.TicketPrinterException;
import com.nordpos.device.scale.DeviceScaleFactory;
import com.nordpos.device.util.StringParser;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import javax.swing.*;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class JRootApp extends JPanel implements AppView {

    private static final String PRINTER_SHEMA = "/com/nordpos/templates/Schema.Printer.xsd";
    private static final String PRINT_START = "/com/nordpos/templates/Printer.Start.xml";

    private AppProperties m_props;
    private Session session;
    private DataLogicSystem m_dlSystem;

    private Properties m_propsdb = null;
    private String m_sActiveCashIndex;
    private int m_iActiveCashSequence;
    private Date m_dActiveCashDateStart;
    private Date m_dActiveCashDateEnd;

    private String m_sInventoryLocation;

    private String m_sGenerateProductReference;
    private String m_sGenerateProductBarcode;

    private String m_sCustomerCard;
    private String m_sUserCard;

    private String m_sUserBarcode;
    private String m_sPriceBarcode;
    private String m_sUnitBarcode;
    private String m_sProductPriceBarcode;

    private String m_sDefaultInventoryLocation;
    private String m_sDefaultTaxCategory;
    private String m_sDefaultProductCategory;

    private StringBuffer inputtext;

    private DeviceScaleFactory m_Scale;
    private DeviceInputOutput m_DevicePLUs;
    private DeviceTicketFactory m_TP;
    private TicketParser m_TTP;

    private final Map<String, BeanFactory> m_aBeanFactories;

    private JPrincipalApp m_principalapp = null;

    public JRootApp() {

        m_aBeanFactories = new HashMap<>();

        // Inicializo los componentes visuales
        initComponents();
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
    }

    public boolean initApp(AppProperties props) {

        m_props = props;
        //setPreferredSize(new java.awt.Dimension(800, 600));

        // support for different component orientation languages.
        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        // Database start
        try {
            session = AppViewConnection.createSession(m_props);
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, e.getMessage(), e));
            return false;
        }

        m_dlSystem = (DataLogicSystem) getBean(DataLogicSystem.class.getName());

        // Create or upgrade the database if database version is not the expected
        String sDBApplication = readDataBaseApplication();
        String sDBVersion = readDataBaseVersion();
        if (!AppLocal.APP_ID.equals(sDBApplication) || !AppLocal.APP_VERSION.equals(sDBVersion)) {

            // Create or upgrade database
            String sScript = (sDBVersion == null && sDBApplication == null)
                    ? m_dlSystem.getInitScript() + "-create-" + AppLocal.APP_ID + ".sql"
                    : m_dlSystem.getInitScript() + "-upgrade-" + sDBApplication + "-" + (sDBVersion == null ? AppLocal.APP_VERSION : sDBVersion) + ".sql";
            if (JRootApp.class.getResource(sScript) == null) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, (sDBVersion == null && sDBApplication == null)
                        ? AppLocal.getIntString("message.databasenotsupported", session.DB.getName()) // Create script does not exists. Database not supported
                        : AppLocal.getIntString("message.noupdatescript"))); // Upgrade script does not exist.
                session.close();
                return false;
            } else {
                // Create or upgrade script exists.
                if (JOptionPane.showConfirmDialog(this, AppLocal.getIntString((sDBVersion == null && sDBApplication == null) ? "message.createdatabase" : "message.updatedatabase"), AppLocal.getIntString("message.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                    try {
                        BatchSentence bsentence = new BatchSentenceResource(session, sScript);
                        bsentence.putParameter("APP_ID", Matcher.quoteReplacement(AppLocal.APP_ID));
                        bsentence.putParameter("APP_NAME", Matcher.quoteReplacement(AppLocal.APP_NAME));
                        bsentence.putParameter("APP_VERSION", Matcher.quoteReplacement(AppLocal.APP_VERSION));

                        java.util.List l = bsentence.list();
                        if (l.size() > 0) {
                            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("Database.ScriptWarning"), l.toArray(new Throwable[l.size()])));
                        }
                    } catch (BasicException e) {
                        JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, AppLocal.getIntString("Database.ScriptError"), e));
                        session.close();
                        return false;
                    }
                } else {
                    session.close();
                    return false;
                }
            }
        }

        // Cargamos las propiedades de base de datos
        m_propsdb = m_dlSystem.getResourceAsProperties(m_props.getHost() + "/properties");

        // creamos la caja activa si esta no existe
        try {
            String sActiveCashIndex = m_propsdb.getProperty("activecash");
            Object[] valcash = sActiveCashIndex == null
                    ? null
                    : m_dlSystem.findActiveCash(sActiveCashIndex);
            if (valcash == null || !m_props.getHost().equals(valcash[0])) {
                // no la encuentro o no es de mi host por tanto creo una...
                setActiveCash(UUID.randomUUID().toString(), m_dlSystem.getSequenceCash(m_props.getHost()) + 1, new Date(), null);

                // creamos la caja activa
                m_dlSystem.execInsertCash(
                        new Object[]{getActiveCashIndex(), m_props.getHost(), getActiveCashSequence(), getActiveCashDateStart(), getActiveCashDateEnd()});
            } else {
                setActiveCash(sActiveCashIndex, (Integer) valcash[1], (Date) valcash[2], (Date) valcash[3]);
            }
        } catch (BasicException e) {
            // Casco. Sin caja no hay pos
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
            session.close();
            return false;
        }

        String sPropertySettings = m_props.getHost() + "/properties";
        // Leo la localizacion de la caja (Almacen).

        m_sDefaultInventoryLocation = m_propsdb.getProperty("location");
        if (m_sDefaultInventoryLocation == null) {
            m_sDefaultInventoryLocation = "0";
            m_propsdb.setProperty("location", m_sDefaultInventoryLocation);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sGenerateProductReference = m_propsdb.getProperty("genreference");
        if (m_sGenerateProductReference == null) {
            m_sGenerateProductReference = "true";
            m_propsdb.setProperty("genreference", m_sGenerateProductReference);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sGenerateProductBarcode = m_propsdb.getProperty("genbarcode");
        if (m_sGenerateProductBarcode == null) {
            m_sGenerateProductBarcode = "true";
            m_propsdb.setProperty("genbarcode", m_sGenerateProductBarcode);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sUserBarcode = m_propsdb.getProperty("userbarcode");
        if (m_sUserBarcode == null) {
            m_sUserBarcode = "200";
            m_propsdb.setProperty("userbarcode", m_sUserBarcode);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sPriceBarcode = m_propsdb.getProperty("pricebarcode");
        if (m_sPriceBarcode == null) {
            m_sPriceBarcode = "210";
            m_propsdb.setProperty("pricebarcode", m_sPriceBarcode);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sUnitBarcode = m_propsdb.getProperty("unitbarcode");
        if (m_sUnitBarcode == null) {
            m_sUnitBarcode = "220";
            m_propsdb.setProperty("unitbarcode", m_sUnitBarcode);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sProductPriceBarcode = m_propsdb.getProperty("productpricebarcode");
        if (m_sProductPriceBarcode == null) {
            m_sProductPriceBarcode = "250";
            m_propsdb.setProperty("productpricebarcode", m_sProductPriceBarcode);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sCustomerCard = m_propsdb.getProperty("customercard");
        if (m_sCustomerCard == null) {
            m_sCustomerCard = "c";
            m_propsdb.setProperty("customercard", m_sCustomerCard);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sUserCard = m_propsdb.getProperty("usercard");
        if (m_sUserCard == null) {
            m_sUserCard = "u";
            m_propsdb.setProperty("usercard", m_sUserCard);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sDefaultTaxCategory = m_propsdb.getProperty("taxcategoryid");
        if (m_sDefaultTaxCategory == null) {
            m_sDefaultTaxCategory = "000";
            m_propsdb.setProperty("taxcategoryid", m_sDefaultTaxCategory);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        m_sDefaultProductCategory = m_propsdb.getProperty("productcategoryid");
        if (m_sDefaultProductCategory == null) {
            m_sDefaultProductCategory = "000";
            m_propsdb.setProperty("productcategoryid", m_sDefaultProductCategory);
            m_dlSystem.setResourceAsProperties(sPropertySettings, m_propsdb);
        }

        // Inicializo la impresora...
        m_TP = new DeviceTicketFactory(this, m_props);

        // Inicializamos
        printerStart();

        // Inicializamos la bascula
        m_Scale = new DeviceScaleFactory(this, m_props);

        // Inicializamos la scanpal
        if (new StringParser(m_props.getProperty("machine.pludevice")).nextToken(':').equals("Not defined")) {
            m_DevicePLUs = null;
        } else {
            m_DevicePLUs = DeviceInputOutputFactory.createInstance(m_props);
        }
        
        // Leemos los recursos basicos
        BufferedImage imgicon = DataLogicSystem.getResourceAsImage("Window.Logo");
        m_jLblTitle.setIcon(imgicon == null ? null : new ImageIcon(imgicon));
        m_jLblTitle.setText(DataLogicSystem.getResourceAsText("Window.Title"));

        BufferedImage imgdesclogo1 = DataLogicSystem.getResourceAsImage("Window.DescLogo");
        m_jLblDescriptionFirst.setIcon(imgdesclogo1 == null ? new ImageIcon(getClass().getResource("/com/openbravo/images/logo.png")) : new ImageIcon(imgdesclogo1));
        m_jLblDescriptionFirst.setText(DataLogicSystem.getResourceAsText("Window.Description"));

        BufferedImage imgdesclogo2 = DataLogicSystem.getResourceAsImage("Window.DescLogoSecond");
        m_jLblDescriptionSecond.setIcon(imgdesclogo2 == null ? null : new ImageIcon(imgdesclogo2));
        m_jLblDescriptionSecond.setText(DataLogicSystem.getResourceAsText("Window.DescriptionSecond"));

        BufferedImage imgpoweredby = DataLogicSystem.getResourceAsImage("Window.PoweredBy");
        m_jLblPoweredBy.setIcon(imgpoweredby == null ? new ImageIcon(getClass().getResource("/com/openbravo/images/poweredby.png")) : new ImageIcon(imgpoweredby));

        BufferedImage imgsupportby = DataLogicSystem.getResourceAsImage("Window.SupportBy");
        m_jLblSupportBy.setIcon(imgsupportby == null ? null : new ImageIcon(imgsupportby));

        showHostAndLocation(m_sDefaultInventoryLocation);
        showLogin();

        return true;
    }

    private void showHostAndLocation(String sWareHouseId) {
        String sWareHouseName;
        try {
            sWareHouseName = m_dlSystem.findLocationName(sWareHouseId);
        } catch (BasicException e) {
            sWareHouseName = null; // no he encontrado el almacen principal
        }

        // Show Hostname, Warehouse and URL in taskbar
        String url;
        try {
            url = session.getURL();
        } catch (SQLException e) {
            url = "";
        }
        m_jHost.setText("<html>" + m_props.getHost() + " - " + sWareHouseName + "<br>" + url);
    }

    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findVersion();
        } catch (BasicException ed) {
            return null;
        }
    }

    private String readDataBaseApplication() {
        try {
            return m_dlSystem.findApplication();
        } catch (BasicException ed) {
            return null;
        }
    }

    public void tryToClose() {

        if (closeAppView()) {

            // success. continue with the shut down
            // apago el visor
            m_TP.getDeviceDisplay().clearVisor();
            // me desconecto de la base de datos.
            session.close();

            // Download Root form
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    // Interfaz de aplicacion
    @Override
    public DeviceTicketFactory getDeviceTicket() {
        return m_TP;
    }

    @Override
    public DeviceScaleFactory getDeviceScale() {
        return m_Scale;
    }

    @Override
    public DeviceInputOutput getDevicePLUs() {
        return m_DevicePLUs;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public String getInventoryLocation() {
        return m_sInventoryLocation;
    }

    @Override
    public String getDefaultInventoryLocation() {
        return m_sDefaultInventoryLocation;
    }

    @Override
    public String getDefaultTaxCategory() {
        return m_sDefaultTaxCategory;
    }

    @Override
    public String getDefaultProductCategory() {
        return m_sDefaultProductCategory;
    }

    @Override
    public String getCustomerCard() {
        return m_sCustomerCard;
    }

    @Override
    public String getUserCard() {
        return m_sUserCard;
    }

    @Override
    public String getUserBarcode() {
        return m_sUserBarcode;
    }

    @Override
    public String getPriceBarcode() {
        return m_sPriceBarcode;
    }

    @Override
    public String getUnitBarcode() {
        return m_sUnitBarcode;
    }

    @Override
    public String getProductPriceBarcode() {
        return m_sProductPriceBarcode;
    }

    @Override
    public String getGenerateProductReference() {
        return m_sGenerateProductReference;
    }

    @Override
    public String getGenerateProductBarcode() {
        return m_sGenerateProductBarcode;
    }

    @Override
    public String getActiveCashIndex() {
        return m_sActiveCashIndex;
    }

    @Override
    public int getActiveCashSequence() {
        return m_iActiveCashSequence;
    }

    @Override
    public Date getActiveCashDateStart() {
        return m_dActiveCashDateStart;
    }

    @Override
    public Date getActiveCashDateEnd() {
        return m_dActiveCashDateEnd;
    }

    @Override
    public void setActiveCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        m_sActiveCashIndex = sIndex;
        m_iActiveCashSequence = iSeq;
        m_dActiveCashDateStart = dStart;
        m_dActiveCashDateEnd = dEnd;

        m_propsdb.setProperty("activecash", m_sActiveCashIndex);
        m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties", m_propsdb);
    }

    @Override
    public AppProperties getProperties() {
        return m_props;
    }

    @Override
    public Object getBean(String beanfactory) throws BeanFactoryException {

        BeanFactory bf = m_aBeanFactories.get(beanfactory);
        if (bf == null) {

            // testing sripts
            if (beanfactory.startsWith("/")) {
                bf = new BeanFactoryScript(beanfactory);
            } else {
                // Class BeanFactory
                try {
                    Class bfclass = Class.forName(beanfactory);

                    if (BeanFactory.class.isAssignableFrom(bfclass)) {
                        bf = (BeanFactory) bfclass.newInstance();
                    } else {
                        // the old construction for beans...
                        Constructor constMyView = bfclass.getConstructor(new Class[]{AppView.class});
                        Object bean = constMyView.newInstance(new Object[]{this});

                        bf = new BeanFactoryObj(bean);
                    }

                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
                    // ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
                    throw new BeanFactoryException(e);
                }
            }

            // cache the factory
            m_aBeanFactories.put(beanfactory, bf);

            // Initialize if it is a BeanFactoryApp
            if (bf instanceof BeanFactoryApp) {
                ((BeanFactoryApp) bf).init(this);
            }
        }
        return bf.getBean();
    }

    @Override
    public void waitCursorBegin() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void waitCursorEnd() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public AppUserView getAppUserView() {
        return m_principalapp;
    }

    private void printerStart() {

        InputStream schema = getClass().getResourceAsStream(PRINTER_SHEMA);
        InputStream template = getClass().getResourceAsStream(PRINT_START);

        if (schema == null || template == null) {
            m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
        } else {
            m_TTP = new TicketParser(schema, getDeviceTicket());
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("local", new AppLocal());
                m_TTP.printTicket(template, script);
            } catch (ScriptException | TicketPrinterException eTP) {
                m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
            }
        }
    }

    private void listPeople() {

        try {

            jScrollPane1.getViewport().setView(null);

            JFlowPanel jPeople = new JFlowPanel();
            jPeople.applyComponentOrientation(getComponentOrientation());

            java.util.List people = m_dlSystem.listPeopleVisible();

            for (Object people1 : people) {
                AppUser user = (AppUser) people1;
                JButton btn = new JButton(new AppUserAction(user));
                btn.applyComponentOrientation(getComponentOrientation());
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setRequestFocusEnabled(false);
                btn.setHorizontalAlignment(SwingConstants.LEADING);
                btn.setMaximumSize(new Dimension(150, 50));
                btn.setPreferredSize(new Dimension(150, 50));
                btn.setMinimumSize(new Dimension(150, 50));
                jPeople.add(btn);
            }
            jScrollPane1.getViewport().setView(jPeople);

        } catch (BasicException ee) {
            ee.printStackTrace();
        }
    }

    // La accion del selector
    private class AppUserAction extends AbstractAction {

        private final AppUser m_actionuser;

        public AppUserAction(AppUser user) {
            m_actionuser = user;
            putValue(Action.SMALL_ICON, m_actionuser.getIcon());
            putValue(Action.NAME, m_actionuser.getName());
        }

        public AppUser getUser() {
            return m_actionuser;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            // String sPassword = m_actionuser.getPassword();
            if (m_actionuser.authenticate()) {
                // p'adentro directo, no tiene password
                openAppView(m_actionuser);
            } else {
                // comprobemos la clave antes de entrar...
                String sPassword = JPasswordDialog.showEditPassword(JRootApp.this,
                        AppLocal.getIntString("Label.Password"),
                        m_actionuser.getName(),
                        m_actionuser.getIcon());
                if (sPassword != null) {
                    if (m_actionuser.authenticate(sPassword)) {
                        openAppView(m_actionuser);
                    } else {
                        MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.BadPassword"));
                        msg.show(JRootApp.this);
                    }
                }
            }
        }
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout) (m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, view);
    }

    private void openAppView(AppUser user) {

        if (closeAppView()) {

            m_principalapp = new JPrincipalApp(this, user);

            // The user status notificator
            jPanel3.add(m_principalapp.getNotificator());
            jPanel3.revalidate();

            m_sInventoryLocation = user.getProperties().getProperty("user.location.id", m_sDefaultInventoryLocation);

            // The main panel
            m_jPanelContainer.add(m_principalapp, "_" + m_principalapp.getUser().getId());
            showView("_" + m_principalapp.getUser().getId());
            showHostAndLocation(m_sInventoryLocation);

            m_principalapp.activate();
        }
    }

    public boolean closeAppView() {

        if (m_principalapp == null) {
            return true;
        } else if (!m_principalapp.deactivate()) {
            return false;
        } else {
            // the status label
            jPanel3.remove(m_principalapp.getNotificator());
            jPanel3.revalidate();
            jPanel3.repaint();

            // remove the card
            m_jPanelContainer.remove(m_principalapp);
            m_principalapp = null;

            m_sInventoryLocation = m_sDefaultInventoryLocation;
            showHostAndLocation(m_sInventoryLocation);

            showLogin();

            return true;
        }
    }

    private void showLogin() {

        // Show Login
        listPeople();
        showView("login");

        // show welcome message
        printerStart();

        // keyboard listener activation
        inputtext = new StringBuffer();
        m_txtKeys.setText(null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_txtKeys.requestFocus();
            }
        });
    }

    private void processKey(char c) {

        if (c == '\n') {

            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(inputtext.toString());
            } catch (BasicException e) {
                e.printStackTrace();
            }

            if (user == null) {
                // user not found
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.nocard"));
                msg.show(this);
            } else {
                openAppView(user);
            }

            inputtext = new StringBuffer();
        } else {
            inputtext.append(c);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelTitle = new javax.swing.JPanel();
        m_jLblTitle = new javax.swing.JLabel();
        m_jLblPoweredBy = new javax.swing.JLabel();
        m_jLblSupportBy = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();
        m_jPanelLogin = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jLblDescriptionFirst = new javax.swing.JLabel();
        m_jLblDescriptionSecond = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        m_jLogonName = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        m_jClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        m_txtKeys = new javax.swing.JTextField();
        m_jPanelDown = new javax.swing.JPanel();
        panelTask = new javax.swing.JPanel();
        m_jHost = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(1024, 768));
        setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        m_jLblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTitle.setText("Window.Title");
        m_jPanelTitle.add(m_jLblTitle, java.awt.BorderLayout.CENTER);

        m_jLblPoweredBy.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        m_jLblPoweredBy.setMaximumSize(new java.awt.Dimension(142, 34));
        m_jLblPoweredBy.setMinimumSize(new java.awt.Dimension(142, 34));
        m_jLblPoweredBy.setPreferredSize(new java.awt.Dimension(142, 34));
        m_jPanelTitle.add(m_jLblPoweredBy, java.awt.BorderLayout.LINE_END);

        m_jLblSupportBy.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        m_jLblSupportBy.setMaximumSize(new java.awt.Dimension(142, 34));
        m_jLblSupportBy.setMinimumSize(new java.awt.Dimension(142, 34));
        m_jLblSupportBy.setPreferredSize(new java.awt.Dimension(142, 34));
        m_jPanelTitle.add(m_jLblSupportBy, java.awt.BorderLayout.LINE_START);

        add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setLayout(new java.awt.CardLayout());

        m_jPanelLogin.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        m_jLblDescriptionFirst.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblDescriptionFirst.setText("Window.Description");
        m_jLblDescriptionFirst.setAlignmentX(0.5F);
        m_jLblDescriptionFirst.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_jLblDescriptionFirst.setMaximumSize(new java.awt.Dimension(976, 800));
        m_jLblDescriptionFirst.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(m_jLblDescriptionFirst);

        m_jLblDescriptionSecond.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblDescriptionSecond.setText("Window.Description");
        m_jLblDescriptionSecond.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        m_jLblDescriptionSecond.setAlignmentX(0.5F);
        m_jLblDescriptionSecond.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_jLblDescriptionSecond.setMaximumSize(new java.awt.Dimension(976, 800));
        m_jLblDescriptionSecond.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(m_jLblDescriptionSecond);

        m_jPanelLogin.add(jPanel4, java.awt.BorderLayout.CENTER);

        m_jLogonName.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jLogonName.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(510, 118));
        m_jLogonName.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.GridLayout(0, 1, 5, 5));

        m_jClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/exit.png"))); // NOI18N
        m_jClose.setText(AppLocal.getIntString("Button.Close")); // NOI18N
        m_jClose.setFocusPainted(false);
        m_jClose.setFocusable(false);
        m_jClose.setRequestFocusEnabled(false);
        m_jClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseActionPerformed(evt);
            }
        });
        jPanel8.add(m_jClose);

        jPanel2.add(jPanel8, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(null);

        m_txtKeys.setPreferredSize(new java.awt.Dimension(0, 0));
        m_txtKeys.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_txtKeysKeyTyped(evt);
            }
        });
        jPanel1.add(m_txtKeys);
        m_txtKeys.setBounds(0, 0, 0, 0);

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

        m_jLogonName.add(jPanel2, java.awt.BorderLayout.LINE_END);

        jPanel5.add(m_jLogonName);

        m_jPanelLogin.add(jPanel5, java.awt.BorderLayout.SOUTH);

        m_jPanelContainer.add(m_jPanelLogin, "login");

        add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        m_jPanelDown.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelDown.setLayout(new java.awt.BorderLayout());

        m_jHost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/display.png"))); // NOI18N
        m_jHost.setText("*Hostname");
        panelTask.add(m_jHost);

        m_jPanelDown.add(panelTask, java.awt.BorderLayout.LINE_START);
        m_jPanelDown.add(jPanel3, java.awt.BorderLayout.LINE_END);

        add(m_jPanelDown, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    private void m_jCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseActionPerformed

        tryToClose();

    }//GEN-LAST:event_m_jCloseActionPerformed

    private void m_txtKeysKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_txtKeysKeyTyped

        m_txtKeys.setText("0");

        processKey(evt.getKeyChar());

    }//GEN-LAST:event_m_txtKeysKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton m_jClose;
    private javax.swing.JLabel m_jHost;
    private javax.swing.JLabel m_jLblDescriptionFirst;
    private javax.swing.JLabel m_jLblDescriptionSecond;
    private javax.swing.JLabel m_jLblPoweredBy;
    private javax.swing.JLabel m_jLblSupportBy;
    private javax.swing.JLabel m_jLblTitle;
    private javax.swing.JPanel m_jLogonName;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JPanel m_jPanelDown;
    private javax.swing.JPanel m_jPanelLogin;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JTextField m_txtKeys;
    private javax.swing.JPanel panelTask;
    // End of variables declaration//GEN-END:variables
}
