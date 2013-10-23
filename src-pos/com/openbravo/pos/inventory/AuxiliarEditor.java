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
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.ticket.ProductInfoExt;
import java.awt.Component;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.panels.JProductFinder;
import java.awt.Toolkit;
import java.util.UUID;

/**
 *
 * @author jaroslawwozniak
 */
public class AuxiliarEditor extends javax.swing.JPanel implements EditorRecord {

    private DataLogicSales m_dlSales;
    
    private Object id;
    private Object product;
    private Object product2;
    private Object name;
    
    private Object insertproduct;

    /** Creates new form AuxiliarEditor */
    public AuxiliarEditor(AppView app, DirtyManager dirty) {

        m_dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");

        initComponents();
     
        m_jProduct.getDocument().addDocumentListener(dirty);
    }
    
    public void setInsertProduct(ProductInfoExt prod) {
        
        if (prod == null) {
            insertproduct = null;
        } else {
            insertproduct = prod.getID();
        }
    }

    public void refresh() {
    }

    public void writeValueEOF() {
        
        id = null;
        product = null;
        product2 = null;
        name = null;
        m_jReference.setText(null);
        m_jBarcode.setText(null);
        m_jProduct.setText(null);

        m_jReference.setEnabled(false);
        m_jBarcode.setEnabled(false);
        m_jProduct.setEnabled(false);
        m_jEnter1.setEnabled(false);
        m_jEnter2.setEnabled(false);
        m_jSearch.setEnabled(false);
    }

    public void writeValueInsert() {
        
        id = UUID.randomUUID().toString();
        product = insertproduct;
        product2 = null;
        name = null;
        m_jReference.setText(null);
        m_jBarcode.setText(null);
        m_jProduct.setText(null);

        m_jReference.setEnabled(true);
        m_jBarcode.setEnabled(true);
        m_jProduct.setEnabled(true);
        m_jEnter1.setEnabled(true);
        m_jEnter2.setEnabled(true);
        m_jSearch.setEnabled(true);
    }

    public void writeValueEdit(Object value) {
        Object[] obj = (Object[]) value;
        
        id = obj[0];
        product = obj[1];
        product2 = obj[2];
        name = obj[5];
        m_jReference.setText(Formats.STRING.formatValue(obj[3]));
        m_jBarcode.setText(Formats.STRING.formatValue(obj[4]));
        m_jProduct.setText(Formats.STRING.formatValue(obj[3]) + " - " + Formats.STRING.formatValue(obj[5]));        

        m_jReference.setEnabled(true);
        m_jBarcode.setEnabled(true);
        m_jProduct.setEnabled(true);
        m_jEnter1.setEnabled(true);
        m_jEnter2.setEnabled(true);
        m_jSearch.setEnabled(true);
    }

    public void writeValueDelete(Object value) {
        Object[] obj = (Object[]) value;
        
        id = obj[0];
        product = obj[1];
        product2 = obj[2];
        name = obj[5];
        m_jReference.setText(Formats.STRING.formatValue(obj[3]));
        m_jBarcode.setText(Formats.STRING.formatValue(obj[4]));
        m_jProduct.setText(Formats.STRING.formatValue(obj[3]) + " - " + Formats.STRING.formatValue(obj[5]));        

        
        m_jReference.setEnabled(false);
        m_jBarcode.setEnabled(false);
        m_jProduct.setEnabled(false);
        m_jEnter1.setEnabled(false);
        m_jEnter2.setEnabled(false);
        m_jSearch.setEnabled(false);       
    }

    public Object createValue() throws BasicException {
        return new Object[] {
            id, 
            product, 
            product2,
            m_jReference.getText(),
            m_jBarcode.getText(),
            name,
        };
    }

    public Component getComponent() {
        return this;
    }

    private void assignProduct(ProductInfoExt prod) {

        if (m_jSearch.isEnabled()) {
            if (prod == null) {
                product2 = null;
                m_jReference.setText(null);
                m_jBarcode.setText(null);
                m_jProduct.setText(null);
                name = null;
            } else {
                product2 = prod.getID();
                m_jReference.setText(prod.getReference());
                m_jBarcode.setText(prod.getCode());
                m_jProduct.setText(prod.getReference() + " - " + prod.getName());
                name = prod.getName();
            }
        }

    }

    private void assignProductByCode() {
        try {
            ProductInfoExt prod = m_dlSales.getProductInfoByCode(m_jBarcode.getText());
            assignProduct(prod);
            if (prod == null) {
                Toolkit.getDefaultToolkit().beep();       
            }
        } catch (BasicException eData) {
            assignProduct(null);
            MessageInf msg = new MessageInf(eData);
            msg.show(this);
        }
    }


    private void assignProductByReference() {
        try {
            ProductInfoExt prod = m_dlSales.getProductInfoByReference(m_jReference.getText());
            assignProduct(prod);
            if (prod == null) {
                Toolkit.getDefaultToolkit().beep();       
            }
        } catch (BasicException eData) {
            assignProduct(null);
            MessageInf msg = new MessageInf(eData);
            msg.show(this);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        m_jReference = new javax.swing.JTextField();
        m_jEnter1 = new javax.swing.JButton();
        m_jEnter2 = new javax.swing.JButton();
        m_jSearch = new javax.swing.JButton();
        m_jProduct = new javax.swing.JTextField();
        m_jBarcode = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        jLabel3.setText(AppLocal.getIntString("label.prodref")); // NOI18N

        m_jReference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jReferenceActionPerformed(evt);
            }
        });

        m_jEnter1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/apply.png"))); // NOI18N
        m_jEnter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEnter1ActionPerformed(evt);
            }
        });

        m_jEnter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/apply.png"))); // NOI18N
        m_jEnter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEnter2ActionPerformed(evt);
            }
        });

        m_jSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/search.png"))); // NOI18N
        m_jSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jSearchActionPerformed(evt);
            }
        });

        m_jProduct.setEditable(false);
        m_jProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jProductActionPerformed(evt);
            }
        });

        m_jBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBarcodeActionPerformed(evt);
            }
        });

        jLabel4.setText(AppLocal.getIntString("label.prodbarcode")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jReference, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(m_jBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jEnter2)
                            .addComponent(m_jEnter1)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_jProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSearch)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jEnter1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(m_jReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_jBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(m_jEnter2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jSearch)
                    .addComponent(m_jProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_jSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jSearchActionPerformed
        
        assignProduct(JProductFinder.showMessage(this, m_dlSales, JProductFinder.PRODUCT_AUXILIAR));
        
}//GEN-LAST:event_m_jSearchActionPerformed

    private void m_jReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jReferenceActionPerformed
        this.assignProductByReference();
    }//GEN-LAST:event_m_jReferenceActionPerformed

    private void m_jEnter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEnter2ActionPerformed
        this.assignProductByCode();
    }//GEN-LAST:event_m_jEnter2ActionPerformed

    private void m_jEnter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEnter1ActionPerformed
        this.assignProductByReference();
    }//GEN-LAST:event_m_jEnter1ActionPerformed

    private void m_jBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBarcodeActionPerformed
        this.assignProductByCode();
    }//GEN-LAST:event_m_jBarcodeActionPerformed

    private void m_jProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jProductActionPerformed

    }//GEN-LAST:event_m_jProductActionPerformed

  


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField m_jBarcode;
    private javax.swing.JButton m_jEnter1;
    private javax.swing.JButton m_jEnter2;
    private javax.swing.JTextField m_jProduct;
    private javax.swing.JTextField m_jReference;
    private javax.swing.JButton m_jSearch;
    // End of variables declaration//GEN-END:variables

}
