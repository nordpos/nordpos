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

import com.openbravo.pos.ticket.TicketInfo;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.openbravo.pos.sales.*;
import com.openbravo.pos.forms.*; 
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.ticket.TicketLineInfo;

public class JTicketsBagRestaurantMap extends JTicketsBag {

//    private static final Icon ICO_OCU = new ImageIcon(JTicketsBag.class.getResource("/com/openbravo/images/edit_group.png"));
//    private static final Icon ICO_FRE = new NullIcon(22, 22);
        
    private java.util.List<Place> m_aplaces;
    private java.util.List<Floor> m_afloors;
    
    private JTicketsBagRestaurant m_restaurantmap;  
    private JTicketsBagRestaurantRes m_jreservations;   
    
    private Place m_PlaceCurrent;
    
    // State vars
    private Place m_PlaceClipboard;  
    private CustomerInfo customer;

    private DataLogicReceipts dlReceipts = null;
    private DataLogicSales dlSales = null;
    
    /** Creates new form JTicketsBagRestaurant */
    public JTicketsBagRestaurantMap(AppView app, TicketsEditor panelticket) {
        
        super(app, panelticket);
        
        dlReceipts = (DataLogicReceipts) app.getBean("com.openbravo.pos.sales.DataLogicReceipts");
        dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        
        m_restaurantmap = new JTicketsBagRestaurant(app, this);
        m_PlaceCurrent = null;
        m_PlaceClipboard = null;
        customer = null;
            
        try {
            SentenceList sent = new StaticSentence(
                    app.getSession(), 
                    "SELECT ID, NAME, IMAGE FROM FLOORS ORDER BY NAME", 
                    null, 
                    new SerializerReadClass(Floor.class));
            m_afloors = sent.list();
               
                
            
        } catch (BasicException eD) {
            m_afloors = new ArrayList<Floor>();
        }
        try {
            SentenceList sent = new StaticSentence(
                    app.getSession(), 
                    "SELECT ID, NAME, X, Y, FLOOR FROM PLACES ORDER BY FLOOR", 
                    null, 
                    new SerializerReadClass(Place.class));
            m_aplaces = sent.list();
        } catch (BasicException eD) {
            m_aplaces = new ArrayList<Place>();
        } 
        
        initComponents(); 
          
        // add the Floors containers
        if (m_afloors.size() > 1) {
            // A tab container for 2 or more floors
            JTabbedPane jTabFloors = new JTabbedPane();
            jTabFloors.applyComponentOrientation(getComponentOrientation());
            jTabFloors.setBorder(new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)));
            jTabFloors.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            jTabFloors.setFocusable(false);
            jTabFloors.setRequestFocusEnabled(false);
            m_jPanelMap.add(jTabFloors, BorderLayout.CENTER);
            
