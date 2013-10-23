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

package com.openbravo.pos.thirdparties;

import java.awt.Component;
import java.util.UUID;
import com.openbravo.basic.BasicException;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.pos.forms.AppView;

public class ThirdPartiesView extends javax.swing.JPanel implements EditorRecord {

    private Object m_oId;
    
    /** Creates new form ThirdPartiesView */
    public ThirdPartiesView(AppView app, DirtyManager dirty) {
        initComponents();
        
        m_jCIF.getDocument().addDocumentListener(dirty);
        m_jName.getDocument().addDocumentListener(dirty);
        m_jAddress.getDocument().addDocumentListener(dirty);
        m_jContactComm.getDocument().addDocumentListener(dirty);
        m_jContactFact.getDocument().addDocumentListener(dirty);
        m_jPayRule.getDocument().addDocumentListener(dirty);
        m_jFaxNumber.getDocument().addDocumentListener(dirty);
        m_jPhoneNumber.getDocument().addDocumentListener(dirty);
        m_jMobileNumber.getDocument().addDocumentListener(dirty);
        m_jEMail.getDocument().addDocumentListener(dirty);
        m_jWebPage.getDocument().addDocumentListener(dirty);
        m_jNotes.getDocument().addDocumentListener(dirty);

        writeValueEOF();        
    }
    public void writeValueEOF() {
        m_oId = null;
        m_jCIF.setText(null);
        m_jName.setText(null);
        m_jAddress.setText(null);
        m_jContactComm.setText(null);
        m_jContactFact.setText(null);
        m_jPayRule.setText(null);
        m_jFaxNumber.setText(null);
        m_jPhoneNumber.setText(null);
        m_jMobileNumber.setText(null);
        m_jEMail.setText(null);
        m_jWebPage.setText(null);
        m_jNotes.setText(null);
        m_jCIF.setEnabled(false);
        m_jName.setEnabled(false);
        m_jAddress.setEnabled(false);
        m_jContactComm.setEnabled(false);
        m_jContactFact.setEnabled(false);
        m_jPayRule.setEnabled(false);
        m_jFaxNumber.setEnabled(false);
        m_jPhoneNumber.setEnabled(false);
        m_jMobileNumber.setEnabled(false);
        m_jEMail.setEnabled(false);
        m_jWebPage.setEnabled(false);
        m_jNotes.setEnabled(false);
    }    
    
    public void writeValueInsert() {
        m_oId = null;
        m_jCIF.setText(null);
        m_jName.setText(null);
        m_jAddress.setText(null);
        m_jContactComm.setText(null);
        m_jContactFact.setText(null);
        m_jPayRule.setText(null);
        m_jFaxNumber.setText(null);
        m_jPhoneNumber.setText(null);
        m_jMobileNumber.setText(null);
        m_jEMail.setText(null);
        m_jWebPage.setText(null);
        m_jNotes.setText(null);
        m_jCIF.setEnabled(true);
        m_jName.setEnabled(true);
        m_jAddress.setEnabled(true);
        m_jContactComm.setEnabled(true);
        m_jContactFact.setEnabled(true);
        m_jPayRule.setEnabled(true);
        m_jFaxNumber.setEnabled(true);
        m_jPhoneNumber.setEnabled(true);
        m_jMobileNumber.setEnabled(true);
        m_jEMail.setEnabled(true);
        m_jWebPage.setEnabled(true);
        m_jNotes.setEnabled(true);
    }
    
    public void writeValueDelete(Object value) {
        Object[] thirdparty = (Object[]) value;
        m_oId = thirdparty[0];
        m_jCIF.setText((String) thirdparty[1]);
        m_jName.setText((String) thirdparty[2]);
        m_jAddress.setText((String) thirdparty[3]);
        m_jContactComm.setText((String) thirdparty[4]);
        m_jContactFact.setText((String) thirdparty[5]);
        m_jPayRule.setText((String) thirdparty[6]);
        m_jFaxNumber.setText((String) thirdparty[7]);
        m_jPhoneNumber.setText((String) thirdparty[8]);
        m_jMobileNumber.setText((String) thirdparty[9]);
        m_jEMail.setText((String) thirdparty[10]);
        m_jWebPage.setText((String) thirdparty[11]);
        m_jNotes.setText((String) thirdparty[12]);
        m_jCIF.setEnabled(false);
        m_jName.setEnabled(false);
        m_jAddress.setEnabled(false);
        m_jContactComm.setEnabled(false);
        m_jContactFact.setEnabled(false);
        m_jPayRule.setEnabled(false);
        m_jFaxNumber.setEnabled(false);
        m_jPhoneNumber.setEnabled(false);
        m_jMobileNumber.setEnabled(false);
        m_jEMail.setEnabled(false);
        m_jWebPage.setEnabled(false);
        m_jNotes.setEnabled(false);    
    }    
    
    public void writeValueEdit(Object value) {
        Object[] thirdparty = (Object[]) value;
        m_oId = thirdparty[0];
        m_jCIF.setText((String) thirdparty[1]);
        m_jName.setText((String) thirdparty[2]);
        m_jAddress.setText((String) thirdparty[3]);
        m_jContactComm.setText((String) thirdparty[4]);
        m_jContactFact.setText((String) thirdparty[5]);
        m_jPayRule.setText((String) thirdparty[6]);
        m_jFaxNumber.setText((String) thirdparty[7]);
        m_jPhoneNumber.setText((String) thirdparty[8]);
        m_jMobileNumber.setText((String) thirdparty[9]);
        m_jEMail.setText((String) thirdparty[10]);
        m_jWebPage.setText((String) thirdparty[11]);
        m_jNotes.setText((String) thirdparty[12]);
        m_jCIF.setEnabled(true);
        m_jName.setEnabled(true);
        m_jAddress.setEnabled(true);
        m_jContactComm.setEnabled(true);
        m_jContactFact.setEnabled(true);
        m_jPayRule.setEnabled(true);
        m_jFaxNumber.setEnabled(true);
        m_jPhoneNumber.setEnabled(true);
        m_jMobileNumber.setEnabled(true);
        m_jEMail.setEnabled(true);
        m_jWebPage.setEnabled(true);
        m_jNotes.setEnabled(true);
    }
     
