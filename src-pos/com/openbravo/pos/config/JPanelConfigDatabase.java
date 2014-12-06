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

package com.openbravo.pos.config;

import com.openbravo.data.user.DirtyManager;
import java.awt.Component;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.util.AltEncrypter;
import com.openbravo.pos.util.DirectoryEvent;
import java.io.File;
import javax.swing.JOptionPane;

/**
 *
 * @author adrianromero
 */
public class JPanelConfigDatabase extends javax.swing.JPanel implements PanelConfig {
    
    private DirtyManager dirty = new DirtyManager();
    
    String[] modelDBType = {
        "Derby",
        "Derby Client",
        "HSQLDB",
        "MySQL",
        "PostgreSQL",
        "FirebirdSQL"};
    
    private boolean bSelectDBType = false;
    
    /** Creates new form JPanelConfigDatabase */
    public JPanelConfigDatabase() {
        
        initComponents();
        
        jtxtDbDriverLib.getDocument().addDocumentListener(dirty);
        jtxtDbDriver.getDocument().addDocumentListener(dirty);
        jtxtDbURL.getDocument().addDocumentListener(dirty);
        jtxtDbPassword.getDocument().addDocumentListener(dirty);
        jtxtDbUser.getDocument().addDocumentListener(dirty);
         
        jbtnDbDriverLib.addActionListener(new DirectoryEvent(jtxtDbDriverLib));
        
        jcboDBType.addActionListener(dirty);
    }
    
    @Override
    public boolean hasChanged() {
        return dirty.isDirty();
    }
    
    @Override
    public Component getConfigComponent() {
        return this;
    }
    
    @Override
    public String getPanelConfigName() {
        return AppLocal.getIntString("Label.Database");
    }
   
    @Override
    public void loadProperties(AppConfig config) {
        
        jtxtDbDriverLib.setText(config.getProperty("db.driverlib"));
        jtxtDbDriver.setText(config.getProperty("db.driver"));
        jtxtDbURL.setText(config.getProperty("db.URL"));
        
        String sDBUser = config.getProperty("db.user");
        String sDBPassword = config.getProperty("db.password");        
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            // La clave esta encriptada.
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }        
        jtxtDbUser.setText(sDBUser);
        jtxtDbPassword.setText(sDBPassword);

        if (!bSelectDBType) {
            if (config.getProperty("db.type") == null) {
                readDBType(config.getProperty("db.type"));
            } else {
                jcboDBType.setSelectedItem(config.getProperty("db.type"));
            }
            bSelectDBType = true;
        }

