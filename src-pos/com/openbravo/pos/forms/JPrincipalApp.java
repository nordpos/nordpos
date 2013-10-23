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

package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import com.openbravo.beans.RoundedBorder;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.util.Hashcypher;

//import com.l2fprod.common.swing.JTaskPane;
//import com.l2fprod.common.swing.JTaskPaneGroup;
import com.openbravo.pos.util.StringUtils;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author adrianromero
 */
public class JPrincipalApp extends javax.swing.JPanel implements AppUserView {

    private static Logger logger = Logger.getLogger("com.openbravo.pos.forms.JPrincipalApp");
    
    private JRootApp m_appview;
    private AppUser m_appuser;
    
    private DataLogicSystem m_dlSystem;
    
    private JLabel m_principalnotificator;
    
    private JPanelView m_jLastView;    
    private Action m_actionfirst;
    
    private Map<String, JPanelView> m_aPreparedViews; // Prepared views   
    private Map<String, JPanelView> m_aCreatedViews;
    
    private Icon menu_open;
    private Icon menu_close;
        
    /** Creates new form JPrincipalApp */
    public JPrincipalApp(JRootApp appview, AppUser appuser) {
        
        m_appview = appview; 
        m_appuser = appuser;
                   
        m_dlSystem = (DataLogicSystem) m_appview.getBean("com.openbravo.pos.forms.DataLogicSystem");
        
        // Cargamos los permisos del usuario
        m_appuser.fillPermissions(m_dlSystem);
               
        m_actionfirst = null;
        m_jLastView = null;
        m_aPreparedViews = new HashMap<String, JPanelView>();
        m_aCreatedViews = new HashMap<String, JPanelView>();
                
        initComponents();
        
        jPanel2.add(Box.createVerticalStrut(50), 0);        
        
        applyComponentOrientation(appview.getComponentOrientation());
        
        m_principalnotificator = new JLabel();
        m_principalnotificator.applyComponentOrientation(getComponentOrientation());
        m_principalnotificator.setText(m_appuser.getName());
        m_principalnotificator.setIcon(m_appuser.getIcon());
//        m_principalnotificator.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("TextField.shadow")), javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5)));        
        
        if (jButton1.getComponentOrientation().isLeftToRight()) {
            menu_open = new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/menu-right.png"));
            menu_close = new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/menu-left.png"));
        } else {
            menu_open = new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/menu-left.png"));
            menu_close = new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/menu-right.png"));
        }
        assignMenuButtonIcon();        
                
        // m_jPanelTitle.setUI(new GradientUI());
        m_jPanelTitle.setBorder(RoundedBorder.createGradientBorder());
        m_jPanelTitle.setVisible(false);
        
        // Anado el panel nulo
        m_jPanelContainer.add(new JPanel(), "<NULL>");
        showView("<NULL>");     
        
        try {

            m_jPanelLeft.setViewportView(getScriptMenu(m_dlSystem.getResourceAsText("Menu.Root")));
        } catch (ScriptException e) {
            logger.log(Level.SEVERE, "Cannot read Menu.Root resource. Trying defaut menu.", e);
            try {
                m_jPanelLeft.setViewportView(getScriptMenu(StringUtils.readResource("/com/openbravo/pos/templates/Menu.Root.txt")));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Cannot read default menu", ex);
            } catch (ScriptException es) {
                logger.log(Level.SEVERE, "Cannot read default menu", es);
            }
        }               
    }

    private Component getScriptMenu(String menutext) throws ScriptException {

        ScriptMenu menu = new ScriptMenu();

        ScriptEngine eng = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
        eng.put("menu", menu);
        eng.eval(menutext);
        return menu.getTaskPane();
    }
    
    private void assignMenuButtonIcon() {
        jButton1.setIcon(m_jPanelLeft.isVisible()
                ? menu_close
                : menu_open);
    }
    
    public class ScriptMenu {
//        private JTaskPane taskPane = new JTaskPane();
        private JXTaskPaneContainer taskPane;
        
        private ScriptMenu() {
            taskPane = new JXTaskPaneContainer();
            taskPane.applyComponentOrientation(getComponentOrientation());
        }
        
