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

package com.openbravo.pos.payment;

import com.openbravo.pos.forms.AppLocal;
import javax.swing.*;

public class PaymentPanelMagCard extends javax.swing.JPanel implements PaymentPanel {
    
    private JPaymentNotifier m_notifier;
    private MagCardReader m_cardreader;
    private String track1 = null;
    private String track2 = null;
    private String track3 = null;
    private String m_sTransactionID;
    private double m_dTotal;
    
    /** Creates new form JMagCardReader */
    // public PaymentPanelMagCard(String sReader, JPaymentNotifier notifier) {
    public PaymentPanelMagCard(MagCardReader cardreader, JPaymentNotifier notifier) {
        
        m_notifier = notifier;
        m_cardreader = cardreader;

        initComponents();
        
        if (m_cardreader != null) {
            // Se van a poder efectuar pagos con tarjeta
            m_jKeyFactory.addKeyListener(new KeyBarsListener());   
            jReset.setEnabled(true);
        } else {
            jReset.setEnabled(false);
        }
    }
    
    public JComponent getComponent(){
        return this;
    }
    
    public void activate(String sTransaction, double dTotal) {
        
        m_sTransactionID = sTransaction;
        m_dTotal = dTotal;
        
        resetState();
        
        m_jKeyFactory.setText(null);       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                m_jKeyFactory.requestFocus();
            }
        });
    }
    
    private void resetState() {
        
        m_notifier.setStatus(false, false);  
              
        m_jHolderName.setText(null);
        m_jCardNumber.setText(null);
        m_jExpirationDate.setText(null);
        track1 = null;
        track2 = null;
        track3 = null;
        
        if (m_cardreader != null) {
            // Se van a poder efectuar pagos con tarjeta
            m_cardreader.getMagCard().reset();
        }
    }
    
    public PaymentInfoMagcard getPaymentInfoMagcard() {

        if (m_dTotal > 0.0) {
            return new PaymentInfoMagcard(
                    m_jHolderName.getText(),
                    m_jCardNumber.getText(), 
                    m_jExpirationDate.getText(),
                    track1,
                    track2,
                    track3,
                    m_sTransactionID,
                    m_dTotal);
        } else {
            return new PaymentInfoMagcardRefund(
                    m_jHolderName.getText(),
                    m_jCardNumber.getText(), 
                    m_jExpirationDate.getText(),
                    track1,
                    track2,
                    track3,
                    m_sTransactionID,
                    m_dTotal);
        }
    } 
    
    private void stateTransition() {
        
        if (m_cardreader.getMagCard().isComplete()) {
            m_jHolderName.setText(m_cardreader.getMagCard().getHolderName());
            m_jCardNumber.setText(m_cardreader.getMagCard().getCardNumber());
            m_jExpirationDate.setText(m_cardreader.getMagCard().getExpirationDate());
            track1 = m_cardreader.getMagCard().getTrack1();
            track2 = m_cardreader.getMagCard().getTrack2();
            track3 = m_cardreader.getMagCard().getTrack3();
            m_notifier.setStatus(true, true);  
        } else {
            m_jHolderName.setText(null);
            m_jCardNumber.setText(null);
            m_jExpirationDate.setText(null); 
            track1 = null;
            track3 = null;
            track3 = null;
            m_notifier.setStatus(false, false);  
        }      
    }    
    
    private class KeyBarsListener extends java.awt.event.KeyAdapter {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            m_cardreader.keyPressed(evt);
            stateTransition();
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            m_cardreader.keyReleased(evt);
            stateTransition();
        }
        public void keyTyped(java.awt.event.KeyEvent evt){
            m_jKeyFactory.setText(null);
            m_cardreader.keyTyped(evt);
            stateTransition(); // e.getKeyChar()
        }
    }   
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jReset = new javax.swing.JButton();
        m_jKeyFactory = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        m_jExpirationDate = new javax.swing.JLabel();
        m_jCardNumber = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        m_jHolderName = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setText(AppLocal.getIntString("message.paymentgatewayswipe")); // NOI18N
        jPanel2.add(jLabel1);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(null);

        jReset.setText(AppLocal.getIntString("button.reset")); // NOI18N
        jReset.setFocusPainted(false);
        jReset.setFocusable(false);
        jReset.setRequestFocusEnabled(false);
        jReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jResetActionPerformed(evt);
            }
        });
        jPanel1.add(jReset);
        jReset.setBounds(380, 20, 90, 30);
        jPanel1.add(m_jKeyFactory);
        m_jKeyFactory.setBounds(0, 0, 0, 0);

        jLabel6.setText(AppLocal.getIntString("label.cardnumber")); // NOI18N
        jPanel1.add(jLabel6);
        jLabel6.setBounds(20, 50, 100, 15);

        jLabel7.setText(AppLocal.getIntString("label.cardexpdate")); // NOI18N
        jPanel1.add(jLabel7);
        jLabel7.setBounds(20, 80, 100, 15);

        m_jExpirationDate.setBackground(java.awt.Color.white);
        m_jExpirationDate.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jExpirationDate.setOpaque(true);
        m_jExpirationDate.setPreferredSize(new java.awt.Dimension(150, 25));
        jPanel1.add(m_jExpirationDate);
        m_jExpirationDate.setBounds(120, 80, 70, 25);

        m_jCardNumber.setBackground(java.awt.Color.white);
        m_jCardNumber.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jCardNumber.setOpaque(true);
        m_jCardNumber.setPreferredSize(new java.awt.Dimension(150, 25));
        jPanel1.add(m_jCardNumber);
        m_jCardNumber.setBounds(120, 50, 250, 25);

        jLabel8.setText(AppLocal.getIntString("label.cardholder")); // NOI18N
        jPanel1.add(jLabel8);
        jLabel8.setBounds(20, 20, 100, 15);

        m_jHolderName.setBackground(java.awt.Color.white);
        m_jHolderName.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jHolderName.setOpaque(true);
        m_jHolderName.setPreferredSize(new java.awt.Dimension(150, 25));
        jPanel1.add(m_jHolderName);
        m_jHolderName.setBounds(120, 20, 250, 25);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jResetActionPerformed

        resetState();
        
    }//GEN-LAST:event_jResetActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jReset;
    private javax.swing.JLabel m_jCardNumber;
    private javax.swing.JLabel m_jExpirationDate;
    private javax.swing.JLabel m_jHolderName;
    private javax.swing.JTextArea m_jKeyFactory;
    // End of variables declaration//GEN-END:variables
    
}
