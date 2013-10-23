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

package com.openbravo.pos.sql;

import java.awt.CardLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import com.openbravo.data.loader.BaseSentence;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataField;
import com.openbravo.data.loader.DataResultSet;
import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.panels.*;

public class JPanelSQLResult extends javax.swing.JPanel {
     
    private final static Icon ICO_NULL = new javax.swing.ImageIcon(JPanelView.class.getResource("/com/openbravo/images/null.png"));
    private final static Icon ICO_TRUE = new javax.swing.ImageIcon(JPanelView.class.getResource("/com/openbravo/images/true.png"));
    private final static Icon ICO_FALSE = new javax.swing.ImageIcon(JPanelView.class.getResource("/com/openbravo/images/false.png"));
    private final static Icon ICO_BYTEA = new javax.swing.ImageIcon(JPanelView.class.getResource("/com/openbravo/images/bytea.png"));
   
    /** Creates new form JPanelSQLResult */
    public JPanelSQLResult() {
        initComponents();
        
        m_jTableResult.setDefaultRenderer(java.lang.String.class, new StringRenderer());       
        m_jTableResult.setDefaultRenderer(java.lang.Double.class, new NumberRenderer());       
        m_jTableResult.setDefaultRenderer(java.lang.Integer.class, new NumberRenderer());       
        m_jTableResult.setDefaultRenderer(java.util.Date.class, new DateRenderer());       
        m_jTableResult.setDefaultRenderer(java.lang.Boolean.class, new BooleanRenderer());       
        m_jTableResult.setDefaultRenderer(byte[].class, new ByteaRenderer());           
    }
    
    public boolean executeSentence(BaseSentence sent) {
        return executeSentence(sent, null);
    }
    
    public boolean executeSentence(BaseSentence sent, Object params) {
        
        CardLayout cl = (CardLayout)(getLayout());
        
        try {
            DataResultSet rs = sent.openExec(params);          
            
            if (rs.updateCount() < 0) {
                cl.show(this, "resultset"); 
                
                DataField [] df = rs.getDataField();  

                SQLTableModel sqlresult = new SQLTableModel(df); 

                while (rs.next()) {
                    sqlresult.addRow(rs);
                }            
                rs.close();

                sent.closeExec();

                m_jTableResult.setModel(sqlresult);
            } else {
                cl.show(this, "updatecount"); 
                m_txtResulltText.setText("Update count: " + Integer.toString(rs.updateCount()));
                m_txtResulltText.setCaretPosition(0);
           }
            return true;
            
        } catch (BasicException e) {
            cl.show(this, "updatecount"); 
            StringWriter w =  new StringWriter();                    
            e.printStackTrace(new PrintWriter(w));                   
            m_txtResulltText.setText(w.toString());
            m_txtResulltText.setCaretPosition(0);
            // e.printStackTrace();
            return false;
        }
    }
    
    static class StringRenderer extends DefaultTableCellRenderer.UIResource {
        public StringRenderer() { super(); }
        public void setValue(Object value) {
            if (value == null) {
                setIcon(ICO_NULL);
                setText(null);
            } else {
                setIcon(null);
                setText(value.toString());
            }
        }
    }

    static class NumberRenderer extends DefaultTableCellRenderer.UIResource {
        NumberFormat formatter = null;
        public NumberRenderer() { 
            super(); 
            setHorizontalAlignment(JLabel.RIGHT);
        }
        public void setValue(Object value) {
            if (formatter == null) {
                formatter = NumberFormat.getInstance();
            }
            if (value == null) {
                setIcon(ICO_NULL);
                setText(null);
            } else {
                setIcon(null);
                setText(formatter.format(value));
            }
        }
    }

    static class DateRenderer extends DefaultTableCellRenderer.UIResource {
        DateFormat formatter;
        public DateRenderer() { super(); }

        public void setValue(Object value) {
            if (formatter==null) {
                formatter = DateFormat.getDateInstance();
            }
            if (value == null) {
                setIcon(ICO_NULL);
                setText(null);
            } else {
                setIcon(null);
                setText(formatter.format(value));
            }
        }
    }

    static class IconRenderer extends DefaultTableCellRenderer.UIResource {
        public IconRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
        }
        public void setValue(Object value) {
            setIcon((value instanceof Icon) ? (Icon)value : null);
        }
    }

    static class ByteaRenderer  extends DefaultTableCellRenderer.UIResource {
        public ByteaRenderer() {
            super();
        }
        public void setValue(Object value) {
            if (value == null) {
                setIcon(ICO_NULL);
            } else {
                setIcon(ICO_BYTEA);
            }
        }
    }
    
    static class BooleanRenderer  extends DefaultTableCellRenderer.UIResource {
        public BooleanRenderer() {
            super();
        }
        public void setValue(Object value) {
            if (value == null) {
                setIcon(ICO_NULL);
            } else {
                setIcon(((Boolean) value).booleanValue() ? ICO_TRUE : ICO_FALSE);
            }
        }
    }    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jTableResult = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        m_txtResulltText = new javax.swing.JTextArea();

        setLayout(new java.awt.CardLayout());

        add(jPanel3, "card4");

        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jButton1.setText("jButton1");
        jToolBar1.add(jButton1);

        jButton2.setText("jButton2");
        jToolBar1.add(jButton2);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.add(jSeparator1);

        jButton3.setText("jButton3");
        jToolBar1.add(jButton3);

        jButton4.setText("jButton4");
        jToolBar1.add(jButton4);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(m_jTableResult);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, "resultset");

        jPanel2.setLayout(new java.awt.BorderLayout());

        m_txtResulltText.setEditable(false);
        jScrollPane2.setViewportView(m_txtResulltText);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        add(jPanel2, "updatecount");

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton jButton1;
    javax.swing.JButton jButton2;
    javax.swing.JButton jButton3;
    javax.swing.JButton jButton4;
    javax.swing.JPanel jPanel1;
    javax.swing.JPanel jPanel2;
    javax.swing.JPanel jPanel3;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JScrollPane jScrollPane2;
    javax.swing.JSeparator jSeparator1;
    javax.swing.JToolBar jToolBar1;
    javax.swing.JTable m_jTableResult;
    javax.swing.JTextArea m_txtResulltText;
    // End of variables declaration//GEN-END:variables
    
}