        public ScriptGroup addGroup(String key) {
            
            ScriptGroup group = new ScriptGroup(key);
            taskPane.add(group.getTaskGroup());
            return group;
        }
        
//        public JTaskPane getTaskPane() {            
        public JXTaskPaneContainer getTaskPane() {            
            return taskPane;
        }
    }
    
    public class ScriptGroup {
//        private JTaskPaneGroup taskGroup;
        private JXTaskPane taskGroup;
        
        private ScriptGroup(String key) {
//            taskGroup = new JTaskPaneGroup();
            taskGroup = new JXTaskPane();
            taskGroup.applyComponentOrientation(getComponentOrientation());
            taskGroup.setFocusable(false);
            taskGroup.setRequestFocusEnabled(false);
            taskGroup.setTitle(AppLocal.getIntString(key));     
            taskGroup.setVisible(false); // Only groups with sons are visible.
        }
        
        public void addPanel(String icon, String key, String classname) {            
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
        }        
        public void addExecution(String icon, String key, String classname) {
            addAction(new MenuExecAction(m_appview, icon, key, classname));
        }        
        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(key); 
            m_aPreparedViews.put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        }        
        public void addChangePasswordAction() {            
            addAction(new ChangePasswordAction("/com/openbravo/images/yast_security.png", "Menu.ChangePassword"));
        }       
        public void addExitAction() {            
            addAction(new ExitAction("/com/openbravo/images/gohome.png", "Menu.Exit"));
        }
        
