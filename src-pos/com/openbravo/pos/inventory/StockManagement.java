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
package com.openbravo.pos.inventory;

import com.openbravo.basic.BasicException;
import com.openbravo.beans.DateUtils;
import com.openbravo.beans.JCalendarDialog;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.LocalRes;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.format.Formats;
import com.openbravo.pos.catalog.CatalogSelector;
import com.openbravo.pos.catalog.JCatalog;
import com.openbravo.pos.forms.*;
import com.nordpos.device.plu.DeviceInputOutput;
import com.nordpos.device.plu.DeviceInputOutputException;
import com.nordpos.device.plu.DeviceInputOutputNull;
import com.nordpos.device.plu.ProductIO;
import com.nordpos.device.ticket.TicketParser;
import com.nordpos.device.ticket.TicketPrinterException;
import com.openbravo.pos.sales.JProductAttEdit;
import com.openbravo.pos.sales.PropertiesConfig;
import com.openbravo.pos.sales.TaxesLogic;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.ticket.ProductInfoExt;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author adrianromero
 * @author jldy0717
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class StockManagement extends JPanel implements JPanelView {

    private static final String PRINTER_SHEMA = "/com/nordpos/templates/Schema.Printer.xsd";
    private static final String PRINT_INVENTORY = "/com/nordpos/templates/Printer.Inventory.xml";
    private static final String PRINT_STOCK_PREVIEW = "/com/nordpos/templates/Printer.StockPreview.xml";

    private final AppView m_App;
    private final DataLogicSystem m_dlSystem;
    final private DataLogicSales m_dlSales;
    private TicketParser m_TTP;

    private TaxesLogic taxeslogic;

    private final CatalogSelector m_cat;
    private final PropertiesConfig panelconfig;
    private final ComboBoxValModel m_ReasonModel;

    private final SentenceList m_sentlocations;
    private ComboBoxValModel m_LocationsModel;
    private ComboBoxValModel m_LocationsModelDes;

    private final JInventoryLines m_invlines;

    private int NUMBER_STATE = 0;
    private int MULTIPLY = 0;
    private static final int DEFAULT = 0;
    private static final int ACTIVE = 1;
    private static final int DECIMAL = 2;

    public StockManagement(AppView app) {

        m_App = app;
        m_dlSystem = (DataLogicSystem) m_App.getBean(DataLogicSystem.class.getName());
        m_dlSales = (DataLogicSales) m_App.getBean(DataLogicSales.class.getName());
//        m_TTP = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);

        initComponents();

        btnDownloadProducts.setEnabled(m_App.getDevicePLUs() != null);

        // El modelo de locales
        m_sentlocations = m_dlSales.getLocationsList();
        m_LocationsModel = new ComboBoxValModel();
        m_LocationsModelDes = new ComboBoxValModel();

        m_ReasonModel = new ComboBoxValModel();
        m_ReasonModel.add(MovementReason.IN_PURCHASE);
        m_ReasonModel.add(MovementReason.IN_REFUND);
        m_ReasonModel.add(MovementReason.IN_MOVEMENT);
        m_ReasonModel.add(MovementReason.OUT_SALE);
        m_ReasonModel.add(MovementReason.OUT_REFUND);
        m_ReasonModel.add(MovementReason.OUT_BREAK);
        m_ReasonModel.add(MovementReason.OUT_MOVEMENT);
        m_ReasonModel.add(MovementReason.OUT_CROSSING);

        m_jreason.setModel(m_ReasonModel);

        panelconfig = new PropertiesConfig(m_dlSystem.getResourceAsXML("Ticket.Buttons"));

        m_cat = new JCatalog(m_dlSales, panelconfig);
        m_cat.getComponent().setPreferredSize(new Dimension(0, 245));
        m_cat.addActionListener(new CatalogListener());
        catcontainer.add(m_cat.getComponent(), BorderLayout.CENTER);
        catcontainer.setPreferredSize(new Dimension(
                0,
                Integer.parseInt(panelconfig.getProperty("cat-height", "200"))));

        // Las lineas de inventario
        m_invlines = new JInventoryLines();
        jPanel5.add(m_invlines, BorderLayout.CENTER);
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.StockMovement");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void activate() throws BasicException {
        m_cat.loadCatalog(m_App);

        taxeslogic = new TaxesLogic(m_dlSales.getTaxList().list());

        java.util.List l = m_sentlocations.list();
        m_LocationsModel = new ComboBoxValModel(l);
        m_jLocation.setModel(m_LocationsModel); // para que lo refresque
        m_LocationsModelDes = new ComboBoxValModel(l);
        m_jLocationDes.setModel(m_LocationsModelDes); // para que lo refresque

        stateToInsert();

    }

    public void stateToInsert() {
        // Inicializamos las cajas de texto
        m_jdate.setText(Formats.TIMESTAMP.formatValue(DateUtils.getTodayMinutes()));
        m_ReasonModel.setSelectedItem(MovementReason.IN_PURCHASE);
        m_LocationsModel.setSelectedKey(m_App.getInventoryLocation());
        m_LocationsModelDes.setSelectedKey(m_App.getDefaultInventoryLocation());
        m_invlines.clear();
        m_jcodebar.setText("");
        m_jKeyFactory.setText(null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_jKeyFactory.requestFocus();
            }
        });
    }

    @Override
    public boolean deactivate() {

        if (m_invlines.getCount() > 0) {
            int res = JOptionPane.showConfirmDialog(this, LocalRes.getIntString("message.wannasave"), LocalRes.getIntString("title.editor"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                saveData();
                return true;
            } else {
                return res == JOptionPane.NO_OPTION;
            }
        } else {
            return true;
        }
    }

    private void addLine(ProductInfoExt oProduct, double dpor, double dprice) throws BasicException {
        double dqty = m_dlSales.findProductStock(((LocationInfo) m_LocationsModel.getSelectedItem()).getID(), oProduct.getID(), null);
        m_invlines.addLine(new InventoryLine(oProduct, taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), new Date()), dpor, dprice, dqty));

        if ("true".equals(panelconfig.getProperty("attributesautoset")) == true) {
            openAttributesEditor(m_invlines.getSelectedRow());
        }

    }

    private void deleteLine(int index) {
        if (index < 0) {
            Toolkit.getDefaultToolkit().beep(); // No hay ninguna seleccionada
        } else {
            m_invlines.deleteLine(index);
        }
    }

    private void incProduct(ProductInfoExt product, double units) throws BasicException {
        // precondicion: prod != null

        MovementReason reason = (MovementReason) m_ReasonModel.getSelectedItem();
        addLine(product, units, reason.isInput()
                ? product.getPriceBuy()
                : product.getPriceSell());
    }

    private void incProductByCode(String sCode) {
        incProductByCode(sCode, 1.0);
    }

    private void incProductByCode(String sCode, double dQuantity) {
        // precondicion: sCode != null

        try {
            ProductInfoExt oProduct = m_dlSales.getProductInfoByCode(sCode);
            if (oProduct == null) {
                Toolkit.getDefaultToolkit().beep();
            } else {
                // Se anade directamente una unidad con el precio y todo
                incProduct(oProduct, dQuantity);
            }
        } catch (BasicException eData) {
            MessageInf msg = new MessageInf(eData);
            msg.show(this);
        }
    }

    private void addUnits(double dUnits) {
        int i = m_invlines.getSelectedRow();
        if (i >= 0) {
            InventoryLine inv = m_invlines.getLine(i);
            double dunits = inv.getMultiply() + dUnits;
            if (dunits <= 0.0) {
                deleteLine(i);
            } else {
                inv.setMultiply(inv.getMultiply() + dUnits);
                m_invlines.setLine(i, inv);
            }
        }
    }

    private void setUnits(double dUnits) {
        int i = m_invlines.getSelectedRow();
        if (i >= 0) {
            InventoryLine inv = m_invlines.getLine(i);
            inv.setMultiply(dUnits);
            m_invlines.setLine(i, inv);
        }
    }

    private void stateTransition(char cTrans) {

        if (cTrans == '\u007f') {
            m_jcodebar.setText("");
            NUMBER_STATE = DEFAULT;
        } else if (cTrans == '*') {
            MULTIPLY = ACTIVE;
        } else if (cTrans == '+') {
            if (MULTIPLY != DEFAULT && NUMBER_STATE != DEFAULT) {
                setUnits(Double.parseDouble(m_jcodebar.getText()));
                m_jcodebar.setText("");
            } else {
                if (m_jcodebar.getText().isEmpty()) {
                    addUnits(1.0);
                } else {
                    addUnits(Double.parseDouble(m_jcodebar.getText()));
                    m_jcodebar.setText("");
                }
            }
            NUMBER_STATE = DEFAULT;
            MULTIPLY = DEFAULT;
        } else if (cTrans == '-') {
            if (m_jcodebar.getText() == null || m_jcodebar.getText().equals("")) {
                addUnits(-1.0);
            } else {
                addUnits(-Double.parseDouble(m_jcodebar.getText()));
                m_jcodebar.setText("");
            }
            NUMBER_STATE = DEFAULT;
            MULTIPLY = DEFAULT;
        } else if (cTrans == '.') {
            if (m_jcodebar.getText() == null || m_jcodebar.getText().equals("")) {
                m_jcodebar.setText("0.");
            } else if (NUMBER_STATE != DECIMAL) {
                m_jcodebar.setText(m_jcodebar.getText() + cTrans);
            }
            NUMBER_STATE = DECIMAL;
        } else if (cTrans == ' ' || cTrans == '=') {
            if (m_invlines.getCount() == 0) {
                // No podemos grabar, no hay ningun registro.
                Toolkit.getDefaultToolkit().beep();
            } else {
                int res = JOptionPane.showConfirmDialog(this, LocalRes.getIntString("message.wannapost"), LocalRes.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    saveData();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        } else if (Character.isDigit(cTrans)) {
            m_jcodebar.setText(m_jcodebar.getText() + cTrans);
            if (NUMBER_STATE != DECIMAL) {
                NUMBER_STATE = ACTIVE;
            }
        } else if (cTrans == '\n') {
            String sCode = m_jcodebar.getText();
            incProductByCode(sCode);
            m_jcodebar.setText("");
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void saveData() {
        try {

            Date d = (Date) Formats.TIMESTAMP.parseValue(m_jdate.getText());
            MovementReason reason = (MovementReason) m_ReasonModel.getSelectedItem();

            if (reason == MovementReason.OUT_CROSSING) {
                // Es una doble entrada
                saveData(new InventoryRecord(
                        d, MovementReason.OUT_MOVEMENT,
                        (LocationInfo) m_LocationsModel.getSelectedItem(),
                        m_invlines.getLines()
                ));
                saveData(new InventoryRecord(
                        d, MovementReason.IN_MOVEMENT,
                        (LocationInfo) m_LocationsModelDes.getSelectedItem(),
                        m_invlines.getLines()
                ));
            } else {
                // Es un movimiento
                saveData(new InventoryRecord(
                        d, reason,
                        (LocationInfo) m_LocationsModel.getSelectedItem(),
                        m_invlines.getLines()
                ));
            }

            stateToInsert();
        } catch (BasicException eData) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotsaveinventorydata"), eData);
            msg.show(this);
        }
    }

    private void saveData(InventoryRecord rec) throws BasicException {

        // A grabar.
        SentenceExec sent = m_dlSales.getStockDiaryInsert();

        for (InventoryLine inv : mergeInventoryLine(rec.getLines())) {
            sent.exec(new Object[]{
                UUID.randomUUID().toString(),
                rec.getDate(),
                rec.getReason().getKey(),
                rec.getLocation().getID(),
                inv.getProductID(),
                inv.getProductAttSetInstId(),
                rec.getReason().samesignum(inv.getMultiply()),
                inv.getPriceBuy()
            });
        }

        // si se ha grabado se imprime, si no, no.
        printTicket(rec);
    }

    private void printTicket(InventoryRecord invrec) {

        InputStream schema = getClass().getResourceAsStream(PRINTER_SHEMA);
        InputStream template = getClass().getResourceAsStream(PRINT_INVENTORY);

        if (schema == null || template == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            m_TTP = new TicketParser(schema, m_App.getDeviceTicket());
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("inventoryrecord", invrec);
                script.put("local", new AppLocal());
                m_TTP.printTicket(template, script);
            } catch (ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    private void printStockPreview(InventoryRecord invrec) throws TicketPrinterException, ScriptException {
        InputStream schema = getClass().getResourceAsStream(PRINTER_SHEMA);
        InputStream template = getClass().getResourceAsStream(PRINT_STOCK_PREVIEW);
        if (schema == null || template == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            m_TTP = new TicketParser(schema, m_App.getDeviceTicket());
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("inventoryrecord", invrec);
                invrec.getLocation().getName();
                script.put("local", new AppLocal());
                m_TTP.printTicket(template, script);
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            } catch (TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
                throw e;
            }
        }
    }

    private List<InventoryLine> mergeInventoryLine(List<InventoryLine> lines) {
        InventoryLine current_stockmgtline;
        InventoryLine loop_stockmgtline;
        double loop_unitsm;
        double current_unitsm;
        int numlinessm = lines.size();
        for (int icounter = 0; icounter < numlinessm; icounter++) {

            current_stockmgtline = lines.get(icounter);
            current_unitsm = current_stockmgtline.getMultiply();

            if (current_unitsm != 0) {
                for (int jcounter = icounter + 1; jcounter < numlinessm; jcounter++) {
                    loop_stockmgtline = lines.get(jcounter);
                    loop_unitsm = loop_stockmgtline.getMultiply();
                    String current_productidsm = current_stockmgtline.getProductID();
                    String current_productatt = current_stockmgtline.getProductAttSetInstId();
                    String loop_productidsm = loop_stockmgtline.getProductID();
                    String loop_productatt = loop_stockmgtline.getProductAttSetInstId();
                    if (loop_productidsm.equals(current_productidsm) && (loop_unitsm != 0) && ((loop_productatt != null && loop_productatt.equals(current_productatt)) || (loop_productatt == null) && (current_productatt == null))) {
                        current_unitsm = current_unitsm + loop_unitsm;
                        loop_stockmgtline.setMultiply(0);
                    }
                }
                current_stockmgtline.setMultiply(current_unitsm);
            }
        }
        for (int icounter = numlinessm - 1; icounter > 0; icounter--) {
            loop_stockmgtline = lines.get(icounter);
            loop_unitsm = loop_stockmgtline.getMultiply();
            if (loop_unitsm == 0) {
                lines.remove(icounter);
            }
        }
        return lines;
    }

    private class CatalogListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (!m_jcodebar.getText().isEmpty()) {

                    String sQty = m_jcodebar.getText();
                    Double dQty = (Double.valueOf(sQty) == 0) ? 1.0 : Double.valueOf(sQty);
                    incProduct((ProductInfoExt) e.getSource(), dQty);
                    m_jcodebar.setText("");

                } else {
                    incProduct((ProductInfoExt) e.getSource(), 1.0);
                }
            } catch (BasicException ex) {
                Logger.getLogger(StockManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void openAttributesEditor(int index) {
        InventoryLine line = m_invlines.getLine(index);
        if (line.getProductAttSetId() != null) {
            try {

                JProductAttEdit attedit = JProductAttEdit.getAttributesEditor(this, m_App.getSession());
                attedit.editAttributes(line.getProductAttSetId(), line.getProductAttSetInstId());
                attedit.setVisible(true);
                if (attedit.isOK()) {
                    // The user pressed OK
                    line.setProductAttSetInstId(attedit.getAttributeSetInst());
                    line.setProductAttSetInstDesc(attedit.getAttributeSetInstDescription());
                    double dqty = m_dlSales.findProductStock(((LocationInfo) m_LocationsModel.getSelectedItem()).getID(), line.getProductID(), line.getProductAttSetInstId());
                    line.setStockQty(dqty);
                    m_invlines.setLine(index, line);
                }
            } catch (BasicException ex) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindattributes"), ex);
                msg.show(this);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        m_jbtnStockPreview = new javax.swing.JButton();
        jNumberKeys = new com.openbravo.beans.JNumberKeys();
        jPanel4 = new javax.swing.JPanel();
        m_jEnter = new javax.swing.JButton();
        m_jcodebar = new javax.swing.JLabel();
        m_jKeyFactory = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        btnDownloadProducts = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        m_jdate = new javax.swing.JTextField();
        m_jbtndate = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        m_jreason = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        m_jLocation = new javax.swing.JComboBox();
        m_jLocationDes = new javax.swing.JComboBox();
        jPanel9 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        m_jDelete = new javax.swing.JButton();
        m_jUp = new javax.swing.JButton();
        m_jDown = new javax.swing.JButton();
        m_jEditLine = new javax.swing.JButton();
        jEditAttributes = new javax.swing.JButton();
        catcontainer = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        m_jbtnStockPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/greenled.png"))); // NOI18N
        m_jbtnStockPreview.setText(AppLocal.getIntString("button.stockpreview")); // NOI18N
        m_jbtnStockPreview.setFocusPainted(false);
        m_jbtnStockPreview.setFocusable(false);
        m_jbtnStockPreview.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnStockPreview.setRequestFocusEnabled(false);
        m_jbtnStockPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnStockPreviewActionPerformed(evt);
            }
        });
        jPanel10.add(m_jbtnStockPreview);

        jPanel2.add(jPanel10);

        jNumberKeys.addJNumberEventListener(new com.openbravo.beans.JNumberEventListener() {
            public void keyPerformed(com.openbravo.beans.JNumberEvent evt) {
                jNumberKeysKeyPerformed(evt);
            }
        });
        jPanel2.add(jNumberKeys);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        m_jEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/barcode.png"))); // NOI18N
        m_jEnter.setFocusPainted(false);
        m_jEnter.setFocusable(false);
        m_jEnter.setRequestFocusEnabled(false);
        m_jEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEnterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(m_jEnter, gridBagConstraints);

        m_jcodebar.setBackground(java.awt.Color.white);
        m_jcodebar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jcodebar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        m_jcodebar.setOpaque(true);
        m_jcodebar.setPreferredSize(new java.awt.Dimension(135, 30));
        m_jcodebar.setRequestFocusEnabled(false);
        m_jcodebar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_jcodebarMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel4.add(m_jcodebar, gridBagConstraints);

        m_jKeyFactory.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setBorder(null);
        m_jKeyFactory.setCaretColor(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setPreferredSize(new java.awt.Dimension(1, 1));
        m_jKeyFactory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jKeyFactoryKeyTyped(evt);
            }
        });
        jPanel4.add(m_jKeyFactory, new java.awt.GridBagConstraints());

        jPanel2.add(jPanel4);

        btnDownloadProducts.setText(AppLocal.getIntString("button.downloadplu")); // NOI18N
        btnDownloadProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadProductsActionPerformed(evt);
            }
        });
        jPanel6.add(btnDownloadProducts);

        jPanel2.add(jPanel6);

        jPanel1.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        add(jPanel1, java.awt.BorderLayout.EAST);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel1.setText(AppLocal.getIntString("label.stockdate")); // NOI18N

        m_jdate.setEditable(false);
        m_jdate.setEnabled(false);

        m_jbtndate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtndate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtndateActionPerformed(evt);
            }
        });

        jLabel2.setText(AppLocal.getIntString("label.stockreason")); // NOI18N

        m_jreason.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jreasonActionPerformed(evt);
            }
        });

        jLabel8.setText(AppLocal.getIntString("label.warehouse")); // NOI18N

        m_jLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jLocationActionPerformed(evt);
            }
        });

        m_jLocationDes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jLocationDesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(m_jdate, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jLocation, 0, 175, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jbtndate)
                            .addComponent(m_jLocationDes, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(m_jreason, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(99, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(m_jdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jbtndate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(m_jreason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(m_jLocationDes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(m_jLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.add(jPanel8, java.awt.BorderLayout.NORTH);

        jPanel5.setLayout(new java.awt.BorderLayout());

        m_jDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/locationbar_erase.png"))); // NOI18N
        m_jDelete.setFocusPainted(false);
        m_jDelete.setFocusable(false);
        m_jDelete.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDelete.setMaximumSize(new java.awt.Dimension(54, 44));
        m_jDelete.setMinimumSize(new java.awt.Dimension(54, 44));
        m_jDelete.setPreferredSize(new java.awt.Dimension(54, 44));
        m_jDelete.setRequestFocusEnabled(false);
        m_jDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDeleteActionPerformed(evt);
            }
        });
        jPanel7.add(m_jDelete);

        m_jUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1uparrow22.png"))); // NOI18N
        m_jUp.setFocusPainted(false);
        m_jUp.setFocusable(false);
        m_jUp.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jUp.setMaximumSize(new java.awt.Dimension(54, 44));
        m_jUp.setMinimumSize(new java.awt.Dimension(54, 44));
        m_jUp.setPreferredSize(new java.awt.Dimension(54, 44));
        m_jUp.setRequestFocusEnabled(false);
        m_jUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jUpActionPerformed(evt);
            }
        });
        jPanel7.add(m_jUp);

        m_jDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1downarrow22.png"))); // NOI18N
        m_jDown.setFocusPainted(false);
        m_jDown.setFocusable(false);
        m_jDown.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDown.setMaximumSize(new java.awt.Dimension(54, 44));
        m_jDown.setMinimumSize(new java.awt.Dimension(54, 44));
        m_jDown.setPreferredSize(new java.awt.Dimension(54, 44));
        m_jDown.setRequestFocusEnabled(false);
        m_jDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDownActionPerformed(evt);
            }
        });
        jPanel7.add(m_jDown);

        m_jEditLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/color_line.png"))); // NOI18N
        m_jEditLine.setFocusPainted(false);
        m_jEditLine.setFocusable(false);
        m_jEditLine.setMaximumSize(new java.awt.Dimension(54, 44));
        m_jEditLine.setMinimumSize(new java.awt.Dimension(54, 44));
        m_jEditLine.setPreferredSize(new java.awt.Dimension(54, 44));
        m_jEditLine.setRequestFocusEnabled(false);
        m_jEditLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEditLineActionPerformed(evt);
            }
        });
        jPanel7.add(m_jEditLine);

        jEditAttributes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/colorize.png"))); // NOI18N
        jEditAttributes.setFocusPainted(false);
        jEditAttributes.setFocusable(false);
        jEditAttributes.setMargin(new java.awt.Insets(8, 14, 8, 14));
        jEditAttributes.setMaximumSize(new java.awt.Dimension(54, 44));
        jEditAttributes.setMinimumSize(new java.awt.Dimension(54, 44));
        jEditAttributes.setPreferredSize(new java.awt.Dimension(54, 44));
        jEditAttributes.setRequestFocusEnabled(false);
        jEditAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditAttributesActionPerformed(evt);
            }
        });
        jPanel7.add(jEditAttributes);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jPanel3.add(jPanel9, java.awt.BorderLayout.CENTER);

        add(jPanel3, java.awt.BorderLayout.CENTER);

        catcontainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        catcontainer.setLayout(new java.awt.BorderLayout());
        add(catcontainer, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDownloadProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadProductsActionPerformed

        // Ejecutamos la descarga...
        DeviceInputOutput s = m_App.getDevicePLUs();
        try {
            s.connectDevice();
            s.startDownloadProduct();

            ProductIO p = s.recieveProduct();
            while (p != null) {
                incProductByCode(p.getCode(), p.getQuantity());
                p = s.recieveProduct();
            }
            // MessageInf msg = new MessageInf(MessageInf.SGN_SUCCESS, "Se ha subido con exito la lista de productos al ScanPal.");
            // msg.show(this);
        } catch (DeviceInputOutputException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.scannerfail2"), e);
            msg.show(this);
        } finally {
            s.disconnectDevice();
        }

    }//GEN-LAST:event_btnDownloadProductsActionPerformed

    private void m_jreasonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jreasonActionPerformed

        m_jLocationDes.setEnabled(m_ReasonModel.getSelectedItem() == MovementReason.OUT_CROSSING);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_jKeyFactory.requestFocus();
            }
        });
    }//GEN-LAST:event_m_jreasonActionPerformed

    private void m_jDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDownActionPerformed

        m_invlines.goDown();

    }//GEN-LAST:event_m_jDownActionPerformed

    private void m_jUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jUpActionPerformed

        m_invlines.goUp();

    }//GEN-LAST:event_m_jUpActionPerformed

    private void m_jDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDeleteActionPerformed

        deleteLine(m_invlines.getSelectedRow());

    }//GEN-LAST:event_m_jDeleteActionPerformed

    private void m_jEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEnterActionPerformed

        incProductByCode(m_jcodebar.getText());
        m_jcodebar.setText("");

    }//GEN-LAST:event_m_jEnterActionPerformed

    private void m_jbtndateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtndateActionPerformed

        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(m_jdate.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTime(this, date);
        if (date != null) {
            m_jdate.setText(Formats.TIMESTAMP.formatValue(date));
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_jKeyFactory.requestFocus();
            }
        });
    }//GEN-LAST:event_m_jbtndateActionPerformed

    private void jNumberKeysKeyPerformed(com.openbravo.beans.JNumberEvent evt) {//GEN-FIRST:event_jNumberKeysKeyPerformed

        stateTransition(evt.getKey());

    }//GEN-LAST:event_jNumberKeysKeyPerformed

