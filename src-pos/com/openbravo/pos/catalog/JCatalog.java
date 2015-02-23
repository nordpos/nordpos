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
package com.openbravo.pos.catalog;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.sales.PropertiesConfig;
import com.openbravo.pos.sales.TaxesLogic;
import com.openbravo.pos.ticket.CategoryInfo;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.util.ThumbNailBuilder;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class JCatalog extends JPanel implements ListSelectionListener, CatalogSelector {

    protected EventListenerList listeners = new EventListenerList();
    private DataLogicSales m_dlSales;
    private TaxesLogic taxeslogic;
    private boolean pricevisible;
    private boolean taxesincluded;
    private boolean refvisible;
    private boolean namevisible;
    private boolean stockvisible;
    // Set of Products panels
    private Map<String, ProductInfoExt> m_productsset = new HashMap<String, ProductInfoExt>();
    // Set of Categoriespanels
    private Set<String> m_categoriesset = new HashSet<String>();
    private ThumbNailBuilder tnbbutton;
    private CategoryInfo showingcategory = null;
    private LinkedList<CategoryInfo> subcategories;
    private String s_DefProdCat;
    private final int iTextFontSize;
    private String s_DefLocation;

    public JCatalog(DataLogicSales dlSales, PropertiesConfig panelconfig) {

        m_dlSales = dlSales;

        this.pricevisible = "true".equals(panelconfig.getProperty("pricevisible", "true"));
        this.taxesincluded = "true".equals(panelconfig.getProperty("taxesincluded", "false"));
        this.refvisible = "true".equals(panelconfig.getProperty("refvisible", "false"));
        this.namevisible = "true".equals(panelconfig.getProperty("namevisible", "true"));
        this.stockvisible = "true".equals(panelconfig.getProperty("stockvisible", "false"));

        initComponents();

        m_jPanelImageViewer.setVisible("true".equals(panelconfig.getProperty("imageviewer", "true")));

        iTextFontSize = Integer.parseInt(panelconfig.getProperty("font-size", "12"));

        tnbbutton = new ThumbNailBuilder(Integer.parseInt(panelconfig.getProperty("img-width", "64")),
                Integer.parseInt(panelconfig.getProperty("img-height", "54")),
                iTextFontSize,
                "com/openbravo/images/package.png");
    }

    public Component getComponent() {
        return this;
    }

    public void showCatalogPanel(String id) {

        if (id == null) {
            showRootCategoriesPanel();
        } else {
            showProductPanel(id);
        }
    }

    @Override
    public void loadCatalog(AppView app) throws BasicException {

        // delete all categories panel
        m_jProducts.removeAll();

        m_productsset.clear();
        m_categoriesset.clear();

        showingcategory = null;
        subcategories = new LinkedList();

        // Load the taxes logic
        taxeslogic = new TaxesLogic(m_dlSales.getTaxList().list());

        s_DefProdCat = app.getDefaultProductCategory();
        s_DefLocation = app.getInventoryLocation();

        // Display catalog panel
        showRootCategoriesPanel();
    }

    public void setComponentEnabled(boolean value) {

        m_btnBackRoot.setEnabled(value);
        m_jProducts.setEnabled(value);
        synchronized (m_jProducts.getTreeLock()) {
            int compCount = m_jProducts.getComponentCount();
            for (int i = 0; i < compCount; i++) {
                m_jProducts.getComponent(i).setEnabled(value);
            }
        }

        this.setEnabled(value);
    }

    public void addActionListener(ActionListener l) {
        listeners.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listeners.remove(ActionListener.class, l);
    }

    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
        }
    }

    protected void fireSelectedProduct(ProductInfoExt prod) {
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (int i = 0; i < l.length; i++) {
            if (e == null) {
                e = new ActionEvent(prod, ActionEvent.ACTION_PERFORMED, prod.getID());
            }
            ((ActionListener) l[i]).actionPerformed(e);
        }

        if (prod.getImage() != null) {
            m_jImage.setImage(prod.getImage());
        } else {
            m_jImage.setImage(null);
        }
    }

    private void selectCategoryPanel(String catid) {

        try {
            // Load categories panel if not exists
            if (!m_categoriesset.contains(catid)) {

                JCatalogTab jcurrTab = new JCatalogTab();
                jcurrTab.applyComponentOrientation(getComponentOrientation());
                m_jProducts.add(jcurrTab, catid);
                m_categoriesset.add(catid);

                // Add subcategories
                java.util.List<CategoryInfo> categories = new LinkedList<CategoryInfo>();
                if (catid.isEmpty()) {
                    categories = m_dlSales.getRootCategories();
                } else {
                    categories = m_dlSales.getSubcategories(catid);
                }

                for (CategoryInfo cat : categories) {
                    jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(cat.getImage(), "<p align=\"center\">" + cat.getName() + "</p>")), new SelectedCategory(cat));
                }

                // Add products
                java.util.List<ProductInfoExt> products = m_dlSales.getProductCatalog(catid);
                for (ProductInfoExt prod : products) {
                    jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new SelectedAction(prod));
                }
            }

            // Show categories panel
            CardLayout cl = (CardLayout) (m_jProducts.getLayout());
            cl.show(m_jProducts, catid);
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notactive"), e));
        }
    }

    private String getProductLabel(ProductInfoExt product) {
        String sProductText = "<p align=\"center\">";
        Double dProductStock = 0.0;

        if (namevisible) {
            sProductText = sProductText.concat(product.getName());
        }

        if (refvisible) {
            sProductText = sProductText.concat("<br>#" + product.getReference());
        }

        if (pricevisible) {
            if (taxesincluded) {
                TaxInfo tax = taxeslogic.getTaxInfo(product.getTaxCategoryID(), new Date());
                sProductText = sProductText.concat("<br>" + product.printPriceSellTax(tax));
            } else {
                sProductText = sProductText.concat("<br>" + product.printPriceSell());
            }
        }

        if (stockvisible) {
            try {
                dProductStock = m_dlSales.findProductStock(s_DefLocation, product.getID(), null);
            } catch (BasicException ex) {
                Logger.getLogger(JCatalog.class.getName()).log(Level.SEVERE, null, ex);
            }
            sProductText = sProductText.concat("<br>[" + Formats.DOUBLE.formatValue(dProductStock) + "]");
        }

        return sProductText.concat("</p>");
    }

    private void showRootCategoriesPanel() {
        subcategories = new LinkedList();
        if (s_DefProdCat != null && !s_DefProdCat.isEmpty()) {
            try {
                showingcategory = m_dlSales.getCategoryInfoById(s_DefProdCat);
            } catch (BasicException ex) {
                Logger.getLogger(JCatalog.class.getName()).log(Level.SEVERE, null, ex);
            }
            subcategories.add(null);
            if (showingcategory != null) {
                selectCategoryPanel(showingcategory.getID());
            }
        } else {
            showingcategory = null;
            showSubcategoryPanel(showingcategory);
        }
    }

    private void showSubcategoryPanel(CategoryInfo category) {
        if (category != null) {
            selectCategoryPanel(category.getID());
        } else {
            selectCategoryPanel("");
        }
        showingcategory = category;
    }

    private void showProductPanel(String id) {

        ProductInfoExt product = m_productsset.get(id);

        if (product == null) {
            if (m_productsset.containsKey(id)) {
                // It is an empty panel
                if (showingcategory == null) {
                    showRootCategoriesPanel();
                } else {
                    showSubcategoryPanel(showingcategory);
                }
            } else {
                try {
                    // Create  products panel
                    java.util.List<ProductInfoExt> products = m_dlSales.getProductComments(id);

                    if (products.isEmpty()) {
                        // no hay productos por tanto lo anado a la de vacios y muestro el panel principal.
                        m_productsset.put(id, null);
                        if (showingcategory == null) {
                            showRootCategoriesPanel();
                        } else {
                            showSubcategoryPanel(showingcategory);
                        }
                    } else {

                        // Load product panel
                        product = m_dlSales.getProductInfo(id);
                        m_productsset.put(id, product);

                        JCatalogTab jcurrTab = new JCatalogTab();
                        jcurrTab.applyComponentOrientation(getComponentOrientation());
                        m_jProducts.add(jcurrTab, "PRODUCT." + id);

                        // Add products
                        for (ProductInfoExt prod : products) {
                            jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new SelectedAction(prod));
                        }

                        CardLayout cl = (CardLayout) (m_jProducts.getLayout());
                        cl.show(m_jProducts, "PRODUCT." + id);
                    }
                } catch (BasicException eb) {
                    m_productsset.put(id, null);
                    if (showingcategory == null) {
                        showRootCategoriesPanel();
                    } else {
                        showSubcategoryPanel(showingcategory);
                    }
                }
            }
        } else {
            // already exists
            CardLayout cl = (CardLayout) (m_jProducts.getLayout());
            cl.show(m_jProducts, "PRODUCT." + id);
        }
    }

    private class SelectedAction implements ActionListener {

        private ProductInfoExt prod;

        public SelectedAction(ProductInfoExt prod) {
            this.prod = prod;
        }

        public void actionPerformed(ActionEvent e) {
            fireSelectedProduct(prod);
        }
    }

    private class SelectedCategory implements ActionListener {

        private CategoryInfo category;

        public SelectedCategory(CategoryInfo category) {
            this.category = category;
        }

        public void actionPerformed(ActionEvent e) {
            subcategories.add(showingcategory);
            showSubcategoryPanel(category);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jProducts = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jCategories = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        m_btnBackRoot = new javax.swing.JButton();
        m_btnBackSub = new javax.swing.JButton();
        m_jPanelImageViewer = new javax.swing.JPanel();
        m_jImage = new com.openbravo.data.gui.JImageViewer();

        setLayout(new java.awt.BorderLayout());

        m_jProducts.setLayout(new java.awt.CardLayout());
        add(m_jProducts, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        m_jCategories.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        m_jCategories.setMinimumSize(new java.awt.Dimension(72, 275));
        m_jCategories.setPreferredSize(new java.awt.Dimension(82, 275));
        m_jCategories.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.GridLayout(0, 1, 0, 2));

        m_btnBackRoot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/3uparrow2.png"))); // NOI18N
        m_btnBackRoot.setFocusPainted(false);
        m_btnBackRoot.setFocusable(false);
        m_btnBackRoot.setRequestFocusEnabled(false);
        m_btnBackRoot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnBackRootActionPerformed(evt);
            }
        });
        jPanel5.add(m_btnBackRoot);

        m_btnBackSub.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/3uparrow.png"))); // NOI18N
        m_btnBackSub.setFocusPainted(false);
        m_btnBackSub.setFocusable(false);
        m_btnBackSub.setRequestFocusEnabled(false);
        m_btnBackSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnBackSubActionPerformed(evt);
            }
        });
        jPanel5.add(m_btnBackSub);

        m_jCategories.add(jPanel5, java.awt.BorderLayout.NORTH);

        jPanel1.add(m_jCategories, java.awt.BorderLayout.LINE_START);

        m_jPanelImageViewer.setPreferredSize(new java.awt.Dimension(240, 275));
        m_jPanelImageViewer.setLayout(new java.awt.BorderLayout());

        m_jImage.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        m_jPanelImageViewer.add(m_jImage, java.awt.BorderLayout.CENTER);

        jPanel1.add(m_jPanelImageViewer, java.awt.BorderLayout.LINE_END);

        add(jPanel1, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void m_btnBackRootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnBackRootActionPerformed

        showRootCategoriesPanel();

    }//GEN-LAST:event_m_btnBackRootActionPerformed

    private void m_btnBackSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnBackSubActionPerformed
        if (!subcategories.isEmpty()) {
            showSubcategoryPanel(subcategories.getLast());
            subcategories.removeLast();
        }
    }//GEN-LAST:event_m_btnBackSubActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton m_btnBackRoot;
    private javax.swing.JButton m_btnBackSub;
    private javax.swing.JPanel m_jCategories;
    private com.openbravo.data.gui.JImageViewer m_jImage;
    private javax.swing.JPanel m_jPanelImageViewer;
    private javax.swing.JPanel m_jProducts;
    // End of variables declaration//GEN-END:variables
}