        private void addAction(Action act) {
            
            if (m_appuser.hasPermission((String) act.getValue(AppUserView.ACTION_TASKNAME))) {
                // add the action
                Component c = taskGroup.add(act);
                c.applyComponentOrientation(getComponentOrientation());
                c.setFocusable(false);
                //c.setRequestFocusEnabled(false);   
                
                taskGroup.setVisible(true);

                if (m_actionfirst == null) {
                    m_actionfirst = act;
                }
            }
        }
        
//        public JTaskPaneGroup getTaskGroup() {
        public JXTaskPane getTaskGroup() {
            return taskGroup;
        }   
    }
    
    public class ScriptSubmenu {
        private MenuDefinition menudef;
        
        private ScriptSubmenu(String key) {
            menudef = new MenuDefinition(key);
        }
        
        public void addTitle(String key) {
            menudef.addMenuTitle(key);
        }
        
        public void addPanel(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
        }
        public void addExecution(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuExecAction(m_appview, icon, key, classname));
        }                
        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(key); 
            m_aPreparedViews.put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        } 
        public void addChangePasswordAction() {            
            menudef.addMenuItem(new ChangePasswordAction("/com/openbravo/images/yast_security.png", "Menu.ChangePassword"));
        }        
        public void addExitAction() {
            menudef.addMenuItem(new ExitAction("/com/openbravo/images/gohome.png", "Menu.Exit"));
        }
        public MenuDefinition getMenuDefinition() {
            return menudef;
        }
    }
    
    private void setMenuVisible(boolean value) {
        
        m_jPanelLeft.setVisible(value);
        assignMenuButtonIcon();
        revalidate();
    }
        
    public JComponent getNotificator() {
        return m_principalnotificator;
    }
    
    public void activate() {
        
        setMenuVisible(getBounds().width > 800);
        
        // arranco la primera opcion
        if (m_actionfirst != null) {
            m_actionfirst.actionPerformed(null);
            m_actionfirst = null;
        }
    }
    
    public boolean deactivate() {
        if (m_jLastView == null) {
            return true;
        } else if (m_jLastView.deactivate()) {
            m_jLastView = null;
            showView("<NULL>");       
            return true;
        } else {
            return false;
        }
        
    }
    
    private class ExitAction extends AbstractAction {
        
        public ExitAction(String icon, String keytext) {
            putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);
        }
        public void actionPerformed(ActionEvent evt) {
            m_appview.closeAppView();
        }
    }
    
    
    // La accion de cambio de password..
    private class ChangePasswordAction extends AbstractAction {
        public ChangePasswordAction(String icon, String keytext) {
            putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);

        }
        public void actionPerformed(ActionEvent evt) {
                       
            String sNewPassword = Hashcypher.changePassword(JPrincipalApp.this, m_appuser.getPassword());
            if (sNewPassword != null) {
                try {
                    
                    m_dlSystem.execChangePassword(new Object[] {sNewPassword, m_appuser.getId()});
                    m_appuser.setPassword(sNewPassword);
                } catch (BasicException e) {
                    JMessageDialog.showMessage(JPrincipalApp.this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotchangepassword")));             
                }
            }
        }
    }
    
    private void showView(String sView) {
        CardLayout cl = (CardLayout)(m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, sView);       
    }
    
    public AppUser getUser() {
        return m_appuser;
    }
    
    public void showTask(String sTaskClass) {
         
        m_appview.waitCursorBegin();       
         
        if (m_appuser.hasPermission(sTaskClass)) {            
            
            JPanelView m_jMyView = (JPanelView) m_aCreatedViews.get(sTaskClass);

            // cierro la antigua
            if (m_jLastView == null || (m_jMyView != m_jLastView && m_jLastView.deactivate())) {

                // Construct the new view
                if (m_jMyView == null) {   
                    
                    // Is the view prepared
                    m_jMyView = m_aPreparedViews.get(sTaskClass);
                    if (m_jMyView == null) {   
                        // The view is not prepared. Try to get as a Bean...
                        try {
                            m_jMyView = (JPanelView) m_appview.getBean(sTaskClass);
                        } catch (BeanFactoryException e) {
                            m_jMyView = new JPanelNull(m_appview, e);
                        }
                    }
                    
                    m_jMyView.getComponent().applyComponentOrientation(getComponentOrientation());
                    m_jPanelContainer.add(m_jMyView.getComponent(), sTaskClass);
                    m_aCreatedViews.put(sTaskClass, m_jMyView);
                }
                
                // ejecuto la tarea
                try {
                    m_jMyView.activate();
                } catch (BasicException e) {
                    JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notactive"), e));            
                }

                // se tiene que mostrar el panel                
                m_jLastView = m_jMyView;

                setMenuVisible(getBounds().width > 800);

                showView(sTaskClass);   
                // Y ahora que he cerrado la antigua me abro yo            
                String sTitle = m_jMyView.getTitle();
                m_jPanelTitle.setVisible(sTitle != null);
                m_jTitle.setText(sTitle);       
            }
        } else  {
            // No hay permisos para ejecutar la accion...
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notpermissions")));            
        }
        m_appview.waitCursorEnd();       
    }
    
    public void executeTask(String sTaskClass) {
        
        m_appview.waitCursorBegin();       

        if (m_appuser.hasPermission(sTaskClass)) {
            try {
                ProcessAction myProcess = (ProcessAction) m_appview.getBean(sTaskClass);

                // execute the proces
                try {
                    MessageInf m = myProcess.execute();    
                    if (m != null) {
                        // si devuelve un mensaje, lo muestro
                        JMessageDialog.showMessage(JPrincipalApp.this, m);
                    }
                } catch (BasicException eb) {
                    // Si se produce un error lo muestro.
                    JMessageDialog.showMessage(JPrincipalApp.this, new MessageInf(eb));            
                }
            } catch (BeanFactoryException e) {
                JMessageDialog.showMessage(JPrincipalApp.this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("Label.LoadError"), e));            
            }                    
        } else  {
            // No hay permisos para ejecutar la accion...
            JMessageDialog.showMessage(JPrincipalApp.this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notpermissions")));            
        }
        m_appview.waitCursorEnd();    
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        m_jPanelLeft = new javax.swing.JScrollPane();
        m_jPanelRight = new javax.swing.JPanel();
        m_jPanelTitle = new javax.swing.JPanel();
        m_jTitle = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setMargin(new java.awt.Insets(14, 2, 14, 2));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_END);
        jPanel1.add(m_jPanelLeft, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.LINE_START);

        m_jPanelRight.setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        m_jTitle.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        m_jTitle.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.darkGray), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        m_jPanelTitle.add(m_jTitle, java.awt.BorderLayout.NORTH);

        m_jPanelRight.add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setLayout(new java.awt.CardLayout());
        m_jPanelRight.add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        add(m_jPanelRight, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    setMenuVisible(!m_jPanelLeft.isVisible());
    
}//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JScrollPane m_jPanelLeft;
    private javax.swing.JPanel m_jPanelRight;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JLabel m_jTitle;
    // End of variables declaration//GEN-END:variables
    
}
