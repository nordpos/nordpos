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

package com.openbravo.pos.sales.restaurant;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.*;
import java.util.*;

import com.openbravo.beans.*;
import com.openbravo.data.gui.*;
import com.openbravo.data.loader.*;
import com.openbravo.data.user.*;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.customers.JCustomerFinder;
import com.openbravo.pos.customers.CustomerInfo;

public class JTicketsBagRestaurantRes extends javax.swing.JPanel implements EditorRecord {

    private JTicketsBagRestaurantMap m_restaurantmap;
    
    private DataLogicCustomers dlCustomers = null;
    
    private DirtyManager m_Dirty;
    private Object m_sID;
    private CustomerInfo customer;
    private Date m_dCreated;
    private JTimePanel m_timereservation;
    private boolean m_bReceived;
    private BrowsableEditableData m_bd;
        
    private Date m_dcurrentday;
    
    private JCalendarPanel m_datepanel;    
    private JTimePanel m_timepanel;
    private boolean m_bpaintlock = false;

    // private Date dinitdate = new GregorianCalendar(1900, 0, 0, 12, 0).getTime();
    
    /** Creates new form JPanelReservations */
    public JTicketsBagRestaurantRes(AppView oApp, JTicketsBagRestaurantMap restaurantmap) {
        
        m_restaurantmap = restaurantmap;
        
        dlCustomers = (DataLogicCustomers) oApp.getBean("com.openbravo.pos.customers.DataLogicCustomers");

        m_dcurrentday = null;
        
        initComponents();
        
        m_datepanel = new JCalendarPanel();
        jPanelDate.add(m_datepanel, BorderLayout.CENTER);
        m_datepanel.addPropertyChangeListener("Date", new DateChangeCalendarListener());
        
        m_timepanel = new JTimePanel(null, JTimePanel.BUTTONS_HOUR);
        m_timepanel.setPeriod(3600000L); // Los milisegundos que tiene una hora.
        jPanelTime.add(m_timepanel, BorderLayout.CENTER);
        m_timepanel.addPropertyChangeListener("Date", new DateChangeTimeListener());
        
        m_timereservation = new JTimePanel(null, JTimePanel.BUTTONS_MINUTE);
        m_jPanelTime.add(m_timereservation, BorderLayout.CENTER);   
            
        txtCustomer.addEditorKeys(m_jKeys);
        m_jtxtChairs.addEditorKeys(m_jKeys);
        m_jtxtDescription.addEditorKeys(m_jKeys);

        m_Dirty = new DirtyManager();
        m_timereservation.addPropertyChangeListener("Date", m_Dirty);
        txtCustomer.addPropertyChangeListener("Text", m_Dirty);
        txtCustomer.addPropertyChangeListener("Text", new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                customer = new CustomerInfo(null);
                customer.setTaxid(null);
                customer.setSearchkey(null);
                customer.setName(txtCustomer.getText());            
            }
        });
        m_jtxtChairs.addPropertyChangeListener("Text", m_Dirty);
        m_jtxtDescription.addPropertyChangeListener("Text", m_Dirty);
        
        writeValueEOF();
        
        ListProvider lpr = new ListProviderCreator(dlCustomers.getReservationsList(), new MyDateFilter());            
        SaveProvider spr = new SaveProvider(dlCustomers.getReservationsUpdate(), dlCustomers.getReservationsInsert(), dlCustomers.getReservationsDelete());        
        
        m_bd = new BrowsableEditableData(lpr, spr, new CompareReservations(), this, m_Dirty);           
        
        JListNavigator nl = new JListNavigator(m_bd, true);
        nl.setCellRenderer(new JCalendarItemRenderer());  
        m_jPanelList.add(nl, BorderLayout.CENTER);
        
        // La Toolbar
        m_jToolbar.add(new JLabelDirty(m_Dirty));
        m_jToolbar.add(new JCounter(m_bd));
        m_jToolbar.add(new JNavigator(m_bd));
        m_jToolbar.add(new JSaver(m_bd));       
    }
    
    private class MyDateFilter implements EditorCreator {
        public Object createValue() throws BasicException {           
            return new Object[] {m_dcurrentday, new Date(m_dcurrentday.getTime() + 3600000L)};   // m_dcurrentday ya no tiene ni minutos, ni segundos.             
        }
    }
    
    public void activate() {
        reload(DateUtils.getTodayHours(new Date()));
    }
    
    public void refresh() {
    }  
    
    public boolean deactivate() {
        try {
            return m_bd.actionClosingForm(this);
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.CannotMove"), eD);
            msg.show(this);
            return false;
        }
    }
    
    public void writeValueEOF() {
        m_sID = null;
        m_dCreated = null;
        m_timereservation.setDate(null);
        assignCustomer(new CustomerInfo(null));
        m_jtxtChairs.reset();
        m_bReceived = false;
        m_jtxtDescription.reset();
        m_timereservation.setEnabled(false);
        txtCustomer.setEnabled(false);
        m_jtxtChairs.setEnabled(false);
        m_jtxtDescription.setEnabled(false);
        m_jKeys.setEnabled(false);
        
        m_jbtnReceive.setEnabled(false);
    }    
    public void writeValueInsert() {
        m_sID = null;
        m_dCreated = null;
        m_timereservation.setCheckDates(m_dcurrentday, new Date(m_dcurrentday.getTime() + 3600000L));
        m_timereservation.setDate(m_dcurrentday);
        assignCustomer(new CustomerInfo(null));
        m_jtxtChairs.setValueInteger(2);
        m_bReceived = false;
        m_jtxtDescription.reset();
        m_timereservation.setEnabled(true);
        txtCustomer.setEnabled(true);
        m_jtxtChairs.setEnabled(true);
        m_jtxtDescription.setEnabled(true);
        m_jKeys.setEnabled(true);
        
        m_jbtnReceive.setEnabled(true);
        
        txtCustomer.activate();
    }
    public void writeValueDelete(Object value) {
        Object[] res = (Object[]) value;
        m_sID = res[0];
        m_dCreated = (Date) res[1];
        m_timereservation.setCheckDates(m_dcurrentday, new Date(m_dcurrentday.getTime() + 3600000L));
        m_timereservation.setDate((Date) res[2]);       
        CustomerInfo c = new CustomerInfo((String) res[3]);
        c.setTaxid((String) res[4]);
        c.setSearchkey((String) res[5]);
        c.setName((String) res[6]);
        assignCustomer(c);        
        m_jtxtChairs.setValueInteger(((Integer)res[7]).intValue());
        m_bReceived = ((Boolean)res[8]).booleanValue();
        m_jtxtDescription.setText(Formats.STRING.formatValue(res[9]));
        m_timereservation.setEnabled(false);
        txtCustomer.setEnabled(false);
        m_jtxtChairs.setEnabled(false);
        m_jtxtDescription.setEnabled(false);
        m_jKeys.setEnabled(false);
        
        m_jbtnReceive.setEnabled(false); 
    }  
    public void writeValueEdit(Object value) {
        Object[] res = (Object[]) value;
        m_sID = res[0];
        m_dCreated = (Date) res[1];
        m_timereservation.setCheckDates(m_dcurrentday, new Date(m_dcurrentday.getTime() + 3600000L));
        m_timereservation.setDate((Date) res[2]);
        CustomerInfo c = new CustomerInfo((String) res[3]);
        c.setTaxid((String) res[4]);
        c.setSearchkey((String) res[5]);
        c.setName((String) res[6]);
        assignCustomer(c);  
        m_jtxtChairs.setValueInteger(((Integer)res[7]).intValue());
        m_bReceived = ((Boolean)res[8]).booleanValue();
        m_jtxtDescription.setText(Formats.STRING.formatValue(res[9]));
        m_timereservation.setEnabled(true);
        txtCustomer.setEnabled(true);
        m_jtxtChairs.setEnabled(true);
        m_jtxtDescription.setEnabled(true);
        m_jKeys.setEnabled(true);

        m_jbtnReceive.setEnabled(!m_bReceived); // se habilita si no se ha recibido al cliente

        txtCustomer.activate();
    }    

    public Object createValue() throws BasicException {
        
        Object[] res = new Object[10];
        
        res[0] = m_sID == null ? UUID.randomUUID().toString() : m_sID; 
        res[1] = m_dCreated == null ? new Date() : m_dCreated; 
        res[2] = m_timereservation.getDate();
        res[3] = customer.getId();
        res[4] = customer.getTaxid();
        res[5] = customer.getSearchkey();
        res[6] = customer.getName();
        res[7] = new Integer(m_jtxtChairs.getValueInteger());
        res[8] = new Boolean(m_bReceived);
        res[9] = m_jtxtDescription.getText();

        return res;
    }    
    
    public Component getComponent() {
        return this;
    }  
    
    private static class CompareReservations implements Comparator {
        public int compare(Object o1, Object o2) {
            Object[] a1 = (Object[]) o1;
            Object[] a2 = (Object[]) o2;
            Date d1 = (Date) a1[2];
            Date d2 = (Date) a2[2];
            int c = d1.compareTo(d2);
            if (c == 0) {
                d1 = (Date) a1[1];
                d2 = (Date) a2[1];
                return d1.compareTo(d2);
            } else {
                return c;
            }
        }
    }
    
    private void reload(Date dDate) {
        
        if (!dDate.equals(m_dcurrentday)) {
   
            Date doldcurrentday = m_dcurrentday;
            m_dcurrentday = dDate;
            try {
                m_bd.actionLoad();
            } catch (BasicException eD) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.noreload"), eD);
                msg.show(this);
                m_dcurrentday = doldcurrentday; // nos retractamos...
            }
        }    
        
        // pinto la fecha del filtro...
        paintDate();
    }
    
    private void paintDate() {
        
        m_bpaintlock = true;
        m_datepanel.setDate(m_dcurrentday);
        m_timepanel.setDate(m_dcurrentday);
        m_bpaintlock = false;
    }
       
    private void assignCustomer(CustomerInfo c) {
        
        txtCustomer.setText(c.getName());
        customer = c;
    }
    
    private class DateChangeCalendarListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (!m_bpaintlock) {
                reload(DateUtils.getTodayHours(DateUtils.getDate(m_datepanel.getDate(), m_timepanel.getDate())));
            }
        }        
    }
        
    private class DateChangeTimeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (!m_bpaintlock) {
                reload(DateUtils.getTodayHours(DateUtils.getDate(m_datepanel.getDate(), m_timepanel.getDate())));
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

        jPanel3 = new javax.swing.JPanel();
        jPanelDate = new javax.swing.JPanel();
        jPanelTime = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jToolbarContainer = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jbtnTables = new javax.swing.JButton();
        m_jbtnReceive = new javax.swing.JButton();
        m_jToolbar = new javax.swing.JPanel();
        m_jPanelList = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jPanelTime = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        m_jtxtDescription = new com.openbravo.editor.JEditorString();
        m_jtxtChairs = new com.openbravo.editor.JEditorIntegerPositive();
        txtCustomer = new com.openbravo.editor.JEditorString();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        m_jKeys = new com.openbravo.editor.JEditorKeys();

        setLayout(new java.awt.BorderLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(10, 210));

        jPanelDate.setPreferredSize(new java.awt.Dimension(410, 190));
        jPanelDate.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPanelDate);

        jPanelTime.setPreferredSize(new java.awt.Dimension(310, 190));
        jPanelTime.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPanelTime);

        add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        m_jToolbarContainer.setLayout(new java.awt.BorderLayout());

        m_jbtnTables.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/atlantikdesignersmall.png"))); // NOI18N
        m_jbtnTables.setText(AppLocal.getIntString("button.tables")); // NOI18N
        m_jbtnTables.setFocusPainted(false);
        m_jbtnTables.setFocusable(false);
        m_jbtnTables.setRequestFocusEnabled(false);
        m_jbtnTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnTablesActionPerformed(evt);
            }
        });
        jPanel4.add(m_jbtnTables);

        m_jbtnReceive.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/mime.png"))); // NOI18N
        m_jbtnReceive.setText(AppLocal.getIntString("button.receive")); // NOI18N
        m_jbtnReceive.setFocusPainted(false);
        m_jbtnReceive.setFocusable(false);
        m_jbtnReceive.setRequestFocusEnabled(false);
        m_jbtnReceive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnReceiveActionPerformed(evt);
            }
        });
        jPanel4.add(m_jbtnReceive);

        m_jToolbarContainer.add(jPanel4, java.awt.BorderLayout.LINE_START);
        m_jToolbarContainer.add(m_jToolbar, java.awt.BorderLayout.CENTER);

        jPanel2.add(m_jToolbarContainer, java.awt.BorderLayout.NORTH);

        m_jPanelList.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jPanelList.setLayout(new java.awt.BorderLayout());
        jPanel2.add(m_jPanelList, java.awt.BorderLayout.LINE_START);

        jPanel1.setLayout(null);

        m_jPanelTime.setLayout(new java.awt.BorderLayout());
        jPanel1.add(m_jPanelTime);
        m_jPanelTime.setBounds(90, 10, 240, 130);

        jLabel1.setText(AppLocal.getIntString("rest.label.date")); // NOI18N
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 10, 80, 15);

        jLabel2.setText(AppLocal.getIntString("rest.label.customer")); // NOI18N
        jPanel1.add(jLabel2);
        jLabel2.setBounds(10, 150, 80, 15);

        jLabel3.setText(AppLocal.getIntString("rest.label.chairs")); // NOI18N
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 180, 80, 15);

        jLabel4.setText(AppLocal.getIntString("rest.label.notes")); // NOI18N
        jPanel1.add(jLabel4);
        jLabel4.setBounds(10, 210, 80, 15);
        jPanel1.add(m_jtxtDescription);
        m_jtxtDescription.setBounds(90, 210, 220, 50);
        jPanel1.add(m_jtxtChairs);
        m_jtxtChairs.setBounds(90, 180, 140, 20);
        jPanel1.add(txtCustomer);
        txtCustomer.setBounds(90, 150, 220, 20);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/kuser.png"))); // NOI18N
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(310, 150, 50, 26);

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new java.awt.BorderLayout());
        jPanel5.add(m_jKeys, java.awt.BorderLayout.NORTH);

        jPanel2.add(jPanel5, java.awt.BorderLayout.LINE_END);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnReceiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnReceiveActionPerformed
             
        
        // marco el cliente como recibido...
        m_bReceived = true;
        m_Dirty.setDirty(true);
        
        try {
            m_bd.saveData();
            m_restaurantmap.viewTables(customer);                    
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.nosaveticket"), eD);
            msg.show(this);
        }       
        
    }//GEN-LAST:event_m_jbtnReceiveActionPerformed

    private void m_jbtnTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnTablesActionPerformed

        m_restaurantmap.viewTables();
        
    }//GEN-LAST:event_m_jbtnTablesActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        JCustomerFinder finder = JCustomerFinder.getCustomerFinder(this, dlCustomers);
        finder.search(customer);
        finder.setVisible(true);
        
        CustomerInfo c = finder.getSelectedCustomer(); 
        
        if (c == null) {       
            assignCustomer(new CustomerInfo(null));
        } else {
            assignCustomer(c);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelDate;
    private javax.swing.JPanel jPanelTime;
    private com.openbravo.editor.JEditorKeys m_jKeys;
    private javax.swing.JPanel m_jPanelList;
    private javax.swing.JPanel m_jPanelTime;
    private javax.swing.JPanel m_jToolbar;
    private javax.swing.JPanel m_jToolbarContainer;
    private javax.swing.JButton m_jbtnReceive;
    private javax.swing.JButton m_jbtnTables;
    private com.openbravo.editor.JEditorIntegerPositive m_jtxtChairs;
    private com.openbravo.editor.JEditorString m_jtxtDescription;
    private com.openbravo.editor.JEditorString txtCustomer;
    // End of variables declaration//GEN-END:variables
    
}
