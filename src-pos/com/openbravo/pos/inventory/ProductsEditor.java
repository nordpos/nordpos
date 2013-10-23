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
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.SentenceFind;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.printer.TicketFiscalPrinterException;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.sales.TaxesLogic;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.ticket.ProductInfoEdit;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class ProductsEditor extends JPanel implements EditorRecord {

    private String s_GenRef;
    private String s_GenBarcode;
    private String s_DefBarcode;
    private String s_DefTaxCat;
    private String s_DefProdCat;

    private SentenceList product;

    private SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;

    private SentenceList taxcatsent;
    private ComboBoxValModel taxcatmodel;

    private SentenceList attsent;
    private ComboBoxValModel attmodel;

    private SentenceList taxsent;
    private TaxesLogic taxeslogic;

    private SentenceFind loadimage;

    private ComboBoxValModel m_CodetypeModel;

    private Object m_id;
    private Object pricesell;
    private boolean priceselllock = false;

    private boolean reportlock = false;

    private DataLogicSales m_dSales;
    private DataLogicSystem m_dSystem;
    private TicketParser m_TTP;

    private ProductInfoEdit m_oCurrentProductEdit;

    private AppView m_App;

    public ProductsEditor(AppView app, DataLogicSystem dlSystem, DataLogicSales dlSales, DirtyManager dirty) {
        initComponents();
        m_App = app;
        m_dSales = dlSales;
        m_dSystem = dlSystem;
        m_TTP = new TicketParser(m_App.getDeviceTicket(), dlSystem);

        loadimage = dlSales.getProductImage();

        product = dlSales.getProductList();

        // The taxes sentence
        taxsent = dlSales.getTaxList();

        // The categories model
        m_sentcat = dlSales.getCategoriesList();
        m_CategoryModel = new ComboBoxValModel();

        // The taxes model
        taxcatsent = dlSales.getTaxCategoriesList();
        taxcatmodel = new ComboBoxValModel();

        // The attributes model
        attsent = dlSales.getAttributeSetList();
        attmodel = new ComboBoxValModel();

        m_CodetypeModel = new ComboBoxValModel();
        m_CodetypeModel.add(null);
        m_CodetypeModel.add(CodeType.EAN13);
        m_CodetypeModel.add(CodeType.CODE128);
        m_jCodetype.setModel(m_CodetypeModel);
        m_jCodetype.setVisible(false);

        m_jRef.getDocument().addDocumentListener(dirty);
        m_jCode.getDocument().addDocumentListener(dirty);
        m_jName.getDocument().addDocumentListener(dirty);
        m_jComment.addActionListener(dirty);
        m_jScale.addActionListener(dirty);
        m_jCategory.addActionListener(dirty);
        m_jTax.addActionListener(dirty);
        m_jAtt.addActionListener(dirty);
        m_jPriceBuy.getDocument().addDocumentListener(dirty);
        m_jPriceSell.getDocument().addDocumentListener(dirty);
        m_jImage.addPropertyChangeListener("image", dirty);
        m_jstockcost.getDocument().addDocumentListener(dirty);
        m_jstockvolume.getDocument().addDocumentListener(dirty);
        m_jInCatalog.addActionListener(dirty);
        m_jCatalogOrder.getDocument().addDocumentListener(dirty);
        txtAttributes.getDocument().addDocumentListener(dirty);

        FieldsManager fm = new FieldsManager();
        m_jPriceBuy.getDocument().addDocumentListener(fm);
        m_jPriceSell.getDocument().addDocumentListener(new PriceSellManager());
        m_jTax.addActionListener(fm);

        m_jPriceSellTax.getDocument().addDocumentListener(new PriceTaxManager());
        m_jmargin.getDocument().addDocumentListener(new MarginManager());

        txtAttributes.setAntiAliasingEnabled(true);
        txtAttributes.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);

        writeValueEOF();
    }

    public void activate() throws BasicException {

        s_GenRef = m_App.getGenerateProductReference();
        s_GenBarcode = m_App.getGenerateProductBarcode();
        s_DefBarcode = m_App.getUserBarcode();
        s_DefTaxCat = m_App.getDefaultTaxCategory();
        s_DefProdCat = m_App.getDefaultProductCategory();

        // Load the taxes logic
        taxeslogic = new TaxesLogic(taxsent.list());

        m_CategoryModel = new ComboBoxValModel(m_sentcat.list());
        m_jCategory.setModel(m_CategoryModel);

        taxcatmodel = new ComboBoxValModel(taxcatsent.list());
        m_jTax.setModel(taxcatmodel);

        attmodel = new ComboBoxValModel(attsent.list());
        attmodel.add(0, null);
        m_jAtt.setModel(attmodel);

        m_oCurrentProductEdit = new ProductInfoEdit();
    }

    public void refresh() {

    }

    public void writeValueEOF() {

        reportlock = true;
        // Los valores
        m_jTitle.setText(AppLocal.getIntString("label.recordeof"));
        m_id = null;
        m_jRef.setText(null);
        m_jCode.setText(null);
        m_jName.setText(null);
        m_jComment.setSelected(false);
        m_jScale.setSelected(false);
        m_CategoryModel.setSelectedKey(null);
        taxcatmodel.setSelectedKey(null);
        attmodel.setSelectedKey(null);
        m_jPriceBuy.setText(null);
        setPriceSell(null);
        m_jImage.setImage(null);
        m_jstockcost.setText(null);
        m_jstockvolume.setText(null);
        m_jInCatalog.setSelected(false);
        m_jCatalogOrder.setText(null);
        txtAttributes.setText(null);
        reportlock = false;

        // Los habilitados
        m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jName.setEnabled(false);
        m_jComment.setEnabled(false);
        m_jScale.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jTax.setEnabled(false);
        m_jAtt.setEnabled(false);
        m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
        m_jPriceSellTax.setEnabled(false);
        m_jmargin.setEnabled(false);
        m_jImage.setEnabled(false);
        m_jstockcost.setEnabled(false);
        m_jstockvolume.setEnabled(false);
        m_jInCatalog.setEnabled(false);
        m_jCatalogOrder.setEnabled(false);
        txtAttributes.setEnabled(false);

        calculateMargin();
        calculatePriceSellTax();
    }
    public void writeValueInsert() {

        reportlock = true;
        // Los valores
        m_jTitle.setText(AppLocal.getIntString("label.recordnew"));
        m_id = UUID.randomUUID().toString();

        String GenCode = "";

        try {
            if (product.list() != null) {
                GenCode = Integer.toString(product.list().size() + 1);
            } else {
                GenCode = "1";
            }
        } catch (BasicException ex) {
        }

        if (s_GenRef.equals("true")) {
            for (int i = GenCode.length(); i < 4; i++) {
                GenCode = "0".concat(GenCode);
            }
            m_jRef.setText(GenCode);
        } else {
            m_jRef.setText(null);
        }

        if (s_GenBarcode.equals("true")) {

            for (int i = GenCode.length(); i < 9; i++) {
                GenCode = "0".concat(GenCode);
            }

            GenCode = s_DefBarcode.concat(GenCode);

            int iBCC = 0;

            for (int i = 0; i < 12; i++) {
                if (i == 1 || i == 3 || i == 5 || i == 7 || i == 9 || i == 11) {
                    iBCC = iBCC + Integer.parseInt(GenCode.substring(i, i + 1)) * 3;
                } else {
                    iBCC = iBCC + Integer.parseInt(GenCode.substring(i, i + 1));
                }
            }

            if (iBCC > 9) {
                iBCC = 10 - Integer.parseInt(Integer.toString(iBCC).substring(1, 2));
                if (iBCC == 10) {
                    iBCC = 0;
                }
            } else {
                iBCC = 10 - Integer.parseInt(Integer.toString(iBCC).substring(0, 1));
            }

            GenCode = GenCode.concat(Integer.toString(iBCC));

            m_jCode.setText(GenCode);
        } else {
            m_jCode.setText(null);
        }

        m_jName.setText(null);
        m_jComment.setSelected(false);
        m_jScale.setSelected(false);
        m_CategoryModel.setSelectedKey(s_DefProdCat);
        taxcatmodel.setSelectedKey(s_DefTaxCat);
        attmodel.setSelectedKey(null);
        m_jPriceBuy.setText(null);
        setPriceSell(null);
        m_jImage.setImage(null);
        m_jstockcost.setText(null);
        m_jstockvolume.setText(null);
        m_jInCatalog.setSelected(true);
        m_jCatalogOrder.setText(null);
        txtAttributes.setText(null);
        reportlock = false;

        // Los habilitados
        m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jName.setEnabled(true);
        m_jComment.setEnabled(true);
        m_jScale.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jAtt.setEnabled(true);
        m_jPriceBuy.setEnabled(true);
        m_jPriceSell.setEnabled(true);
        m_jPriceSellTax.setEnabled(true);
        m_jmargin.setEnabled(true);
        m_jImage.setEnabled(true);
        m_jstockcost.setEnabled(true);
        m_jstockvolume.setEnabled(true);
        m_jInCatalog.setEnabled(true);
        m_jCatalogOrder.setEnabled(false);
        txtAttributes.setEnabled(true);

        calculateMargin();
        calculatePriceSellTax();
   }

    public void writeValueDelete(Object value) {

        reportlock = true;
        Object[] myprod = (Object[]) value;
        m_jTitle.setText(Formats.STRING.formatValue(myprod[1]) + " - " + Formats.STRING.formatValue(myprod[3]) + " " + AppLocal.getIntString("label.recorddeleted"));
        m_id = myprod[0];
        m_jRef.setText(Formats.STRING.formatValue(myprod[1]));
        m_jCode.setText(Formats.STRING.formatValue(myprod[2]));
        m_jName.setText(Formats.STRING.formatValue(myprod[3]));
        m_jComment.setSelected(((Boolean)myprod[4]).booleanValue());
        m_jScale.setSelected(((Boolean)myprod[5]).booleanValue());
        m_jPriceBuy.setText(Formats.CURRENCY.formatValue(myprod[6]));
        setPriceSell(myprod[7]);
        m_CategoryModel.setSelectedKey(myprod[8]);
        taxcatmodel.setSelectedKey(myprod[9]);
        attmodel.setSelectedKey(myprod[10]);
        m_jImage.setImage(findImage(m_id));
        m_jstockcost.setText(Formats.CURRENCY.formatValue(myprod[12]));
        m_jstockvolume.setText(Formats.DOUBLE.formatValue(myprod[13]));
        m_jInCatalog.setSelected(((Boolean)myprod[14]).booleanValue());
        m_jCatalogOrder.setText(Formats.INT.formatValue(myprod[15]));
        txtAttributes.setText(Formats.BYTEA.formatValue(myprod[16]));
        txtAttributes.setCaretPosition(0);
        reportlock = false;

        // Los habilitados
        m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jName.setEnabled(false);
        m_jComment.setEnabled(false);
        m_jScale.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jTax.setEnabled(false);
        m_jAtt.setEnabled(false);
        m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
        m_jPriceSellTax.setEnabled(false);
        m_jmargin.setEnabled(false);
        m_jImage.setEnabled(false);
        m_jstockcost.setEnabled(false);
        m_jstockvolume.setEnabled(false);
        m_jInCatalog.setEnabled(false);
        m_jCatalogOrder.setEnabled(false);
        txtAttributes.setEnabled(false);

        calculateMargin();
        calculatePriceSellTax();
    }

    public void writeValueEdit(Object value) {

        reportlock = true;
        Object[] myprod = (Object[]) value;
        m_jTitle.setText(Formats.STRING.formatValue(myprod[1]) + " - " + Formats.STRING.formatValue(myprod[3]));
        m_id = myprod[0];
        m_jRef.setText(Formats.STRING.formatValue(myprod[1]));
        m_jCode.setText(Formats.STRING.formatValue(myprod[2]));
        m_jName.setText(Formats.STRING.formatValue(myprod[3]));
        m_jComment.setSelected(((Boolean)myprod[4]).booleanValue());
        m_jScale.setSelected(((Boolean)myprod[5]).booleanValue());
        m_jPriceBuy.setText(Formats.CURRENCY.formatValue(myprod[6]));
        setPriceSell(myprod[7]);
        m_CategoryModel.setSelectedKey(myprod[8]);
        taxcatmodel.setSelectedKey(myprod[9]);
        attmodel.setSelectedKey(myprod[10]);
        m_jImage.setImage(findImage(m_id));
        m_jstockcost.setText(Formats.CURRENCY.formatValue(myprod[12]));
        m_jstockvolume.setText(Formats.DOUBLE.formatValue(myprod[13]));
        m_jInCatalog.setSelected(((Boolean)myprod[14]).booleanValue());
        m_jCatalogOrder.setText(Formats.INT.formatValue(myprod[15]));
        txtAttributes.setText(Formats.BYTEA.formatValue(myprod[16]));
        txtAttributes.setCaretPosition(0);
        reportlock = false;

        // Los habilitados
        m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jName.setEnabled(true);
        m_jComment.setEnabled(true);
        m_jScale.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jAtt.setEnabled(true);
        m_jPriceBuy.setEnabled(true);
        m_jPriceSell.setEnabled(true);
        m_jPriceSellTax.setEnabled(true);
        m_jmargin.setEnabled(true);
        m_jImage.setEnabled(true);
        m_jstockcost.setEnabled(true);
        m_jstockvolume.setEnabled(true);
        m_jInCatalog.setEnabled(true);
        m_jCatalogOrder.setEnabled(m_jInCatalog.isSelected());
        txtAttributes.setEnabled(true);

        calculateMargin();
        calculatePriceSellTax();
    }

    public Object createValue() throws BasicException {

        Object[] myprod = new Object[17];
        myprod[0] = m_id;
        myprod[1] = m_jRef.getText();
        myprod[2] = m_jCode.getText();
        myprod[3] = m_jName.getText();
        myprod[4] = Boolean.valueOf(m_jComment.isSelected());
        myprod[5] = Boolean.valueOf(m_jScale.isSelected());
        myprod[6] = Formats.CURRENCY.parseValue(m_jPriceBuy.getText());
        myprod[7] = pricesell;
        myprod[8] = m_CategoryModel.getSelectedKey();
        myprod[9] = taxcatmodel.getSelectedKey();
        myprod[10] = attmodel.getSelectedKey();
        myprod[11] = m_jImage.getImage();
        myprod[12] = Formats.CURRENCY.parseValue(m_jstockcost.getText());
        myprod[13] = Formats.DOUBLE.parseValue(m_jstockvolume.getText());
        myprod[14] = Boolean.valueOf(m_jInCatalog.isSelected());
        myprod[15] = Formats.INT.parseValue(m_jCatalogOrder.getText());
        myprod[16] = Formats.BYTEA.parseValue(txtAttributes.getText());

        return myprod;
    }

    public Component getComponent() {
        return this;
    }

    private BufferedImage findImage(Object id) {
        try {
            return (BufferedImage) loadimage.find(id);
        } catch (BasicException e) {
            return null;
        }
    }

    private void calculateMargin() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dPriceSell = (Double) pricesell;

            if (dPriceBuy == null || dPriceSell == null) {
                m_jmargin.setText(null);
            } else {
                m_jmargin.setText(Formats.PERCENT.formatValue(new Double(dPriceSell.doubleValue() / dPriceBuy.doubleValue() - 1.0)));
            }
            reportlock = false;
        }
    }

    private void calculatePriceSellTax() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceSell = (Double) pricesell;

            if (dPriceSell == null) {
                m_jPriceSellTax.setText(null);
            } else {
                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem(), new Date());
                m_jPriceSellTax.setText(Formats.CURRENCY.formatValue(new Double(dPriceSell.doubleValue() * (1.0 + dTaxRate))));
            }
            reportlock = false;
        }
    }

    private void calculatePriceSellfromMargin() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dMargin = readPercent(m_jmargin.getText());

            if (dMargin == null || dPriceBuy == null) {
                setPriceSell(null);
            } else {
                setPriceSell(new Double(dPriceBuy.doubleValue() * (1.0 + dMargin.doubleValue())));
            }

            reportlock = false;
        }

    }

    private void calculatePriceSellfromPST() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceSellTax = readCurrency(m_jPriceSellTax.getText());

            if (dPriceSellTax == null) {
                setPriceSell(null);
            } else {
                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem(), new Date());
                setPriceSell(new Double(dPriceSellTax.doubleValue() / (1.0 + dTaxRate)));
            }

            reportlock = false;
        }
    }

    private void setPriceSell(Object value) {

        if (!priceselllock) {
            priceselllock = true;
            pricesell = value;
            m_jPriceSell.setText(Formats.CURRENCY.formatValue(pricesell));
            priceselllock = false;
        }
    }

    private class PriceSellManager implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
        }
        public void insertUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
        }
        public void removeUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
        }
    }

    private class FieldsManager implements DocumentListener, ActionListener {
        public void changedUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
        }
        public void insertUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
        }
        public void removeUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
        }
        public void actionPerformed(ActionEvent e) {
            calculateMargin();
            calculatePriceSellTax();
        }
    }

    private class PriceTaxManager implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
        }
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
        }
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
        }
    }

    private class MarginManager implements DocumentListener  {
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
        }
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
        }
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
        }
    }

    private final static Double readCurrency(String sValue) {
        try {
            return (Double) Formats.CURRENCY.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }

    private final static Double readPercent(String sValue) {
        try {
            return (Double) Formats.PERCENT.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }

    private void printProductLabel(ProductInfoEdit productinfo) throws TicketPrinterException, ScriptException, TicketFiscalPrinterException {
        String sresource = m_dSystem.getResourceAsXML("Printer.ProductLabel");
        if (sresource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("product", productinfo);
                m_TTP.printTicket(m_App, script.eval(sresource).toString());
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        m_jRef = new javax.swing.JTextField();
        m_jName = new javax.swing.JTextField();
        m_jTitle = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        m_jCode = new javax.swing.JTextField();
        m_jImage = new com.openbravo.data.gui.JImageEditor();
        jLabel3 = new javax.swing.JLabel();
        m_jPriceBuy = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_jPriceSell = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        m_jCategory = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        m_jTax = new javax.swing.JComboBox();
        m_jmargin = new javax.swing.JTextField();
        m_jPriceSellTax = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        m_jCodetype = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        m_jAtt = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        m_jstockcost = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        m_jstockvolume = new javax.swing.JTextField();
        m_jScale = new javax.swing.JCheckBox();
        m_jComment = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        m_jCatalogOrder = new javax.swing.JTextField();
        m_jInCatalog = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new org.fife.ui.rtextarea.RTextScrollPane();
        txtAttributes = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        m_jPrintLabel = new javax.swing.JButton();
        m_jVirtualKeyboard = new javax.swing.JButton();

        setLayout(null);

        jLabel1.setText(AppLocal.getIntString("label.prodref")); // NOI18N
        add(jLabel1);
        jLabel1.setBounds(10, 50, 80, 30);

        jLabel2.setText(AppLocal.getIntString("label.prodname")); // NOI18N
        add(jLabel2);
        jLabel2.setBounds(180, 50, 70, 30);
        add(m_jRef);
        m_jRef.setBounds(90, 50, 70, 30);
        add(m_jName);
        m_jName.setBounds(250, 50, 260, 30);

        m_jTitle.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        add(m_jTitle);
        m_jTitle.setBounds(10, 10, 500, 30);

        jPanel1.setLayout(null);

        jLabel6.setText(AppLocal.getIntString("label.prodbarcode")); // NOI18N
        jPanel1.add(jLabel6);
        jLabel6.setBounds(10, 20, 150, 18);
        jPanel1.add(m_jCode);
        m_jCode.setBounds(160, 20, 170, 28);

        m_jImage.setMaxDimensions(new java.awt.Dimension(256, 256));
        jPanel1.add(m_jImage);
        m_jImage.setBounds(340, 20, 220, 230);

        jLabel3.setText(AppLocal.getIntString("label.prodpricebuy")); // NOI18N
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 50, 150, 18);

        m_jPriceBuy.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(m_jPriceBuy);
        m_jPriceBuy.setBounds(160, 50, 80, 28);

        jLabel4.setText(AppLocal.getIntString("label.prodpricesell")); // NOI18N
        jPanel1.add(jLabel4);
        jLabel4.setBounds(10, 80, 150, 18);

        m_jPriceSell.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(m_jPriceSell);
        m_jPriceSell.setBounds(160, 80, 80, 28);

        jLabel5.setText(AppLocal.getIntString("label.prodcategory")); // NOI18N
        jPanel1.add(jLabel5);
        jLabel5.setBounds(10, 170, 150, 18);
        jPanel1.add(m_jCategory);
        m_jCategory.setBounds(160, 170, 170, 20);

        jLabel7.setText(AppLocal.getIntString("label.taxcategory")); // NOI18N
        jPanel1.add(jLabel7);
        jLabel7.setBounds(10, 140, 150, 18);
        jPanel1.add(m_jTax);
        m_jTax.setBounds(160, 140, 170, 20);

        m_jmargin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(m_jmargin);
        m_jmargin.setBounds(250, 80, 80, 28);

        m_jPriceSellTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(m_jPriceSellTax);
        m_jPriceSellTax.setBounds(160, 110, 80, 28);

        jLabel16.setText(AppLocal.getIntString("label.prodpriceselltax")); // NOI18N
        jPanel1.add(jLabel16);
        jLabel16.setBounds(10, 110, 150, 18);
        jPanel1.add(m_jCodetype);
        m_jCodetype.setBounds(250, 40, 80, 20);

        jLabel13.setText(AppLocal.getIntString("label.attributes")); // NOI18N
        jPanel1.add(jLabel13);
        jLabel13.setBounds(10, 200, 150, 18);
        jPanel1.add(m_jAtt);
        m_jAtt.setBounds(160, 200, 170, 20);

        jTabbedPane1.addTab(AppLocal.getIntString("label.prodgeneral"), jPanel1); // NOI18N

        jPanel2.setLayout(null);

        jLabel9.setText(AppLocal.getIntString("label.prodstockcost")); // NOI18N
        jPanel2.add(jLabel9);
        jLabel9.setBounds(10, 20, 150, 18);

        m_jstockcost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel2.add(m_jstockcost);
        m_jstockcost.setBounds(160, 20, 80, 28);

        jLabel10.setText(AppLocal.getIntString("label.prodstockvol")); // NOI18N
        jPanel2.add(jLabel10);
        jLabel10.setBounds(10, 50, 150, 18);

        m_jstockvolume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel2.add(m_jstockvolume);
        m_jstockvolume.setBounds(160, 50, 80, 28);
        jPanel2.add(m_jScale);
        m_jScale.setBounds(160, 140, 80, 24);
        jPanel2.add(m_jComment);
        m_jComment.setBounds(160, 110, 80, 24);

        jLabel18.setText(AppLocal.getIntString("label.prodorder")); // NOI18N
        jPanel2.add(jLabel18);
        jLabel18.setBounds(250, 80, 60, 18);

        m_jCatalogOrder.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel2.add(m_jCatalogOrder);
        m_jCatalogOrder.setBounds(310, 80, 80, 28);

        m_jInCatalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jInCatalogActionPerformed(evt);
            }
        });
        jPanel2.add(m_jInCatalog);
        m_jInCatalog.setBounds(160, 80, 50, 24);

        jLabel8.setText(AppLocal.getIntString("label.prodincatalog")); // NOI18N
        jPanel2.add(jLabel8);
        jLabel8.setBounds(10, 80, 150, 18);

        jLabel11.setText(AppLocal.getIntString("label.prodaux")); // NOI18N
        jPanel2.add(jLabel11);
        jLabel11.setBounds(10, 110, 150, 18);

        jLabel12.setText(AppLocal.getIntString("label.prodscale")); // NOI18N
        jPanel2.add(jLabel12);
        jLabel12.setBounds(10, 140, 150, 18);

        jTabbedPane1.addTab(AppLocal.getIntString("label.prodstock"), jPanel2); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());

        txtAttributes.setFont(new java.awt.Font("DialogInput", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(txtAttributes);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(AppLocal.getIntString("label.properties"), jPanel3); // NOI18N

        add(jTabbedPane1);
        jTabbedPane1.setBounds(10, 90, 570, 300);

        m_jPrintLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/yast_printer.png"))); // NOI18N
        m_jPrintLabel.setFocusPainted(false);
        m_jPrintLabel.setFocusable(false);
        m_jPrintLabel.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jPrintLabel.setRequestFocusEnabled(false);
        m_jPrintLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPrintLabelActionPerformed(evt);
            }
        });
        add(m_jPrintLabel);
        m_jPrintLabel.setBounds(520, 10, 60, 30);

        m_jVirtualKeyboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/input-keyboard.png"))); // NOI18N
        m_jVirtualKeyboard.setFocusPainted(false);
        m_jVirtualKeyboard.setFocusable(false);
        m_jVirtualKeyboard.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jVirtualKeyboard.setRequestFocusEnabled(false);
        m_jVirtualKeyboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jVirtualKeyboardActionPerformed(evt);
            }
        });
        add(m_jVirtualKeyboard);
        m_jVirtualKeyboard.setBounds(520, 50, 60, 30);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jInCatalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jInCatalogActionPerformed

        if (m_jInCatalog.isSelected()) {
            m_jCatalogOrder.setEnabled(true);
        } else {
            m_jCatalogOrder.setEnabled(false);
            m_jCatalogOrder.setText(null);
        }

    }//GEN-LAST:event_m_jInCatalogActionPerformed

    private void m_jVirtualKeyboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jVirtualKeyboardActionPerformed
        ProductInfoEdit m_oCurrentProductEdit = new ProductInfoEdit();
        m_oCurrentProductEdit.setID(m_id.toString());
        m_oCurrentProductEdit.setReference(m_jRef.getText());
        m_oCurrentProductEdit.setName(m_jName.getText());
        m_oCurrentProductEdit.setCode(m_jCode.getText());

        Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
        if (dPriceBuy != null) m_oCurrentProductEdit.setPriceBuy(dPriceBuy);
        Double dPriceSell = readCurrency(m_jPriceSell.getText());
        if (dPriceSell != null) m_oCurrentProductEdit.setPriceSell(dPriceSell);

        m_oCurrentProductEdit.setCategoryID(m_CategoryModel.getSelectedKey().toString());
        m_oCurrentProductEdit.setTaxID(taxcatmodel.getSelectedKey().toString());
        m_oCurrentProductEdit.setAttributeUseID((attmodel.getSelectedKey() != null) ? attmodel.getSelectedKey().toString() : null);

        try {
            JProductEditDialog editor = JProductEditDialog.getProductEditor(this, m_dSales);
            editor.editProductFields(m_oCurrentProductEdit);
            editor.setVisible(true);

            m_oCurrentProductEdit = editor.getEditProduct();
            m_jRef.setText(m_oCurrentProductEdit.getReference());
            m_jName.setText(m_oCurrentProductEdit.getName());
            m_jCode.setText(m_oCurrentProductEdit.getCode());
            m_jPriceBuy.setText(Formats.CURRENCY.formatValue(m_oCurrentProductEdit.getPriceBuy()));
            m_jPriceSell.setText(Formats.CURRENCY.formatValue(m_oCurrentProductEdit.getPriceSell()));
            m_CategoryModel.setSelectedKey(m_oCurrentProductEdit.getCategoryID());
            taxcatmodel.setSelectedKey(m_oCurrentProductEdit.getTaxID());
            attmodel.setSelectedKey(m_oCurrentProductEdit.getAttributeUseID());
        } catch (BasicException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculateMargin();

    }//GEN-LAST:event_m_jVirtualKeyboardActionPerformed

    private void m_jPrintLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPrintLabelActionPerformed
        m_oCurrentProductEdit.setID(m_id.toString());
        m_oCurrentProductEdit.setReference(m_jRef.getText());
        m_oCurrentProductEdit.setName(m_jName.getText());
        m_oCurrentProductEdit.setCode(m_jCode.getText());

        Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
        if (dPriceBuy != null) {
            m_oCurrentProductEdit.setPriceBuy(dPriceBuy);
        }
        Double dPriceSell = readCurrency(m_jPriceSell.getText());
        if (dPriceSell != null) {
            m_oCurrentProductEdit.setPriceSell(dPriceSell);
        }

        m_oCurrentProductEdit.setCategoryID(m_CategoryModel.getSelectedKey().toString());
        m_oCurrentProductEdit.setTaxID(taxcatmodel.getSelectedKey().toString());
        m_oCurrentProductEdit.setAttributeUseID((attmodel.getSelectedKey() != null) ? attmodel.getSelectedKey().toString() : null);

        try {
            printProductLabel(m_oCurrentProductEdit);
        } catch (TicketPrinterException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScriptException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TicketFiscalPrinterException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_m_jPrintLabelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private org.fife.ui.rtextarea.RTextScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox m_jAtt;
    private javax.swing.JTextField m_jCatalogOrder;
    private javax.swing.JComboBox m_jCategory;
    private javax.swing.JTextField m_jCode;
    private javax.swing.JComboBox m_jCodetype;
    private javax.swing.JCheckBox m_jComment;
    private com.openbravo.data.gui.JImageEditor m_jImage;
    private javax.swing.JCheckBox m_jInCatalog;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextField m_jPriceBuy;
    private javax.swing.JTextField m_jPriceSell;
    private javax.swing.JTextField m_jPriceSellTax;
    private javax.swing.JButton m_jPrintLabel;
    private javax.swing.JTextField m_jRef;
    private javax.swing.JCheckBox m_jScale;
    private javax.swing.JComboBox m_jTax;
    private javax.swing.JLabel m_jTitle;
    private javax.swing.JButton m_jVirtualKeyboard;
    private javax.swing.JTextField m_jmargin;
    private javax.swing.JTextField m_jstockcost;
    private javax.swing.JTextField m_jstockvolume;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea txtAttributes;
    // End of variables declaration//GEN-END:variables

}