    public Object createValue() throws BasicException {
        
        Object[] thirdparty = new Object[13];
        thirdparty[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;
        thirdparty[1] = m_jCIF.getText();
        thirdparty[2] = m_jName.getText();
        thirdparty[3] = m_jAddress.getText();
        thirdparty[4] = m_jContactComm.getText();
        thirdparty[5] = m_jContactFact.getText();
        thirdparty[6] = m_jPayRule.getText();
        thirdparty[7] = m_jFaxNumber.getText();
        thirdparty[8] = m_jPhoneNumber.getText();
        thirdparty[9] = m_jMobileNumber.getText();
        thirdparty[10] = m_jEMail.getText();
        thirdparty[11] = m_jWebPage.getText();
        thirdparty[12] = m_jNotes.getText();

        return thirdparty;
    }
    
    public Component getComponent() {
        return this;
    }   
    
    public void refresh() {
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel2 = new javax.swing.JLabel();
        m_jCIF = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_jContactComm = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        m_jContactFact = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        m_jPayRule = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        m_jFaxNumber = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        m_jPhoneNumber = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        m_jMobileNumber = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        m_jEMail = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        m_jWebPage = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jNotes = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        m_jAddress = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();

        setLayout(null);

        jLabel2.setText("CIF");
        add(jLabel2);
        jLabel2.setBounds(20, 20, 90, 15);

        add(m_jCIF);
        m_jCIF.setBounds(110, 20, 270, 19);

        jLabel3.setText("Nombre");
        add(jLabel3);
        jLabel3.setBounds(20, 50, 90, 15);

        add(m_jName);
        m_jName.setBounds(110, 50, 270, 19);

        jLabel4.setText("Comercial");
        add(jLabel4);
        jLabel4.setBounds(20, 190, 90, 15);

        add(m_jContactComm);
        m_jContactComm.setBounds(110, 190, 160, 19);

        jLabel5.setText("Facturaci\u00f3n");
        add(jLabel5);
        jLabel5.setBounds(290, 190, 90, 15);

        add(m_jContactFact);
        m_jContactFact.setBounds(380, 190, 160, 19);

        jLabel6.setText("Forma de pago");
        add(jLabel6);
        jLabel6.setBounds(20, 340, 90, 15);

        add(m_jPayRule);
        m_jPayRule.setBounds(110, 340, 160, 19);

        jLabel7.setText("N\u00ba de fax");
        add(jLabel7);
        jLabel7.setBounds(20, 250, 90, 15);

        add(m_jFaxNumber);
        m_jFaxNumber.setBounds(110, 250, 160, 19);

        jLabel8.setText("N\u00ba de tel\u00e9fono");
        add(jLabel8);
        jLabel8.setBounds(20, 220, 90, 15);

        add(m_jPhoneNumber);
        m_jPhoneNumber.setBounds(110, 220, 160, 19);

        jLabel9.setText("N\u00ba de m\u00f3vil");
        add(jLabel9);
        jLabel9.setBounds(290, 220, 90, 15);

        add(m_jMobileNumber);
        m_jMobileNumber.setBounds(380, 220, 160, 19);

        jLabel10.setText("E Mail");
        add(jLabel10);
        jLabel10.setBounds(290, 250, 90, 15);

        add(m_jEMail);
        m_jEMail.setBounds(380, 250, 160, 19);

        jLabel11.setText("P\u00e1gina web");
        add(jLabel11);
        jLabel11.setBounds(20, 280, 90, 15);

        add(m_jWebPage);
        m_jWebPage.setBounds(110, 280, 270, 19);

        jLabel12.setText("Notas");
        add(jLabel12);
        jLabel12.setBounds(20, 410, 90, 15);

        jLabel13.setText("Direcci\u00f3n");
        add(jLabel13);
        jLabel13.setBounds(20, 80, 90, 15);

        jScrollPane1.setViewportView(m_jNotes);

        add(jScrollPane1);
        jScrollPane1.setBounds(110, 410, 270, 70);

        jScrollPane2.setViewportView(m_jAddress);

        add(jScrollPane2);
        jScrollPane2.setBounds(110, 80, 270, 70);

        jLabel14.setText("Contacto");
        jLabel14.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        add(jLabel14);
        jLabel14.setBounds(20, 160, 520, 16);

        jLabel15.setText("Pagos");
        jLabel15.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        add(jLabel15);
        jLabel15.setBounds(20, 310, 520, 16);

        jLabel16.setText("Notas");
        jLabel16.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        add(jLabel16);
        jLabel16.setBounds(20, 380, 520, 16);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea m_jAddress;
    private javax.swing.JTextField m_jCIF;
    private javax.swing.JTextField m_jContactComm;
    private javax.swing.JTextField m_jContactFact;
    private javax.swing.JTextField m_jEMail;
    private javax.swing.JTextField m_jFaxNumber;
    private javax.swing.JTextField m_jMobileNumber;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jNotes;
    private javax.swing.JTextField m_jPayRule;
    private javax.swing.JTextField m_jPhoneNumber;
    private javax.swing.JTextField m_jWebPage;
    // End of variables declaration//GEN-END:variables
    
}
