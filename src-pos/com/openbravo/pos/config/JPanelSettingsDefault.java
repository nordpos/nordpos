/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.config;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JPanelView;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class JPanelSettingsDefault extends JPanel implements JPanelView, BeanFactoryApp {

    private DirtyManager dirty = new DirtyManager();
    private DataLogicSystem dlSystem;
    private DataLogicSales dlSales;
    private AppProperties appProp;
    private Properties hostProp;
    private SentenceList m_Warehouses;
    private ComboBoxValModel m_WarehouseModel;
    private SentenceList m_TaxCategories;
    private ComboBoxValModel m_TaxCategoryModel;
    private SentenceList m_Categories;
    private ComboBoxValModel m_CategoryModel;

    public JPanelSettingsDefault() {
        initComponents();

        jTxtCustomerCard.getDocument().addDocumentListener(dirty);
        jTxtUserCard.getDocument().addDocumentListener(dirty);
        jTxtPriceBarcode.getDocument().addDocumentListener(dirty);
        jTxtProductBarcode.getDocument().addDocumentListener(dirty);
        jTxtUnitBarcode.getDocument().addDocumentListener(dirty);
        jTxtUserBarcode.getDocument().addDocumentListener(dirty);

        m_WarehouseModel = new ComboBoxValModel();
        m_TaxCategoryModel = new ComboBoxValModel();
        m_CategoryModel = new ComboBoxValModel();
        jcboWarehoseType.addActionListener(dirty);
        jcboTaxCategory.addActionListener(dirty);
        jcboProductCategory.addActionListener(dirty);

        jcbGenProdRef.addActionListener(dirty);
        jcbGenProdBarcode.addActionListener(dirty);

    }

    @Override
    public void init(AppView app) throws BeanFactoryException {
        dlSystem = (DataLogicSystem) app.getBean(DataLogicSystem.class.getName());
        dlSales = (DataLogicSales) app.getBean(DataLogicSales.class.getName());
        appProp = app.getProperties();
        hostProp = dlSystem.getResourceAsProperties(appProp.getHost() + "/properties");

    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.DefaultSet");
    }

    @Override
    public void activate() throws BasicException {

        jTxtCustomerCard.setText(hostProp.getProperty("customercard"));
        jTxtUserCard.setText(hostProp.getProperty("usercard"));
        jTxtUserBarcode.setText(hostProp.getProperty("userbarcode"));
        jTxtPriceBarcode.setText(hostProp.getProperty("pricebarcode"));
        jTxtUnitBarcode.setText(hostProp.getProperty("unitbarcode"));
        jTxtProductBarcode.setText(hostProp.getProperty("productpricebarcode"));

        m_Warehouses = dlSales.getLocationsList();
        m_WarehouseModel = new ComboBoxValModel(m_Warehouses.list());
        jcboWarehoseType.setModel(m_WarehouseModel);
        m_WarehouseModel.setSelectedKey(hostProp.getProperty("location"));

        m_TaxCategories = dlSales.getTaxCategoriesList();
        m_TaxCategoryModel = new ComboBoxValModel(m_TaxCategories.list());
        jcboTaxCategory.setModel(m_TaxCategoryModel);
        m_TaxCategoryModel.setSelectedKey(hostProp.getProperty("taxcategoryid"));

        m_Categories = dlSales.getCategoriesList();
        m_CategoryModel = new ComboBoxValModel(m_Categories.list());
        m_CategoryModel.add(null);
        jcboProductCategory.setModel(m_CategoryModel);
        m_CategoryModel.setSelectedKey(hostProp.getProperty("productcategoryid"));

        jcbGenProdRef.setSelected(hostProp.getProperty("genreference").equalsIgnoreCase("true"));
        jcbGenProdBarcode.setSelected(hostProp.getProperty("genbarcode").equalsIgnoreCase("true"));

        dirty.setDirty(false);
    }

    @Override
    public boolean deactivate() {
        if (dirty.isDirty()) {
            int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.syncwannasave"), AppLocal.getIntString("title.defaultparam"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                saveProperties();
                return true;
            } else {
                return res == JOptionPane.NO_OPTION;
            }
        } else {
            return true;
        }
    }

    private void saveProperties() {
        hostProp.setProperty("customercard", jTxtCustomerCard.getText());
        hostProp.setProperty("usercard", jTxtUserCard.getText());
        hostProp.setProperty("userbarcode", jTxtUserBarcode.getText());
        hostProp.setProperty("pricebarcode", jTxtPriceBarcode.getText());
        hostProp.setProperty("unitbarcode", jTxtUnitBarcode.getText());
        hostProp.setProperty("productpricebarcode", jTxtProductBarcode.getText());

        hostProp.setProperty("location", m_WarehouseModel.getSelectedKey().toString());
        hostProp.setProperty("taxcategoryid", m_TaxCategoryModel.getSelectedKey().toString());
        hostProp.setProperty("productcategoryid", m_CategoryModel.getSelectedKey() == null ? "" : m_CategoryModel.getSelectedKey().toString());

        if (jcbGenProdRef.isSelected()) {
            hostProp.setProperty("genreference", "true");
        } else {
            hostProp.setProperty("genreference", "false");
        }

        if (jcbGenProdBarcode.isSelected()) {
            hostProp.setProperty("genbarcode", "true");
        } else {
            hostProp.setProperty("genbarcode", "false");
        }

        dlSystem.setResourceAsProperties(appProp.getHost() + "/properties", hostProp);

        JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.defaultparam.save"), AppLocal.getIntString("title.defaultparam"), JOptionPane.INFORMATION_MESSAGE);

        dirty.setDirty(false);
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Object getBean() {
        return this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanelPrefix = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTxtCustomerCard = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTxtUserBarcode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTxtPriceBarcode = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTxtUnitBarcode = new javax.swing.JTextField();
        jTxtProductBarcode = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTxtUserCard = new javax.swing.JTextField();
        jPanelSelect = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jcboWarehoseType = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jcboProductCategory = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jcboTaxCategory = new javax.swing.JComboBox();
        jPanelSet = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jcbGenProdRef = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jcbGenProdBarcode = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        Save = new javax.swing.JButton();

        jPanelPrefix.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("Label.Prefix"))); // NOI18N

        jLabel1.setText(AppLocal.getIntString("label.prefix.card.customer")); // NOI18N

        jLabel2.setText(AppLocal.getIntString("label.prefix.barcode.user")); // NOI18N

        jLabel3.setText(AppLocal.getIntString("label.prefix.barcode.price")); // NOI18N

        jLabel4.setText(AppLocal.getIntString("label.prefix.barcode.unit")); // NOI18N

        jLabel5.setText(AppLocal.getIntString("label.prefix.barcode.product")); // NOI18N

        jLabel6.setText(AppLocal.getIntString("label.prefix.card.user")); // NOI18N

        javax.swing.GroupLayout jPanelPrefixLayout = new javax.swing.GroupLayout(jPanelPrefix);
        jPanelPrefix.setLayout(jPanelPrefixLayout);
        jPanelPrefixLayout.setHorizontalGroup(
            jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrefixLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTxtCustomerCard, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtUserCard, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtUserBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtPriceBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtUnitBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtProductBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(501, Short.MAX_VALUE))
        );
        jPanelPrefixLayout.setVerticalGroup(
            jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrefixLayout.createSequentialGroup()
                .addGroup(jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jTxtCustomerCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(jTxtUserCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jTxtUserBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(jTxtPriceBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(jTxtUnitBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrefixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(jTxtProductBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanelSelect.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("Label.SelectGroup"))); // NOI18N

        jLabel7.setText(AppLocal.getIntString("label.warehouse")); // NOI18N

        jLabel8.setText(AppLocal.getIntString("label.prodcategory")); // NOI18N

        jLabel9.setText(AppLocal.getIntString("label.taxcategory")); // NOI18N

        javax.swing.GroupLayout jPanelSelectLayout = new javax.swing.GroupLayout(jPanelSelect);
        jPanelSelect.setLayout(jPanelSelectLayout);
        jPanelSelectLayout.setHorizontalGroup(
            jPanelSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSelectLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcboWarehoseType, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboTaxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboProductCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(371, Short.MAX_VALUE))
        );
        jPanelSelectLayout.setVerticalGroup(
            jPanelSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSelectLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(jcboWarehoseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9)
                    .addComponent(jcboTaxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSelectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(jcboProductCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanelSet.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("Label.SetBoolean"))); // NOI18N

        jLabel10.setText(AppLocal.getIntString("Label.GenProdRef")); // NOI18N

        jLabel11.setText(AppLocal.getIntString("Label.GenProdBarcode")); // NOI18N

        javax.swing.GroupLayout jPanelSetLayout = new javax.swing.GroupLayout(jPanelSet);
        jPanelSet.setLayout(jPanelSetLayout);
        jPanelSetLayout.setHorizontalGroup(
            jPanelSetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSetLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jcbGenProdRef))
                    .addGroup(jPanelSetLayout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jcbGenProdBarcode)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelSetLayout.setVerticalGroup(
            jPanelSetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10)
                    .addComponent(jcbGenProdRef))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelSetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(jcbGenProdBarcode))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelPrefix, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelSelect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelSet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        Save.setText(AppLocal.getIntString("Button.Save")); // NOI18N
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(709, Short.MAX_VALUE)
                .addComponent(Save))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Save, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(552, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(47, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
        saveProperties();
    }//GEN-LAST:event_SaveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JPanel jPanelPrefix;
    private javax.swing.JPanel jPanelSelect;
    private javax.swing.JPanel jPanelSet;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTxtCustomerCard;
    private javax.swing.JTextField jTxtPriceBarcode;
    private javax.swing.JTextField jTxtProductBarcode;
    private javax.swing.JTextField jTxtUnitBarcode;
    private javax.swing.JTextField jTxtUserBarcode;
    private javax.swing.JTextField jTxtUserCard;
    private javax.swing.JCheckBox jcbGenProdBarcode;
    private javax.swing.JCheckBox jcbGenProdRef;
    private javax.swing.JComboBox jcboProductCategory;
    private javax.swing.JComboBox jcboTaxCategory;
    private javax.swing.JComboBox jcboWarehoseType;
    // End of variables declaration//GEN-END:variables
}