            for (Floor f : m_afloors) {
                f.getContainer().applyComponentOrientation(getComponentOrientation());
                
                JScrollPane jScrCont = new JScrollPane();
                jScrCont.applyComponentOrientation(getComponentOrientation());
                JPanel jPanCont = new JPanel();  
                jPanCont.applyComponentOrientation(getComponentOrientation());
                
                jTabFloors.addTab(f.getName(), f.getIcon(), jScrCont);     
                jScrCont.setViewportView(jPanCont);
                jPanCont.add(f.getContainer());
            }
        } else if (m_afloors.size() == 1) {
            // Just a frame for 1 floor
            Floor f = m_afloors.get(0);
            f.getContainer().applyComponentOrientation(getComponentOrientation());
            
            JPanel jPlaces = new JPanel();
            jPlaces.applyComponentOrientation(getComponentOrientation());
            jPlaces.setLayout(new BorderLayout());
            jPlaces.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)),
                    new javax.swing.border.TitledBorder(f.getName())));
            
            JScrollPane jScrCont = new JScrollPane();
            jScrCont.applyComponentOrientation(getComponentOrientation());
            JPanel jPanCont = new JPanel();
            jPanCont.applyComponentOrientation(getComponentOrientation());
            
            // jPlaces.setLayout(new FlowLayout());           
            m_jPanelMap.add(jPlaces, BorderLayout.CENTER);
            jPlaces.add(jScrCont, BorderLayout.CENTER);
            jScrCont.setViewportView(jPanCont);            
            jPanCont.add(f.getContainer());
        }   
        
        // Add all the Table buttons.
        Floor currfloor = null;
        
        
        for (Place pl : m_aplaces) {
            int iFloor = 0;
            
            if (currfloor == null || !currfloor.getID().equals(pl.getFloor())) {
                // Look for a new floor
                do {
                    currfloor = m_afloors.get(iFloor++);
                } while (!currfloor.getID().equals(pl.getFloor()));
            }

            currfloor.getContainer().add(pl.getButton());
            pl.setButtonBounds();
            pl.getButton().addActionListener(new MyActionListener(pl));
        }
        
        // Add the reservations panel
        m_jreservations = new JTicketsBagRestaurantRes(app, this);
        add(m_jreservations, "res");
    }
    
    public void activate() {
        
        // precondicion es que no tenemos ticket activado ni ticket en el panel

        m_PlaceClipboard = null;
        customer = null;
        loadTickets();        
        printState(); 
        
        m_panelticket.setActiveTicket(null, null); 
        m_restaurantmap.activate();
       
        showView("map"); // arrancamos en la vista de las mesas.
        
        // postcondicion es que tenemos ticket activado aqui y ticket en el panel
    }
    
    public boolean deactivate() {
        
        // precondicion es que tenemos ticket activado aqui y ticket en el panel
        
        if (viewTables()) {
        
            // borramos el clipboard
            m_PlaceClipboard = null;
            customer = null;

            // guardamos el ticket
            if (m_PlaceCurrent != null) {
                            
                try {
                    dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());
                } catch (BasicException e) {
                    new MessageInf(e).show(this);
                }                                  
 
                m_PlaceCurrent = null;
            }

            // desactivamos cositas.
            printState();     
            m_panelticket.setActiveTicket(null, null); 

            return true;
        } else {
            return false;
        }
        
        // postcondicion es que no tenemos ticket activado
    }

        
    protected JComponent getBagComponent() {
        return m_restaurantmap;
    }
    protected JComponent getNullComponent() {
        return this;
    }
   
    public void moveTicket() {
        
        // guardamos el ticket
        if (m_PlaceCurrent != null) {
                          
            try {
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }      
            
            // me guardo el ticket que quiero copiar.
            m_PlaceClipboard = m_PlaceCurrent;    
            customer = null;
            m_PlaceCurrent = null;
        }
        
        printState();
        m_panelticket.setActiveTicket(null, null);
    }
    
    public boolean viewTables(CustomerInfo c) {
        // deberiamos comprobar si estamos en reservations o en tables...
        if (m_jreservations.deactivate()) {
            showView("map"); // arrancamos en la vista de las mesas.
            
            m_PlaceClipboard = null;    
            customer = c;     
            printState();
            
            return true;
        } else {
            return false;
        }        
    }
    
    public boolean viewTables() {
        return viewTables(null);
    }
        
    public void newTicket() {
        
        // guardamos el ticket
        if (m_PlaceCurrent != null) {
                         
            try {
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());                
            } catch (BasicException e) {
                new MessageInf(e).show(this); // maybe other guy deleted it
            }              

            m_PlaceCurrent = null;
        }
        
        printState();     
        m_panelticket.setActiveTicket(null, null);     
    }
    
    public void deleteTicket() {
        
        if (m_PlaceCurrent != null) {
            
            String id = m_PlaceCurrent.getId();
            try {
                dlReceipts.deleteSharedTicket(id);
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }       
            
            m_PlaceCurrent.setPeople(false);
            
            m_PlaceCurrent = null;
        }        
        
        printState();     
        m_panelticket.setActiveTicket(null, null); 
    }
    
    public void loadTickets() {

        Set<String> atickets = new HashSet<String>();
        
        try {
            java.util.List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();
            for (SharedTicketInfo ticket : l) {
                atickets.add(ticket.getId());
            }
        } catch (BasicException e) {
            new MessageInf(e).show(this);
        }            
            
        for (Place table : m_aplaces) {
            table.setPeople(atickets.contains(table.getId()));
        }
    }
    
    private void printState() {
        
        if (m_PlaceClipboard == null) {
            if (customer == null) {
                // Select a table
                m_jText.setText(null);
                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(true);
                }
                m_jbtnReservations.setEnabled(true);
            } else {
                // receive a customer
                m_jText.setText(AppLocal.getIntString("label.restaurantcustomer", new Object[] {customer.getName()}));
                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(!place.hasPeople());
                }                
                m_jbtnReservations.setEnabled(false);
            }
        } else {
            // Moving or merging the receipt to another table
            m_jText.setText(AppLocal.getIntString("label.restaurantmove", new Object[] {m_PlaceClipboard.getName()}));
            // Enable all empty tables and origin table.
            for (Place place : m_aplaces) {
                place.getButton().setEnabled(true);
            }  
            m_jbtnReservations.setEnabled(false);
        }
    }   
    
    private TicketInfo getTicketInfo(Place place) {

        try {
            return dlReceipts.getSharedTicket(place.getId());
        } catch (BasicException e) {
            new MessageInf(e).show(JTicketsBagRestaurantMap.this);
            return null;
        }
    }
    
    private void setActivePlace(Place place, TicketInfo ticket) {
        m_PlaceCurrent = place;
        m_panelticket.setActiveTicket(ticket, m_PlaceCurrent.getName());
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout)(getLayout());
        cl.show(this, view);  
    }
    
    private class MyActionListener implements ActionListener {
        
        private Place m_place;
        
        public MyActionListener(Place place) {
            m_place = place;
        }
        
        public void actionPerformed(ActionEvent evt) {    
            
            if (m_PlaceClipboard == null) {  
                
                if (customer == null) {
                    // tables
                
                    // check if the sharedticket is the same
                    TicketInfo ticket = getTicketInfo(m_place);

                    // check
                    if (ticket == null && !m_place.hasPeople()) {
                        // Empty table and checked

                        // table occupied
                        ticket = new TicketInfo();
                        try {
                            dlReceipts.insertSharedTicket(m_place.getId(), ticket);
                        } catch (BasicException e) {
                            new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                        }                     
                        m_place.setPeople(true);
                        setActivePlace(m_place, ticket);

                    } else if (ticket == null  && m_place.hasPeople()) {
                        // The table is now empty
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JTicketsBagRestaurantMap.this);
                        m_place.setPeople(false); // fixed        

                    } else if (ticket != null && !m_place.hasPeople()) {
                        // The table is now full
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JTicketsBagRestaurantMap.this);       
                        m_place.setPeople(true);

                    } else { // both != null
                        // Full table                
                        // m_place.setPeople(true); // already true                           
                        setActivePlace(m_place, ticket);                   
                    }
                } else {
                    // receiving customer.
                    
                    // check if the sharedticket is the same
                    TicketInfo ticket = getTicketInfo(m_place);
                    if (ticket == null) {
                        // receive the customer
                        // table occupied
                        ticket = new TicketInfo();
                        
                        try {
                            ticket.setCustomer(customer.getId() == null
                                    ? null
                                    : dlSales.loadCustomerExt(customer.getId()));
                        } catch (BasicException e) {
                            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), e);
                            msg.show(JTicketsBagRestaurantMap.this);            
                        }                     
                        
                        try {
                            dlReceipts.insertSharedTicket(m_place.getId(), ticket);
                        } catch (BasicException e) {
                            new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                        }                     
                        m_place.setPeople(true);
                        
                        m_PlaceClipboard = null;
                        customer = null;     
                        
                        setActivePlace(m_place, ticket);                        
                    } else {
                        // TODO: msg: The table is now full
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JTicketsBagRestaurantMap.this);       
                        m_place.setPeople(true);
                        m_place.getButton().setEnabled(false);
                    }
                }
            } else {
                // check if the sharedticket is the same
                TicketInfo ticketclip = getTicketInfo(m_PlaceClipboard);

                if (ticketclip == null) {
                    new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JTicketsBagRestaurantMap.this);
                    m_PlaceClipboard.setPeople(false);
                    m_PlaceClipboard = null;
                    customer = null;
                    printState();
                } else {
                    // tenemos que copiar el ticket del clipboard
                    if (m_PlaceClipboard == m_place) {
                        // the same button. Canceling.
                        Place placeclip = m_PlaceClipboard;                       
                        m_PlaceClipboard = null;
                        customer = null;
                        printState();
                        setActivePlace(placeclip, ticketclip);
                    } else if (!m_place.hasPeople()) {
                        // Moving the receipt to an empty table
                        TicketInfo ticket = getTicketInfo(m_place);

                        if (ticket == null) {
                            try {
                                dlReceipts.insertSharedTicket(m_place.getId(), ticketclip);
                                m_place.setPeople(true);
                                dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());
                                m_PlaceClipboard.setPeople(false);
                            } catch (BasicException e) {
                                new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                            }

                            m_PlaceClipboard = null;
                            customer = null;
                            printState();

                            // No hace falta preguntar si estaba bloqueado porque ya lo estaba antes
                            // activamos el ticket seleccionado
                            setActivePlace(m_place, ticketclip);
                        } else {
                            // Full table
                            new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JTicketsBagRestaurantMap.this);
                            m_PlaceClipboard.setPeople(true);
                            printState();
                        }
                    } else {                          
                        // Merge the lines with the receipt of the table
                        TicketInfo ticket = getTicketInfo(m_place);

                        if (ticket == null) {
                            // The table is now empty
                            new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JTicketsBagRestaurantMap.this);
                            m_place.setPeople(false); // fixed
                        } else {
                            //asks if you want to merge tables
                            if (JOptionPane.showConfirmDialog(JTicketsBagRestaurantMap.this, AppLocal.getIntString("message.mergetablequestion"), AppLocal.getIntString("message.mergetable"), JOptionPane.YES_NO_OPTION)
                                    == JOptionPane.YES_OPTION){                                 
                                // merge lines ticket

                                try {
                                    dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());
                                    m_PlaceClipboard.setPeople(false);
                                    if (ticket.getCustomer() == null) {
                                    ticket.setCustomer(ticketclip.getCustomer());
                                    }
                                    for(TicketLineInfo line : ticketclip.getLines()) {
                                        ticket.addLine(line);
                                    }
                                    dlReceipts.updateSharedTicket(m_place.getId(), ticket);
                                } catch (BasicException e) {
                                    new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                                }

                                m_PlaceClipboard = null;
                                customer = null;
                                printState();

                                setActivePlace(m_place, ticket);
                            } else { 
                                // Cancel merge operations
                                Place placeclip = m_PlaceClipboard;                       
                                m_PlaceClipboard = null;
                                customer = null;
                                printState();
                                setActivePlace(placeclip, ticketclip);                                   
                            }
                        }                                
                    }
                }
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

        m_jPanelMap = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jbtnReservations = new javax.swing.JButton();
        m_jbtnRefresh = new javax.swing.JButton();
        m_jText = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        m_jPanelMap.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_jbtnReservations.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtnReservations.setText(AppLocal.getIntString("button.reservations")); // NOI18N
        m_jbtnReservations.setFocusPainted(false);
        m_jbtnReservations.setFocusable(false);
        m_jbtnReservations.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnReservations.setRequestFocusEnabled(false);
        m_jbtnReservations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnReservationsActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnReservations);

        m_jbtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        m_jbtnRefresh.setText(AppLocal.getIntString("button.reloadticket")); // NOI18N
        m_jbtnRefresh.setFocusPainted(false);
        m_jbtnRefresh.setFocusable(false);
        m_jbtnRefresh.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnRefresh.setRequestFocusEnabled(false);
        m_jbtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnRefreshActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnRefresh);
        jPanel2.add(m_jText);

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_START);

        m_jPanelMap.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(m_jPanelMap, "map");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnRefreshActionPerformed

        m_PlaceClipboard = null;
        customer = null;
        loadTickets();     
        printState();   
        
    }//GEN-LAST:event_m_jbtnRefreshActionPerformed

    private void m_jbtnReservationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnReservationsActionPerformed

        showView("res");
        m_jreservations.activate();
        
    }//GEN-LAST:event_m_jbtnReservationsActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel m_jPanelMap;
    private javax.swing.JLabel m_jText;
    private javax.swing.JButton m_jbtnRefresh;
    private javax.swing.JButton m_jbtnReservations;
    // End of variables declaration//GEN-END:variables
    
}
