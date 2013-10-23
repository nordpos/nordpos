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

package com.openbravo.beans;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author  Adrian
 */
public class JCalendarDialog extends javax.swing.JDialog {
    
    // private static ResourceBundle m_Intl;
    private static LocaleResources m_resources;
    
    private Date m_date;
    private JCalendarPanel myCalendar = null;
    private JTimePanel myTime = null;
    
    /** Creates new form JCalendarDialog */
    public JCalendarDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        if (m_resources == null) {
            m_resources = new LocaleResources();
            m_resources.addBundleName("beans_messages");
        }
    }
    /** Creates new form JCalendarDialog */
    public JCalendarDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        
        if (m_resources == null) {
            m_resources = new LocaleResources();
            m_resources.addBundleName("beans_messages");
        }
    }    
    
    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window) parent;
        } else {
            return getWindow(parent.getParent());
        }
    }    
    
    public static Date showCalendarTimeHours(Component parent, Date date) {
        return internalCalendarTime(parent, date == null ? DateUtils.getToday() : date, true);
    }
    
    public static Date showCalendarTime(Component parent, Date date) {
        return internalCalendarTime(parent, date == null ? DateUtils.getTodayMinutes() : date, true);
    }
    
    public static Date showCalendar(Component parent, Date date) {
        return internalCalendarTime(parent, date == null ? DateUtils.getTodayMinutes() : date, false);
    }
    
    private static Date internalCalendarTime(Component parent, Date date, boolean bTimePanel) {
        
        Window window = getWindow(parent);      
        
        JCalendarDialog myMsg;
        if (window instanceof Frame) { 
            myMsg = new JCalendarDialog((Frame) window, true);
        } else {
            myMsg = new JCalendarDialog((Dialog) window, true);
        }
        
        myMsg.initComponents();
        
        Date d = date;
        int dialogwidth = 400;
        
        myMsg.myCalendar = new JCalendarPanel(d);     
        myMsg.myCalendar.addPropertyChangeListener("Date", new JPanelCalendarChange(myMsg));
        myMsg.jPanelGrid.add(myMsg.myCalendar);
        
        if (bTimePanel) {
            myMsg.myTime = new JTimePanel(d);
            myMsg.myTime.addPropertyChangeListener("Date", new JPanelTimeChange(myMsg)); 
            myMsg.jPanelGrid.add(myMsg.myTime);
            dialogwidth += 400;
        }
        
        myMsg.getRootPane().setDefaultButton(myMsg.jcmdOK);        
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        myMsg.setBounds((screenSize.width - dialogwidth) / 2, (screenSize.height - 359) / 2, dialogwidth, 359);
        
        //myMsg.show();
        myMsg.m_date = null;
        myMsg.setVisible(true);
        return myMsg.m_date;
    }
      
    private static class JPanelTimeChange implements PropertyChangeListener {
        private JCalendarDialog m_me;
        public JPanelTimeChange(JCalendarDialog me) {
            m_me = me;
        }
        public void propertyChange(PropertyChangeEvent evt) {
            m_me.myCalendar.setDate(m_me.myTime.getDate());
        }        
    }
      
    private static class JPanelCalendarChange implements PropertyChangeListener {
        private JCalendarDialog m_me;
        public JPanelCalendarChange(JCalendarDialog me) {
            m_me = me;
        }
        public void propertyChange(PropertyChangeEvent evt) {
            m_me.myTime.setDate(m_me.myCalendar.getDate());
        }        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jcmdOK = new javax.swing.JButton();
        jcmdCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanelGrid = new javax.swing.JPanel();

        setTitle(m_resources.getString("title.calendar"));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeWindow(evt);
            }
        });

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jcmdOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/button_ok.png")));
        jcmdOK.setText(m_resources.getString("button.ok"));
        jcmdOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        jcmdOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdOKActionPerformed(evt);
            }
        });

        jPanel1.add(jcmdOK);

        jcmdCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/button_cancel.png")));
        jcmdCancel.setText(m_resources.getString("button.cancel"));
        jcmdCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        jcmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdCancelActionPerformed(evt);
            }
        });

        jPanel1.add(jcmdCancel);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanelGrid.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        jPanel2.add(jPanelGrid, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

    private void jcmdOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdOKActionPerformed

        GregorianCalendar dateresult;
        
        GregorianCalendar date1 = new GregorianCalendar();
        date1.setTime(myCalendar.getDate());
        
        if (myTime == null) {
            dateresult = new GregorianCalendar(
                    date1.get(GregorianCalendar.YEAR),
                    date1.get(GregorianCalendar.MONTH),
                    date1.get(GregorianCalendar.DAY_OF_MONTH));

        } else {
            GregorianCalendar date2 = new GregorianCalendar();
            date2.setTime(myTime.getDate());
            dateresult = new GregorianCalendar(
                    date1.get(GregorianCalendar.YEAR),
                    date1.get(GregorianCalendar.MONTH),
                    date1.get(GregorianCalendar.DAY_OF_MONTH),
                    date2.get(GregorianCalendar.HOUR_OF_DAY),
                    date2.get(GregorianCalendar.MINUTE));
        }
        
        m_date = dateresult.getTime();
                
        setVisible(false);
        dispose();        
    }//GEN-LAST:event_jcmdOKActionPerformed

    private void jcmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdCancelActionPerformed

        setVisible(false);
        dispose();        
    }//GEN-LAST:event_jcmdCancelActionPerformed

    private void closeWindow(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeWindow

        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeWindow
       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelGrid;
    private javax.swing.JButton jcmdCancel;
    private javax.swing.JButton jcmdOK;
    // End of variables declaration//GEN-END:variables
    
}
