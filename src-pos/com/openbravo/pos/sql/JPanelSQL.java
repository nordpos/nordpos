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
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.openbravo.data.loader.BaseSentence;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.BatchSentenceScript;
import com.openbravo.data.loader.DataResultSet;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.MetaSentence;
import com.openbravo.data.loader.SerializerReadBasic;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.panels.*;


public class JPanelSQL  extends JPanel implements JPanelView {

    private ArrayList m_aHistory;
    private int m_iHistoryIndex;
    
    private int m_iResultIndex;
    
    private AppView m_App;
    
    /** Creates new form JPanelSQL */
    public JPanelSQL(AppView oApp) {
        m_App = oApp;        
        
        m_aHistory = new ArrayList();
        m_aHistory.add("");
        m_iHistoryIndex = 0;
       
        initComponents();
        
        m_iResultIndex = 0;
        addResultTab();
        
        
        DefaultMutableTreeNode rootnode =  new DefaultMutableTreeNode(AppLocal.APP_NAME);
        
        
        SQLDatabase db = new SQLDatabase(AppLocal.APP_NAME + " - Database.");
        
        try {
            BaseSentence sent = new MetaSentence(m_App.getSession(), "getTables"
                    , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING})
                    , new SerializerReadBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING}));
            DataResultSet rs = sent.openExec( new Object[] {null, null});          
            while (rs.next()) {
                Object[] aTable = (Object[]) rs.getCurrent();
                db.addTable((String) aTable[2]);
            }            
            rs.close();
            sent.closeExec();
            
            sent = new MetaSentence(m_App.getSession(), "getColumns"
                    , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING})
                    , new SerializerReadBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING}));
            rs = sent.openExec(new Object[] {null, null, null}); 
            while (rs.next()) {
                Object[] aColumn = (Object[]) rs.getCurrent();
                SQLTable t = db.getTable((String) aColumn[2]);
                if (t != null) {
                    t.addColumn((String) aColumn[3]);
                }
            }            
            rs.close();
            sent.closeExec();
            
        } catch (BasicException e) {
            // e.printStackTrace();
        }        
       
    
        DefaultTreeModel tm = new DefaultTreeModel(db);
        
        m_jTreeDB.setModel(tm);
        
        statusButtons();
    }
    
    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return null;
    } 
    
    public void activate() throws BasicException {      
    }
    
    public boolean deactivate() {
        return true;
    }    
    
    private void addResultTab() {
        m_iResultIndex++;
        m_TabbedPane.addTab("Resultset " + Integer.toString(m_iResultIndex), new JPanelSQLResult());
        m_TabbedPane.setSelectedIndex(m_TabbedPane.getTabCount() - 1);
        m_jtabdelete.setEnabled(m_TabbedPane.getTabCount() > 1);
    }
    
    private void statusButtons() {
        m_jForward.setEnabled(m_iHistoryIndex < m_aHistory.size() - 1);
        m_jBack.setEnabled(m_iHistoryIndex > 0);                
    }
    
    private boolean executeSentence(BaseSentence sent) {
        return executeSentence(sent, null);
    }
    private boolean executeSentence(BaseSentence sent, Object params) {
        JPanelSQLResult panresult = (JPanelSQLResult) m_TabbedPane.getSelectedComponent();
        return panresult.executeSentence(sent, params);
    } 
    
    private void insertProductCategories() {
        try {        
            for( int i = 100; i < 50000; i++) {
                String sentence =
                        "INSERT INTO m_product_category(" +
                        " m_product_category_id," +
                        " ad_client_id," +
                        " ad_org_id," +
                        " isactive, " +
                        " created, createdby, " +
                        " updated, updatedby, " +
                        " value, " +
                        " name, " +
                        " description, " +
                        " isdefault, plannedmargin, a_asset_group_id) VALUES " +
                        "(" +
                        Integer.toString(i + 1000000) + " ," +
                        " 1000000, " + 
                        " 0, " +
                        "'Y', " +
                        " '2007-01-01', 100, " +
                        " '2007-01-01', 100, " +
                        " 'Fictious Category no " + Integer.toString(i) +"' ," +
                        " 'Fictious Category Name no " + Integer.toString(i) +"' ," +
                        " null," +
                        " 'N'," +
                        " 0," +
                        " null)";
                System.out.println(i);
                System.out.println(sentence);
                BaseSentence sent = new StaticSentence(m_App.getSession(), sentence);
                sent.exec();
            }
        } catch (BasicException e) {
            e.printStackTrace();
        }        
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jtxtSQL = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        m_jTreeDB = new javax.swing.JTree();
        m_jForward = new javax.swing.JButton();
        m_jBack = new javax.swing.JButton();
        m_TabbedPane = new javax.swing.JTabbedPane();
        jButton2 = new javax.swing.JButton();
        m_jtabdelete = new javax.swing.JButton();
        m_jbegintrans = new javax.swing.JButton();
        m_jcommit = new javax.swing.JButton();
        m_jrollback = new javax.swing.JButton();

        setLayout(null);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/launch.png")));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        add(jButton1);
        jButton1.setBounds(460, 10, 60, 80);

        jScrollPane1.setViewportView(m_jtxtSQL);

        add(jScrollPane1);
        jScrollPane1.setBounds(10, 40, 440, 50);

        m_jTreeDB.setRootVisible(false);
        m_jTreeDB.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                m_jTreeDBTreeExpanded(evt);
            }
        });

        jScrollPane4.setViewportView(m_jTreeDB);

        add(jScrollPane4);
        jScrollPane4.setBounds(530, 10, 150, 320);

        m_jForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1rightarrow.png")));
        m_jForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jForwardActionPerformed(evt);
            }
        });

        add(m_jForward);
        m_jForward.setBounds(70, 10, 49, 25);

        m_jBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1leftarrow.png")));
        m_jBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBackActionPerformed(evt);
            }
        });

        add(m_jBack);
        m_jBack.setBounds(10, 10, 50, 25);

        add(m_TabbedPane);
        m_TabbedPane.setBounds(10, 130, 510, 200);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editnew.png")));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        add(jButton2);
        jButton2.setBounds(10, 100, 49, 25);

        m_jtabdelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editdelete.png")));
        m_jtabdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jtabdeleteActionPerformed(evt);
            }
        });

        add(m_jtabdelete);
        m_jtabdelete.setBounds(70, 100, 49, 25);

        m_jbegintrans.setText("trans");
        m_jbegintrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbegintransActionPerformed(evt);
            }
        });

        add(m_jbegintrans);
        m_jbegintrans.setBounds(210, 10, 57, 23);

        m_jcommit.setText("commit");
        m_jcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jcommitActionPerformed(evt);
            }
        });

        add(m_jcommit);
        m_jcommit.setBounds(300, 10, 65, 23);

        m_jrollback.setText("rollback");
        m_jrollback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jrollbackActionPerformed(evt);
            }
        });

        add(m_jrollback);
        m_jrollback.setBounds(370, 10, 69, 23);

    }// </editor-fold>//GEN-END:initComponents

    private void m_jbegintransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbegintransActionPerformed
        try {
            m_App.getSession().begin();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_m_jbegintransActionPerformed

    private void m_jrollbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jrollbackActionPerformed
        
        try {
            m_App.getSession().rollback();
        } catch (SQLException e) {
            System.out.println(e);
        }        
        
    }//GEN-LAST:event_m_jrollbackActionPerformed

    private void m_jcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jcommitActionPerformed

        try {
            m_App.getSession().commit();
        } catch (SQLException e) {
            System.out.println(e);
        }        
        
    }//GEN-LAST:event_m_jcommitActionPerformed

    private void m_jtabdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jtabdeleteActionPerformed

        m_TabbedPane.removeTabAt(m_TabbedPane.getSelectedIndex());
        m_jtabdelete.setEnabled(m_TabbedPane.getTabCount() > 1);

    }//GEN-LAST:event_m_jtabdeleteActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        addResultTab();
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void m_jForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jForwardActionPerformed
// TODO add your handling code here:
        
        m_iHistoryIndex ++;
        m_jtxtSQL.setText((String)m_aHistory.get(m_iHistoryIndex));
        statusButtons();
        
    }//GEN-LAST:event_m_jForwardActionPerformed

    private void m_jBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBackActionPerformed

        m_iHistoryIndex --;
        m_jtxtSQL.setText((String)m_aHistory.get(m_iHistoryIndex));
        statusButtons();
        
    }//GEN-LAST:event_m_jBackActionPerformed

    private void m_jTreeDBTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_m_jTreeDBTreeExpanded
