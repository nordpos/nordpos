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

package com.openbravo.data.gui;

import java.util.*;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.ComparatorCreator;
import com.openbravo.data.loader.LocalRes;
import com.openbravo.data.loader.Vectorer;
import com.openbravo.data.user.BrowseListener;
import com.openbravo.data.user.BrowsableEditableData;
import com.openbravo.data.user.StateListener;

public class JNavigator extends javax.swing.JPanel implements BrowseListener, StateListener {
    
    public final static int BUTTONS_ALL = 0;
    public final static int BUTTONS_NONAVIGATE = 1;
    
    protected BrowsableEditableData m_bd;
    protected ComparatorCreator m_cc;
    
    protected FindInfo m_LastFindInfo;  

    private javax.swing.JButton jbtnFind = null;
    private javax.swing.JButton jbtnSort = null;
    private javax.swing.JButton jbtnFirst = null;
    private javax.swing.JButton jbtnLast = null;
    private javax.swing.JButton jbtnNext = null;
    private javax.swing.JButton jbtnPrev = null;
    private javax.swing.JButton jbtnRefresh = null;
    private javax.swing.JButton jbtnReload = null;    
    
    /** Creates new form JNavigator */
    public JNavigator(BrowsableEditableData bd, Vectorer vec, ComparatorCreator cc, int iButtons) {

        initComponents();
        
        if (iButtons == BUTTONS_ALL) {
            jbtnFirst = new javax.swing.JButton();
            jbtnFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/2leftarrow.png")));
            jbtnFirst.setMargin(new java.awt.Insets(2, 8, 2, 8));            
            jbtnFirst.setFocusPainted(false);
            jbtnFirst.setFocusable(false);
            jbtnFirst.setRequestFocusEnabled(false);
            jbtnFirst.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jbtnFirstActionPerformed(evt);
                }
            });
            add(jbtnFirst);
        }

        if (iButtons == BUTTONS_ALL) {
            jbtnPrev = new javax.swing.JButton();
            jbtnPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1leftarrow.png")));
            jbtnPrev.setMargin(new java.awt.Insets(2, 8, 2, 8));
            jbtnPrev.setFocusPainted(false);
            jbtnPrev.setFocusable(false);
            jbtnPrev.setRequestFocusEnabled(false);
            jbtnPrev.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jbtnPrevActionPerformed(evt);
                }
            });
            add(jbtnPrev);
        }

        jbtnRefresh = new javax.swing.JButton();
        jbtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1downarrow.png")));
        jbtnRefresh.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jbtnRefresh.setFocusPainted(false);
        jbtnRefresh.setFocusable(false);
        jbtnRefresh.setRequestFocusEnabled(false);
        jbtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRefreshActionPerformed(evt);
            }
        });
        add(jbtnRefresh);

        if (iButtons == BUTTONS_ALL) {
            jbtnNext = new javax.swing.JButton();
            jbtnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1rightarrow.png")));
            jbtnNext.setMargin(new java.awt.Insets(2, 8, 2, 8));
            jbtnNext.setFocusPainted(false);
            jbtnNext.setFocusable(false);
            jbtnNext.setRequestFocusEnabled(false);
            jbtnNext.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jbtnNextActionPerformed(evt);
                }
            });
            add(jbtnNext);
        }

        if (iButtons == BUTTONS_ALL) {
            jbtnLast = new javax.swing.JButton();
            jbtnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/2rightarrow.png")));
            jbtnLast.setMargin(new java.awt.Insets(2, 8, 2, 8));
            jbtnLast.setFocusPainted(false);
            jbtnLast.setFocusable(false);
            jbtnLast.setRequestFocusEnabled(false);
            jbtnLast.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jbtnLastActionPerformed(evt);
                }
            });
            add(jbtnLast);
        }

        add(new javax.swing.JSeparator());

        if (bd.canLoadData()) {
            jbtnReload = new javax.swing.JButton();
            jbtnReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png")));
            jbtnReload.setMargin(new java.awt.Insets(2, 8, 2, 8));
            jbtnReload.setFocusPainted(false);
            jbtnReload.setFocusable(false);
            jbtnReload.setRequestFocusEnabled(false);
            jbtnReload.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jbtnReloadActionPerformed(evt);
                }
            });
            add(jbtnReload);

            add(new javax.swing.JSeparator());
        }
        
        if (vec == null) {
            m_LastFindInfo = null;
        } else {
            m_LastFindInfo = new FindInfo(vec);
            jbtnFind = new javax.swing.JButton();
            jbtnFind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/search.png")));
            jbtnFind.setMargin(new java.awt.Insets(2, 8, 2, 8));
            jbtnFind.setFocusPainted(false);
            jbtnFind.setFocusable(false);
            jbtnFind.setRequestFocusEnabled(false);
            jbtnFind.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jbtnFindActionPerformed(evt);
                }
            });
            add(jbtnFind);  
        }
        
        m_cc = cc;
        if (m_cc != null) {
            jbtnSort = new javax.swing.JButton();
            jbtnSort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/sort_incr.png")));
            jbtnSort.setMargin(new java.awt.Insets(2, 8, 2, 8));
            jbtnSort.setFocusPainted(false);
            jbtnSort.setFocusable(false);
            jbtnSort.setRequestFocusEnabled(false);
            jbtnSort.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jbtnSortActionPerformed(evt);
                }
            });
            add(jbtnSort);
        }       
        
        m_bd = bd;
        bd.addBrowseListener(this);
        bd.addStateListener(this);
    }   
    
    public JNavigator(BrowsableEditableData bd) {
        this(bd, null, null, BUTTONS_ALL);
    }
    public JNavigator(BrowsableEditableData bd, Vectorer vec, ComparatorCreator cc) {
        this(bd, vec, cc, BUTTONS_ALL);
    }

    public void updateState(int iState) {
        if (iState == BrowsableEditableData.ST_INSERT || iState == BrowsableEditableData.ST_DELETE) {
             // Insert o Delete
            if (jbtnFirst != null) jbtnFirst.setEnabled(false);
            if (jbtnPrev != null) jbtnPrev.setEnabled(false);
            if (jbtnNext != null) jbtnNext.setEnabled(false);
            if (jbtnLast != null) jbtnLast.setEnabled(false);
            if (jbtnRefresh != null) jbtnRefresh.setEnabled(true);
        }
    }  
    
    public void updateIndex(int iIndex, int iCounter) {
        
        if (iIndex >= 0 && iIndex < iCounter) {
            // Reposicionamiento
            if (jbtnFirst != null) jbtnFirst.setEnabled(iIndex > 0);
            if (jbtnPrev != null) jbtnPrev.setEnabled(iIndex > 0);
            if (jbtnNext != null) jbtnNext.setEnabled(iIndex < iCounter - 1);
            if (jbtnLast != null) jbtnLast.setEnabled(iIndex < iCounter - 1);
            if (jbtnRefresh != null) jbtnRefresh.setEnabled(true);
        } else {
            // EOF
            if (jbtnFirst != null) jbtnFirst.setEnabled(false);
            if (jbtnPrev != null) jbtnPrev.setEnabled(false);
            if (jbtnNext != null) jbtnNext.setEnabled(false);
            if (jbtnLast != null) jbtnLast.setEnabled(false);
            if (jbtnRefresh != null) jbtnRefresh.setEnabled(false);
        }
    }   
    
    private void jbtnSortActionPerformed(java.awt.event.ActionEvent evt) {                                         
        try {
            Comparator c = JSort.showMessage(this, m_cc);
            if (c != null) {
                m_bd.sort(c);
            }
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.nolistdata"), eD);
            msg.show(this);
        }  
    }
    
    private void jbtnFindActionPerformed(java.awt.event.ActionEvent evt) {                                         
        
        try {
            FindInfo newFindInfo = JFind.showMessage(this, m_LastFindInfo);
            if (newFindInfo != null) {
                m_LastFindInfo = newFindInfo;
                
                int index = m_bd.findNext(newFindInfo);
                if (index < 0) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.norecord"));
                    msg.show(this);
                } else {
                    m_bd.moveTo(index);
                }
            }
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.nolistdata"), eD);
            msg.show(this);
        }           
    }                                        

    private void jbtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {                                            
       
        m_bd.actionReloadCurrent(this);       
    }                                           

    private void jbtnReloadActionPerformed(java.awt.event.ActionEvent evt) {                                           

        try {
            m_bd.actionLoad();
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.noreload"), eD);
            msg.show(this);
        }
    }                                          

    private void jbtnLastActionPerformed(java.awt.event.ActionEvent evt) {                                         

        try {
            m_bd.moveLast();
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.nomove"), eD);
            msg.show(this);
        }
    }                                        

    private void jbtnFirstActionPerformed(java.awt.event.ActionEvent evt) {                                          

        try{
            m_bd.moveFirst();
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.nomove"), eD);
            msg.show(this);
        }
    }                                         

    private void jbtnPrevActionPerformed(java.awt.event.ActionEvent evt) {                                         
        try {
            m_bd.movePrev();
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.nomove"), eD);
            msg.show(this);
        }       
    }                                        

    private void jbtnNextActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            m_bd.moveNext();
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.nomove"), eD);
            msg.show(this);
        }     
    }                                        
       
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

    }// </editor-fold>//GEN-END:initComponents
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