        dirty.setDirty(false);
    }
   
    @Override
    public void saveProperties(AppConfig config) {
        
        config.setProperty("db.driverlib", jtxtDbDriverLib.getText());
        config.setProperty("db.driver", jtxtDbDriver.getText());
        config.setProperty("db.URL", jtxtDbURL.getText());
        config.setProperty("db.user", jtxtDbUser.getText());
        AltEncrypter cypher = new AltEncrypter("cypherkey" + jtxtDbUser.getText());       
        config.setProperty("db.password", "crypt:" + cypher.encrypt(new String(jtxtDbPassword.getPassword())));

        config.setProperty("db.type", jcboDBType.getSelectedItem() == null ? "" : jcboDBType.getSelectedItem().toString());
        
        dirty.setDirty(false);
    }
    
    private void readDBType(String sDB) {
        switch (sDB) {
            case "org.hsqldb.jdbcDriver":
                jcboDBType.setSelectedItem("HSQLDB");
                break;
            case "com.mysql.jdbc.Driver":
                jcboDBType.setSelectedItem("MySQL");
                break;
            case "org.postgresql.Driver":
                jcboDBType.setSelectedItem("PostgreSQL");
                break;
            case "org.firebirdsql.jdbc.FBDriver":
                jcboDBType.setSelectedItem("FirebirdSQL");
                break;
            case "org.apache.derby.jdbc.ClientDriver":
                jcboDBType.setSelectedItem("Derby Client");
                break;
            default:
                jcboDBType.setSelectedItem("Derby");
                break;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jtxtDbDriverLib = new javax.swing.JTextField();
        jbtnDbDriverLib = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtDbDriver = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtDbURL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtDbUser = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtDbPassword = new javax.swing.JPasswordField();
        jLabel19 = new javax.swing.JLabel();
        jcboDBType = new javax.swing.JComboBox();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));

        jLabel18.setText(AppLocal.getIntString("label.dbdriverlib")); // NOI18N

        jbtnDbDriverLib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/fileopen.png"))); // NOI18N

        jLabel1.setText(AppLocal.getIntString("Label.DbDriver")); // NOI18N

        jLabel2.setText(AppLocal.getIntString("Label.DbURL")); // NOI18N

        jLabel3.setText(AppLocal.getIntString("Label.DbUser")); // NOI18N

        jLabel4.setText(AppLocal.getIntString("Label.DbPassword")); // NOI18N

        jLabel19.setText(AppLocal.getIntString("label.dbtype")); // NOI18N

        jcboDBType.setModel(new javax.swing.DefaultComboBoxModel(modelDBType));
        jcboDBType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboDBTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtDbURL, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboDBType, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDbUser, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnDbDriverLib)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel19)
                    .addComponent(jcboDBType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18)
                    .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnDbDriverLib))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jtxtDbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jtxtDbURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(jtxtDbUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(jtxtDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void jcboDBTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboDBTypeActionPerformed
        if (bSelectDBType) {
            if (JOptionPane.showConfirmDialog(
                    this,
                    AppLocal.getIntString("message.configdatabase"),
                    AppLocal.getIntString("message.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                String dirname = System.getProperty("dirname.path");
                dirname = dirname == null ? "./" : dirname;
                
                String dbName = AppLocal.APP_ID.concat("-db");

                String sTypeJDBC = jcboDBType.getSelectedItem().toString();

                if (sTypeJDBC.equals("HSQLDB")) {
                    jtxtDbDriverLib.setText(new File(new File(dirname), "lib-jdbc/").getAbsolutePath());
                    jtxtDbDriver.setText("org.hsqldb.jdbcDriver");
                    jtxtDbURL.setText("jdbc:hsqldb:file:" + new File(new File(System.getProperty("user.home")), dbName).getAbsolutePath() + ";shutdown=true");
                    jtxtDbUser.setText("sa");
                    jtxtDbPassword.setText("");
                } else if (sTypeJDBC.equals("MySQL")) {
                    jtxtDbDriverLib.setText(new File(new File(dirname), "lib-jdbc/").getAbsolutePath());
                    jtxtDbDriver.setText("com.mysql.jdbc.Driver");
                    jtxtDbURL.setText("jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8");
                    jtxtDbUser.setText("");
                    jtxtDbPassword.setText("");
                } else if (sTypeJDBC.equals("PostgreSQL")) {
                    jtxtDbDriverLib.setText(new File(new File(dirname), "lib-jdbc/").getAbsolutePath());
                    jtxtDbDriver.setText("org.postgresql.Driver");
                    jtxtDbURL.setText("jdbc:postgresql://localhost:5432/" + dbName);
                    jtxtDbUser.setText("");
                    jtxtDbPassword.setText("");
                } else if (sTypeJDBC.equals("FirebirdSQL")) {
                    jtxtDbDriverLib.setText(new File(new File(dirname), "lib-jdbc/").getAbsolutePath());
                    jtxtDbDriver.setText("org.firebirdsql.jdbc.FBDriver");
                    jtxtDbURL.setText("jdbc:firebirdsql:localhost/3051:" + new File(new File(System.getProperty("user.home")), dbName + ".fdb").getAbsolutePath() + "?charSet=Cp1251");
                    jtxtDbUser.setText("");
                    jtxtDbPassword.setText("");
                } else if (sTypeJDBC.equals("Derby Client")) {
                    jtxtDbDriverLib.setText(new File(new File(dirname), "lib-jdbc/derbyclient.jar").getAbsolutePath());
                    jtxtDbDriver.setText("org.apache.derby.jdbc.ClientDriver");
                    jtxtDbURL.setText("jdbc:derby://localhost:1527/" + dbName + ";create=true");
                    jtxtDbUser.setText("APP");
                    jtxtDbPassword.setText("app");
                } else {
                    jtxtDbDriverLib.setText(new File(new File(dirname), "lib-jdbc/derby.jar").getAbsolutePath());
                    jtxtDbDriver.setText("org.apache.derby.jdbc.EmbeddedDriver");
                    jtxtDbURL.setText("jdbc:derby:" + new File(new File(System.getProperty("user.home")), dbName).getAbsolutePath() + ";create=true");
                    jtxtDbUser.setText("");
                    jtxtDbPassword.setText("");
                }
            } else {
                readDBType(jtxtDbDriver.getText());
            }
        }
    }//GEN-LAST:event_jcboDBTypeActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnDbDriverLib;
    private javax.swing.JComboBox jcboDBType;
    private javax.swing.JTextField jtxtDbDriver;
    private javax.swing.JTextField jtxtDbDriverLib;
    private javax.swing.JPasswordField jtxtDbPassword;
    private javax.swing.JTextField jtxtDbURL;
    private javax.swing.JTextField jtxtDbUser;
    // End of variables declaration//GEN-END:variables
    
}