// TODO add your handling code here:
        
        
    }//GEN-LAST:event_m_jTreeDBTreeExpanded

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // BaseSentence sent = new MetaSentence(m_App.getConnection(), "getTables", new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING}), null);
        // BaseSentence sent = new MetaSentence(m_App.getConnection(), "getColumns", new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING}), null);
        // executeSentence(sent, new Object[] {null, null, null});

        BaseSentence sent = new BatchSentenceScript(m_App.getSession(), m_jtxtSQL.getText());
        if (executeSentence(sent)) {
            // guardamos la historia.
            if (!m_aHistory.get(m_iHistoryIndex).equals(m_jtxtSQL.getText())) {
                m_iHistoryIndex ++;
                m_aHistory.subList(m_iHistoryIndex, m_aHistory.size()).clear();
                m_aHistory.add(m_jtxtSQL.getText());
                statusButtons();
            }
        }

    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane m_TabbedPane;
    private javax.swing.JButton m_jBack;
    private javax.swing.JButton m_jForward;
    private javax.swing.JTree m_jTreeDB;
    private javax.swing.JButton m_jbegintrans;
    private javax.swing.JButton m_jcommit;
    private javax.swing.JButton m_jrollback;
    private javax.swing.JButton m_jtabdelete;
    private javax.swing.JTextArea m_jtxtSQL;
    // End of variables declaration//GEN-END:variables
    
}