private void m_jcodebarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_jcodebarMouseClicked
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            m_jKeyFactory.requestFocus();
        }
    });
}//GEN-LAST:event_m_jcodebarMouseClicked

private void jEditAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditAttributesActionPerformed

    int i = m_invlines.getSelectedRow();
    if (i < 0) {
        Toolkit.getDefaultToolkit().beep(); // no line selected
    } else {
        openAttributesEditor(i);
    }
}//GEN-LAST:event_jEditAttributesActionPerformed

    private void m_jKeyFactoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jKeyFactoryKeyTyped
        m_jKeyFactory.setText(null);
        stateTransition(evt.getKeyChar());
    }//GEN-LAST:event_m_jKeyFactoryKeyTyped

    private void m_jLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jLocationActionPerformed
        try {
            int i = 0;
            String locationId = ((LocationInfo) m_LocationsModel.getSelectedItem()).getID();
            for (InventoryLine line : m_invlines.getLines()) {
                line.setStockQty(m_dlSales.findProductStock(locationId, line.getProductID(), line.getProductAttSetInstId()));
                m_invlines.setLine(i, line);
                i++;
            }
        } catch (BasicException ex) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindattributes"), ex);
            msg.show(this);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_jKeyFactory.requestFocus();
            }
        });
    }//GEN-LAST:event_m_jLocationActionPerformed

    private void m_jLocationDesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jLocationDesActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_jKeyFactory.requestFocus();
            }
        });
    }//GEN-LAST:event_m_jLocationDesActionPerformed

    private void m_jbtnStockPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnStockPreviewActionPerformed

        try {
            MovementReason reason = (MovementReason) m_ReasonModel.getSelectedItem();

            printStockPreview(new InventoryRecord(
                    new Date(), reason == MovementReason.OUT_CROSSING ? MovementReason.OUT_MOVEMENT : reason,
                    (LocationInfo) m_LocationsModel.getSelectedItem(),
                    mergeInventoryLine(m_invlines.getLines())));
        } catch (TicketPrinterException | ScriptException ex) {
            Logger.getLogger(StockManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_m_jbtnStockPreviewActionPerformed

    private void m_jEditLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEditLineActionPerformed

        int i = m_invlines.getSelectedRow();
        if (i < 0) {
            Toolkit.getDefaultToolkit().beep(); // no line selected
        } else {
            try {
                InventoryLine newline = JInventoryLineEdit.showMessage(this, m_App, m_invlines.getLine(i));
                if (newline != null) {
                    // line has been modified
                    m_invlines.setLine(i, newline);
                }
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
        }
    }//GEN-LAST:event_m_jEditLineActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDownloadProducts;
    private javax.swing.JPanel catcontainer;
    private javax.swing.JButton jEditAttributes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel8;
    private com.openbravo.beans.JNumberKeys jNumberKeys;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JButton m_jDelete;
    private javax.swing.JButton m_jDown;
    private javax.swing.JButton m_jEditLine;
    private javax.swing.JButton m_jEnter;
    private javax.swing.JTextField m_jKeyFactory;
    private javax.swing.JComboBox m_jLocation;
    private javax.swing.JComboBox m_jLocationDes;
    private javax.swing.JButton m_jUp;
    private javax.swing.JButton m_jbtnStockPreview;
    private javax.swing.JButton m_jbtndate;
    private javax.swing.JLabel m_jcodebar;
    private javax.swing.JTextField m_jdate;
    private javax.swing.JComboBox m_jreason;
    // End of variables declaration//GEN-END:variables

}
