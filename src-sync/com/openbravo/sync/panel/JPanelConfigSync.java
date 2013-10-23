/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.sync.panel;

import com.openbravo.basic.BasicException;
import com.openbravo.beans.LocaleResources;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JPanelView;
import java.awt.CardLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author svininykh-av
 */
public class JPanelConfigSync extends JPanel implements JPanelView, BeanFactoryApp {

    private DirtyManager dirty = new DirtyManager();
    private DataLogicSystem dlSystem;
    private AppProperties appProp;
    private Properties hostProp;

    String[] modelSyncType = {"openbravoerp",
                              "generaterows",
                              "csv",
                              "none"};

    public JPanelConfigSync() {
        initComponents();

        jTxtUrl.getDocument().addDocumentListener(dirty);
        jTxtClient.getDocument().addDocumentListener(dirty);
        jTxtOrg.getDocument().addDocumentListener(dirty);
        jTxtPOS.getDocument().addDocumentListener(dirty);
        jTxtUser.getDocument().addDocumentListener(dirty);
        jTxtPassword.getDocument().addDocumentListener(dirty);

        jcboSyncType.addActionListener(dirty);

        jTxtImportDir.getDocument().addDocumentListener(dirty);
        jTxtExportDir.getDocument().addDocumentListener(dirty);

    }

    @Override
    public void init(AppView app) throws BeanFactoryException {
        dlSystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        appProp = app.getProperties();
        hostProp = dlSystem.getResourceAsProperties(appProp.getHost() + "/properties");

    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.ConfigureSync");
    }

    @Override
    public void activate() throws BasicException {
        jTxtUrl.setText(hostProp.getProperty("erp.url"));
        jTxtClient.setText(hostProp.getProperty("erp.id"));
        jTxtOrg.setText(hostProp.getProperty("erp.org"));
        jTxtPOS.setText(hostProp.getProperty("erp.pos"));
        jTxtUser.setText(hostProp.getProperty("erp.user"));
        jTxtPassword.setText(hostProp.getProperty("erp.password"));

        String sImpDir = hostProp.getProperty("sync.importdir");
        if (sImpDir == null) {
            jTxtImportDir.setText(System.getProperty("user.home"));
        } else {
            jTxtImportDir.setText(sImpDir);
        }

        String sExpDir = hostProp.getProperty("sync.exportdir");
        if (sExpDir == null) {
            jTxtExportDir.setText(System.getProperty("user.home"));
        } else {
            jTxtExportDir.setText(sExpDir);
        }

        String sType = hostProp.getProperty("sync.type");
        if (sType.isEmpty()) {
            jcboSyncType.setSelectedItem("none");
        } else {
            jcboSyncType.setSelectedItem(sType);
        }

        dirty.setDirty(false);
    }

