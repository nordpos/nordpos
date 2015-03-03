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

import com.openbravo.basic.BasicException;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.util.RoundUtils;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class JPaymentDebt extends javax.swing.JPanel implements JPaymentInterface {

    private final JPaymentNotifier notifier;
    private final DataLogicSales dlSales;
    private CustomerInfoExt customerext;

    private double m_dPaid;
    private double m_dTotal;

    public JPaymentDebt(JPaymentNotifier notifier, DataLogicSales dlSales) {

        this.notifier = notifier;
        this.dlSales = dlSales;

        initComponents();

        m_jTendered.addPropertyChangeListener("Edition", new RecalculateState());
        m_jTendered.addEditorKeys(m_jKeys);

    }

    @Override
    public void activate(PaymentInfoList paymentInfoList, CustomerInfoExt customerext, double dTotal, String transID) {

        if (customerext != null) {
            try {
                customerext = dlSales.loadCustomerExt(customerext.getId());
                double debt = customerext.getCurdebt() == null ? 0.0 : customerext.getCurdebt();
                for (PaymentInfo payment : paymentInfoList.getPayments()) {
                    if (payment.getName().equals("debt")) {
                        debt = debt + payment.getTotal();
                    }
                }
                customerext.setCurdebt(debt);
            } catch (BasicException ex) {
                customerext = null;
            }
        }

        m_dTotal = dTotal;

        m_jTendered.reset();

        if (customerext == null) {
            m_jName.setText(null);

            m_jKeys.setEnabled(false);
            m_jTendered.setEnabled(false);

        } else {
            m_jName.setText(customerext.getName());

            if (RoundUtils.compare(RoundUtils.getValue(customerext.getCurdebt()), RoundUtils.getValue(customerext.getMaxdebt())) >= 0) {
                m_jKeys.setEnabled(false);
                m_jTendered.setEnabled(false);
            } else {
                m_jKeys.setEnabled(true);
                m_jTendered.setEnabled(true);
                m_jTendered.activate();
            }
        }

        this.customerext = customerext;
        printState();

    }

    @Override
    public PaymentInfo executePayment() {
        try {
            customerext = dlSales.loadCustomerExt(customerext.getId());
        } catch (BasicException ex) {
            return null;
        }

        if (RoundUtils.compare(RoundUtils.getValue(customerext.getCurdebt()) + m_dPaid, RoundUtils.getValue(customerext.getMaxdebt())) >= 0) {
            printState();
            return null;
        } else {
            return new PaymentInfoTicket(m_dPaid, "debt");
        }

    }

    @Override
    public Component getComponent() {
        return this;
    }

    private void printState() {

        if (customerext == null) {
            m_jNotes.setText(null);
            txtMaxdebt.setText(null);
            txtCurdate.setText(null);
            txtCurdebt.setText(null);

            m_jMoneyEuros.setText(null);
            jlblMessage.setText(AppLocal.getIntString("message.nocustomernodebt"));
            notifier.setStatus(false, false);
        } else {
            m_jNotes.setText(customerext.getNotes());
            txtMaxdebt.setText(Formats.CURRENCY.formatValue(RoundUtils.getValue(customerext.getMaxdebt())));
            txtCurdate.setText(Formats.DATE.formatValue(customerext.getCurdate()));
            txtCurdebt.setText(Formats.CURRENCY.formatValue(RoundUtils.getValue(customerext.getCurdebt())));

            Double value = m_jTendered.getDoubleValue();
            if (value == null || value == 0.0) {
                m_dPaid = m_dTotal;
            } else {
                m_dPaid = value;
            }

            m_jMoneyEuros.setText(Formats.CURRENCY.formatValue(m_dPaid));

            if (RoundUtils.compare(RoundUtils.getValue(customerext.getCurdebt()) + m_dPaid, RoundUtils.getValue(customerext.getMaxdebt())) >= 0) {
                // maximum debt exceded
                jlblMessage.setText(AppLocal.getIntString("message.customerdebtexceded"));
                notifier.setStatus(false, false);
            } else {
                jlblMessage.setText(null);
                int iCompare = RoundUtils.compare(m_dPaid, m_dTotal);
                // if iCompare > 0 then the payment is not valid
                notifier.setStatus(m_dPaid > 0.0 && iCompare <= 0, iCompare == 0);
            }

        }
    }

    private class RecalculateState implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            printState();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        m_jMoneyEuros = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMaxdebt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCurdebt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCurdate = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jNotes = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jlblMessage = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jKeys = new com.openbravo.editor.JEditorKeys();
        jPanel3 = new javax.swing.JPanel();
        m_jTendered = new com.openbravo.editor.JEditorCurrencyPositive();

        setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel8.setText(AppLocal.getIntString("label.debt")); // NOI18N

        m_jMoneyEuros.setBackground(new java.awt.Color(153, 153, 255));
        m_jMoneyEuros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jMoneyEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jMoneyEuros.setOpaque(true);
        m_jMoneyEuros.setPreferredSize(new java.awt.Dimension(150, 25));

        jLabel3.setText(AppLocal.getIntString("label.name")); // NOI18N

        m_jName.setEditable(false);

        jLabel12.setText(AppLocal.getIntString("label.notes")); // NOI18N

        jLabel2.setText(AppLocal.getIntString("label.maxdebt")); // NOI18N

        txtMaxdebt.setEditable(false);
        txtMaxdebt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText(AppLocal.getIntString("label.curdebt")); // NOI18N

        txtCurdebt.setEditable(false);
        txtCurdebt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setText(AppLocal.getIntString("label.curdate")); // NOI18N

        txtCurdate.setEditable(false);
        txtCurdate.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        m_jNotes.setEditable(false);
        jScrollPane1.setViewportView(m_jNotes);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCurdate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtMaxdebt, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                        .addComponent(txtCurdebt, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(m_jMoneyEuros, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(95, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(m_jMoneyEuros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(txtMaxdebt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(txtCurdebt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(txtCurdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.add(jPanel4, java.awt.BorderLayout.CENTER);

        jlblMessage.setEditable(false);
        jlblMessage.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        jlblMessage.setLineWrap(true);
        jlblMessage.setWrapStyleWord(true);
        jlblMessage.setFocusable(false);
        jlblMessage.setPreferredSize(new java.awt.Dimension(300, 64));
        jlblMessage.setRequestFocusEnabled(false);
        jPanel6.add(jlblMessage);

        jPanel5.add(jPanel6, java.awt.BorderLayout.SOUTH);

        add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jPanel1.add(m_jKeys);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel3.add(m_jTendered, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3);

        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(jPanel2, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jlblMessage;
    private com.openbravo.editor.JEditorKeys m_jKeys;
    private javax.swing.JLabel m_jMoneyEuros;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jNotes;
    private com.openbravo.editor.JEditorCurrencyPositive m_jTendered;
    private javax.swing.JTextField txtCurdate;
    private javax.swing.JTextField txtCurdebt;
    private javax.swing.JTextField txtMaxdebt;
    // End of variables declaration//GEN-END:variables

}
