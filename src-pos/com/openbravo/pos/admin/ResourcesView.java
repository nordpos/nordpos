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

package com.openbravo.pos.admin;

import javax.swing.*;
import com.openbravo.pos.forms.AppLocal;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.UUID;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.util.Base64Encoder;

/**
 *
 * @author adrianromero
 */
public class ResourcesView extends JPanel implements EditorRecord {
    
    private Object m_oId;
    private ComboBoxValModel m_ResourceModel;
            
    /** Creates new form ResourcesEditor */
    public ResourcesView(DirtyManager dirty) {
        initComponents();
        
        m_ResourceModel = new ComboBoxValModel();
        m_ResourceModel.add(ResourceType.TEXT);
        m_ResourceModel.add(ResourceType.IMAGE);
        m_ResourceModel.add(ResourceType.BINARY);
        m_jType.setModel(m_ResourceModel);
        
        m_jName.getDocument().addDocumentListener(dirty);
        m_jType.addActionListener(dirty);
        m_jText.getDocument().addDocumentListener(dirty);
        m_jImage.addPropertyChangeListener("image", dirty);
        
        writeValueEOF();        
    }

    public void writeValueEOF() {
        m_oId = null;
        m_jName.setText(null);
        m_ResourceModel.setSelectedItem(null);
        m_jText.setText(null);
        m_jImage.setImage(null);     
        m_jName.setEnabled(false);
        m_jType.setEnabled(false);
        m_jText.setEnabled(false);
        m_jImage.setEnabled(false);
    }    
    
    public void writeValueInsert() {
        m_oId = null;
        m_jName.setText(null);
        m_ResourceModel.setSelectedItem(ResourceType.TEXT);
        m_jText.setText(null);
        m_jImage.setImage(null);     
        m_jName.setEnabled(true);
        m_jType.setEnabled(true);
        m_jText.setEnabled(true);
        m_jImage.setEnabled(true);
    }
    
    public void writeValueDelete(Object value) {
        Object[] resource = (Object[]) value;
        m_oId = resource[0];
        m_jName.setText((String) resource[1]);
        m_ResourceModel.setSelectedKey(resource[2]);
        
        ResourceType restype = (ResourceType) m_ResourceModel.getSelectedItem();
        if (restype == ResourceType.TEXT) {
            m_jText.setText(Formats.BYTEA.formatValue(resource[3]));
            m_jText.setCaretPosition(0);
            m_jImage.setImage(null);
        } else if (restype == ResourceType.IMAGE) {
            m_jText.setText(null);
            m_jImage.setImage(ImageUtils.readImage((byte[]) resource[3]));
        } else if (restype == ResourceType.BINARY) {
            m_jText.setText(resource[3] == null
                    ? null
                    : Base64Encoder.encodeChunked((byte[]) resource[3]));
            m_jText.setCaretPosition(0);
            m_jImage.setImage(null);
        } else {
            m_jText.setText(null);
            m_jImage.setImage(null);
        }
        m_jName.setEnabled(false);
        m_jType.setEnabled(false);
        m_jText.setEnabled(false);
        m_jImage.setEnabled(false);       
    }  
    
    public void writeValueEdit(Object value) {
        Object[] resource = (Object[]) value;
        m_oId = resource[0];
        m_jName.setText((String) resource[1]);
        m_ResourceModel.setSelectedKey(resource[2]);
        
        ResourceType restype = (ResourceType) m_ResourceModel.getSelectedItem();
        if (restype == ResourceType.TEXT) {
            m_jText.setText(Formats.BYTEA.formatValue(resource[3]));
            m_jText.setCaretPosition(0);
            m_jImage.setImage(null);
        } else if (restype == ResourceType.IMAGE) {
            m_jText.setText(null);
            m_jImage.setImage(ImageUtils.readImage((byte[]) resource[3]));
        } else if (restype == ResourceType.BINARY) {
            m_jText.setText(resource[2] == null
                    ? null
                    : Base64Encoder.encodeChunked((byte[]) resource[3]));
            m_jText.setCaretPosition(0);
            m_jImage.setImage(null);
        } else {
            m_jText.setText(null);
            m_jImage.setImage(null);
        }
        m_jName.setEnabled(true);
        m_jType.setEnabled(true);
        m_jText.setEnabled(true);
        m_jImage.setEnabled(true);
    }
    
    public Object createValue() throws BasicException {
        Object[] resource = new Object[4];

        resource[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;
        resource[1] = m_jName.getText();
        
        ResourceType restype = (ResourceType) m_ResourceModel.getSelectedItem();
        resource[2] = restype.getKey();
        if (restype == ResourceType.TEXT) {
            resource[3] = Formats.BYTEA.parseValue(m_jText.getText());
        } else if (restype == ResourceType.IMAGE) {
            resource[3] = ImageUtils.writeImage(m_jImage.getImage());
        } else if (restype == ResourceType.BINARY) {
            resource[3] = Base64Encoder.decode(m_jText.getText());
        } else {
            resource[3] = null;
        }

        return resource;
    }
    
    public Component getComponent() {
        return this;
    }
    
    public void refresh() {
    }
    
    private void showView(String view) {
        CardLayout cl = (CardLayout)(m_jContainer.getLayout());
        cl.show(m_jContainer, view);  
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jGroupType = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        m_jContainer = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jText = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        m_jImage = new com.openbravo.data.gui.JImageEditor();
        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        m_jType = new javax.swing.JComboBox();

        jPanel3.setLayout(new java.awt.BorderLayout());

        m_jContainer.setLayout(new java.awt.CardLayout());

        m_jText.setFont(new java.awt.Font("DialogInput", 0, 12));
        jScrollPane1.setViewportView(m_jText);

        m_jContainer.add(jScrollPane1, "text");
        m_jContainer.add(jPanel1, "null");
        m_jContainer.add(m_jImage, "image");

        jPanel3.add(m_jContainer, java.awt.BorderLayout.CENTER);

        jLabel2.setText(AppLocal.getIntString("label.resname")); // NOI18N

        m_jType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jType, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jType, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_jTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jTypeActionPerformed

        ResourceType restype = (ResourceType) m_ResourceModel.getSelectedItem();
        if (restype == ResourceType.TEXT) {
            showView("text");
        } else if (restype == ResourceType.IMAGE) {
            showView("image");
        } else if (restype == ResourceType.BINARY) {
            showView("text");
        } else {
            showView("null");
        }
      
    }//GEN-LAST:event_m_jTypeActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel m_jContainer;
    private javax.swing.ButtonGroup m_jGroupType;
    private com.openbravo.data.gui.JImageEditor m_jImage;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jText;
    private javax.swing.JComboBox m_jType;
    // End of variables declaration//GEN-END:variables
    
}