    @Override
    public boolean deactivate() {
        if (dirty.isDirty()) {
            int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.syncwannasave"), AppLocal.getIntString("title.sync"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                saveProperties();
                return true;
            } else {
                return res == JOptionPane.NO_OPTION;
            }
        } else {
            return true;
        }
    }

    private void saveProperties() {
        hostProp.setProperty("erp.url", jTxtUrl.getText());
        hostProp.setProperty("erp.id", jTxtClient.getText());
        hostProp.setProperty("erp.org", jTxtOrg.getText());
        hostProp.setProperty("erp.pos", jTxtPOS.getText());
        hostProp.setProperty("erp.user", jTxtUser.getText());
        hostProp.setProperty("erp.password", new String(jTxtPassword.getPassword()));

        hostProp.setProperty("sync.type", jcboSyncType.getSelectedItem().toString());

        hostProp.setProperty("sync.importdir", jTxtImportDir.getText());
        hostProp.setProperty("sync.exportdir", jTxtExportDir.getText());

        dlSystem.setResourceAsProperties(appProp.getHost() + "/properties", hostProp);

        JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.syncchanges"), AppLocal.getIntString("title.sync"), JOptionPane.INFORMATION_MESSAGE);

        dirty.setDirty(false);
    }

    private void testConnection() {
        try {

            if (jTxtUrl.getText().isEmpty()) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.urlnotdefined")));
            }

            URL ws = new URL(jTxtUrl.getText() + "?l=" + jTxtUser.getText() + "&p=" + new String(jTxtPassword.getPassword()));
            URLConnection wsCon = ws.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(wsCon.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.webserviceconnectionok"), content.toString()));


        } catch (MalformedURLException emu) { // new URL() failed
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.malformedurlexception"), emu));
        } catch (IOException eio) { // openConnection() failed
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.returnnull"), eio));
        }

    }


    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Object getBean() {
        return this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        m_jSyncParams = new javax.swing.JPanel();
        jPanelNone = new javax.swing.JPanel();
        jPanelOpenbravoERP = new javax.swing.JPanel();
        jlabelUrl = new javax.swing.JLabel();
        jTxtUrl = new javax.swing.JTextField();
        jLabelId = new javax.swing.JLabel();
        jTxtClient = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTxtOrg = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTxtPOS = new javax.swing.JTextField();
        jLabelName = new javax.swing.JLabel();
        jTxtUser = new javax.swing.JTextField();
        jLabelProperties = new javax.swing.JLabel();
        jTxtPassword = new javax.swing.JPasswordField();
        jPanelCSV = new javax.swing.JPanel();
        jlabelImp = new javax.swing.JLabel();
        jTxtImportDir = new javax.swing.JTextField();
        jlabelExp = new javax.swing.JLabel();
        jTxtExportDir = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jcboSyncType = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        Save = new javax.swing.JButton();
        jbtnTest = new javax.swing.JButton();

        m_jSyncParams.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jPanelNoneLayout = new javax.swing.GroupLayout(jPanelNone);
        jPanelNone.setLayout(jPanelNoneLayout);
        jPanelNoneLayout.setHorizontalGroup(
            jPanelNoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 693, Short.MAX_VALUE)
        );
        jPanelNoneLayout.setVerticalGroup(
            jPanelNoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 263, Short.MAX_VALUE)
        );

        m_jSyncParams.add(jPanelNone, "none");

        jPanelOpenbravoERP.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("Label.ERPParameters"))); // NOI18N

        jlabelUrl.setText(AppLocal.getIntString("label.erpurl")); // NOI18N

        jLabelId.setText(AppLocal.getIntString("label.erpid")); // NOI18N

        jLabel1.setText(AppLocal.getIntString("label.erporg")); // NOI18N

        jLabel2.setText(AppLocal.getIntString("label.erppos")); // NOI18N

        jLabelName.setText(AppLocal.getIntString("label.erpuser")); // NOI18N

        jLabelProperties.setText(AppLocal.getIntString("label.erppassword")); // NOI18N

        javax.swing.GroupLayout jPanelOpenbravoERPLayout = new javax.swing.GroupLayout(jPanelOpenbravoERP);
        jPanelOpenbravoERP.setLayout(jPanelOpenbravoERPLayout);
        jPanelOpenbravoERPLayout.setHorizontalGroup(
            jPanelOpenbravoERPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOpenbravoERPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOpenbravoERPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelOpenbravoERPLayout.createSequentialGroup()
                        .addComponent(jlabelUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelOpenbravoERPLayout.createSequentialGroup()
                        .addComponent(jLabelId, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtClient, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelOpenbravoERPLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtOrg, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelOpenbravoERPLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelOpenbravoERPLayout.createSequentialGroup()
                        .addComponent(jLabelName, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelOpenbravoERPLayout.createSequentialGroup()
                        .addComponent(jLabelProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanelOpenbravoERPLayout.setVerticalGroup(
            jPanelOpenbravoERPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOpenbravoERPLayout.createSequentialGroup()
                .addGroup(jPanelOpenbravoERPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlabelUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOpenbravoERPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTxtClient, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelId, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOpenbravoERPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtOrg, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOpenbravoERPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOpenbravoERPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOpenbravoERPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 51, Short.MAX_VALUE))
        );

        m_jSyncParams.add(jPanelOpenbravoERP, "openbravoerp");

        jPanelCSV.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("Label.SynFolders"))); // NOI18N

        jlabelImp.setText(AppLocal.getIntString("label.importfolder")); // NOI18N

        jlabelExp.setText(AppLocal.getIntString("label.exportfolder")); // NOI18N

        javax.swing.GroupLayout jPanelCSVLayout = new javax.swing.GroupLayout(jPanelCSV);
        jPanelCSV.setLayout(jPanelCSVLayout);
        jPanelCSVLayout.setHorizontalGroup(
            jPanelCSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCSVLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCSVLayout.createSequentialGroup()
                        .addComponent(jlabelImp, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtImportDir, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelCSVLayout.createSequentialGroup()
                        .addComponent(jlabelExp, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtExportDir, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanelCSVLayout.setVerticalGroup(
            jPanelCSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCSVLayout.createSequentialGroup()
                .addGroup(jPanelCSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlabelImp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtImportDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlabelExp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtExportDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 178, Short.MAX_VALUE))
        );

        m_jSyncParams.add(jPanelCSV, "csv");

        jLabel3.setText(AppLocal.getIntString("label.synctype")); // NOI18N

        jcboSyncType.setModel(new javax.swing.DefaultComboBoxModel(modelSyncType));
        jcboSyncType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboSyncTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcboSyncType, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(364, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_jSyncParams, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jcboSyncType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_jSyncParams, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel1);

        Save.setText(AppLocal.getIntString("Button.Save")); // NOI18N
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });

        jbtnTest.setText(AppLocal.getIntString("button.test")); // NOI18N
        jbtnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnTestActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(686, Short.MAX_VALUE)
                .addComponent(jbtnTest)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Save))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(Save, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jbtnTest, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(390, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(86, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
        saveProperties();
    }//GEN-LAST:event_SaveActionPerformed

    private void jbtnTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnTestActionPerformed
        testConnection();
    }//GEN-LAST:event_jbtnTestActionPerformed

    private void jcboSyncTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboSyncTypeActionPerformed
        CardLayout cl = (CardLayout) (m_jSyncParams.getLayout());

        cl.show(m_jSyncParams, jcboSyncType.getSelectedItem().toString());

        if ("csv".equals(jcboSyncType.getSelectedItem())) {
            cl.show(m_jSyncParams, "csv");
        } else if ("openbravoerp".equals(jcboSyncType.getSelectedItem())) {
            cl.show(m_jSyncParams, "openbravoerp");
        } else {
            cl.show(m_jSyncParams, "none");
        }
    }//GEN-LAST:event_jcboSyncTypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelId;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelProperties;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelCSV;
    private javax.swing.JPanel jPanelNone;
    private javax.swing.JPanel jPanelOpenbravoERP;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTxtClient;
    private javax.swing.JTextField jTxtExportDir;
    private javax.swing.JTextField jTxtImportDir;
    private javax.swing.JTextField jTxtOrg;
    private javax.swing.JTextField jTxtPOS;
    private javax.swing.JPasswordField jTxtPassword;
    private javax.swing.JTextField jTxtUrl;
    private javax.swing.JTextField jTxtUser;
    private javax.swing.JButton jbtnTest;
    private javax.swing.JComboBox jcboSyncType;
    private javax.swing.JLabel jlabelExp;
    private javax.swing.JLabel jlabelImp;
    private javax.swing.JLabel jlabelUrl;
    private javax.swing.JPanel m_jSyncParams;
    // End of variables declaration//GEN-END:variables
}
