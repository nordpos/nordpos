//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2009 Openbravo, S.L.
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
package com.openbravo.sync.panel;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.pos.forms.*;
import com.openbravo.pos.util.AltEncrypter;
import com.openbravo.sync.kettle.KettleTransformation;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.row.ValueMetaInterface;

/**
 * @author Mikel Irurita
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class Preview extends JPanel implements JPanelView, BeanFactoryApp {

    DataLogicSystem dlSystem;
    AppProperties appProp;
    Properties hostProp;

    private String sERPUrl;
    private String sERPClientId;
    private String sERPOrgId;
    private String sERPPos;
    private String sERPUser;
    private String sERPPassword;

    private String sImportDirectory;
    private String sExportDirectory;

    private String sImportType;

    public Preview() {
        initComponents();
    }

    @Override
    public void init(AppView app) throws BeanFactoryException {
        dlSystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        appProp = app.getProperties();
        hostProp = dlSystem.getResourceAsProperties(appProp.getHost() + "/properties");
    }

    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.PreviewSync");
    }

    @Override
    public void activate() throws BasicException {
        initSyncParameters();
        initERPParameters();
        initSyncFolders();
        initEntityCombo();
        clearAll();
    }

    @Override
    public boolean deactivate() {
        return true;
    }

    private void initSyncParameters() {
        sImportType = hostProp.getProperty("sync.type", "generaterows").toLowerCase();
    }

    private void initSyncFolders() {
        sImportDirectory = hostProp.getProperty("sync.importdir", System.getProperty("user.home"));
        sExportDirectory = hostProp.getProperty("sync.exportdir", System.getProperty("user.home"));
    }

    private void initERPParameters() {
        sERPUrl = hostProp.getProperty("erp.url");
        sERPClientId = hostProp.getProperty("erp.id");
        sERPOrgId = hostProp.getProperty("erp.org");
        sERPPos = hostProp.getProperty("erp.pos");
        sERPUser = hostProp.getProperty("erp.user");
        if (sERPUser != null && sERPPassword != null && sERPPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sERPUser);
            sERPPassword = cypher.decrypt(sERPPassword.substring(6));
        }
    }

    private void initEntityCombo() {
        String[] entities = new String[]{
//            "Import ATTRIBUTE",
//            "Import ATTRIBUTESET",
//            "Import ATTRIBUTEUSE",
//            "Import ATTRIBUTEVALUE",
//            "Import ATTRIBUTEINSTANCE",
//            "Import ATTRIBUTESETINSTANCE",
            "Import CATEGORIES",
            "Import CUSTOMERS",
//            "Import TAXCUSTCATEGORIES",
//            "Import STOCKDIARY",
            "Import LOCATIONS",
            "Import PRODUCTS",
            "Import TAXES",
            "Import TAXCATEGORIES",
            "Export Orders"};
        jcboxEntity.setModel(new DefaultComboBoxModel(entities));
        redrawSyncButton();
    }

    private void prepareTransformations(String sTransMode) {
        KettleTransformation kt = null;
        clearAll();

        try {
            String selectedEntity = (String) jcboxEntity.getSelectedItem();
            if (selectedEntity != null) {
                File f = createTransFile("/com/openbravo/transformations/" + sImportType + "/PreviewImport.ktr");
                if (f == null) {
                    jTextLog.append("Transformation file doesnt exist\n");
                } else {
                    kt = new KettleTransformation(f, LogLevel.BASIC, null);

                    kt.setVariable("entity", selectedEntity);

                    kt.setVariable("mode", sTransMode);

                    kt.setVariable("db.URL", appProp.getDBURL());
                    kt.setVariable("db.driver", appProp.getDBDriver());
                    kt.setVariable("db.user", appProp.getDBUser());
                    kt.setVariable("db.password", appProp.getDBPassword());

                    kt.setVariable("sync.importdir", sImportDirectory);
                    kt.setVariable("sync.exportdir", sExportDirectory);

                    kt.setVariable("erp.URL", sERPUrl);
                    kt.setVariable("erp.id", sERPClientId);
                    kt.setVariable("erp.org", sERPOrgId);
                    kt.setVariable("erp.pos", sERPPos);
                    kt.setVariable("erp.user", sERPUser);
                    kt.setVariable("erp.password", sERPPassword);

                    kt.runLocal();

                    jTextLog.setText(kt.getLogContent());

                    if (kt.hasFinish()) {
                        showResults(kt, selectedEntity + " " + sTransMode);
                        myTable1.refresh();
                    }
                    f.delete();
                }
            }
        } catch (Exception e) {
            jTextLog.setText(kt.getLogContent());
        }
    }

    private void showResults(KettleTransformation kt, String selectedEntity) {

        String action = "write";

        List<RowMetaAndData> list = kt.getStepRows(selectedEntity, action);

        if (list.isEmpty()) {
            myTable1.refresh();
            jTextLog.setText("No Data");
        } else {
            List<Object> d = new ArrayList<Object>();

//            if (selectedEntity.endsWith("UploadOrder")) {
//                String[] header = {"UUID", "*ID", "TYPE", "DATE", "CASHIER", "CUSTOMER ID", "CUSTOMER NAME", "TICKETLINE", "PRODUCT", "UNITS", "PRICE", "TAX", "TOTAL"};
//                List<String> wordList = Arrays.asList(header);
//                d.addAll(wordList);
//            } else {

            List<ValueMetaInterface> vmlist = list.get(0).getRowMeta().getValueMetaList();

            int asterisk = 0;
            for (ValueMetaInterface vmi : vmlist) {
                if (!vmi.getName().equalsIgnoreCase("entity") && !vmi.getName().equalsIgnoreCase("mode")) {
                    if (asterisk == 1) {
                        d.add("* " + vmi.getName());
                    } else {
                        d.add(vmi.getName());
                    }
                    asterisk++;
                }
            }

            myTable1.createTable(null, d.toArray());

            for (RowMetaAndData r : list) {
//                Object[] o = r.getData();
//                Object[] dest = new Object[o.length];
//                System.arraycopy(o, 2, dest, 0, o.length-2);
//                myTable1.addLine(dest);
                myTable1.addLine(r.getData());
            }
        }
    }

    private void clearAll() {
        myTable1.clear();
        jTextLog.setText("");
    }

    private File createTransFile(String path) {
        FileWriter fw = null;
        File f = null;

        try {
            byte[] bytes = ImageUtils.getBytesFromResource(path);

            String selectedEntity = (String) jcboxEntity.getSelectedItem();

            if (bytes == null) {
                return null;
            } else {
                if (selectedEntity.startsWith("Export")) {
                    f = File.createTempFile("PreviewExport", ".ktr");
                } else {
                    f = File.createTempFile("PreviewImport", ".ktr");
                }
                fw = new FileWriter(f);
                String text = new String(bytes);
                fw.write(text);
            }

        } catch (IOException ex) {
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
            }
        }

        return f;
    }

    private void redrawSyncButton() {
        if (jcboxEntity.getSelectedItem().toString().startsWith("Import")) {
            jbtnSync.setEnabled(true);
            jbtnSync.setText(AppLocal.getIntString("label.import"));
            jbtnSync.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/edit-redo.png")));
        } else if (jcboxEntity.getSelectedItem().toString().startsWith("Export")) {
            jbtnSync.setEnabled(true);
            jbtnSync.setText(AppLocal.getIntString("label.export"));
            jbtnSync.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/edit-undo.png")));
        } else {
            jbtnSync.setEnabled(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jbtnClearTable = new javax.swing.JButton();
        jbtnExec = new javax.swing.JButton();
        jcboxEntity = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jbtnSync = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        myTable1 = new com.openbravo.sync.panel.MyTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextLog = new javax.swing.JTextArea();

        jbtnClearTable.setText(AppLocal.getIntString("label.clear")); // NOI18N
        jbtnClearTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnClearTableActionPerformed(evt);
            }
        });

        jbtnExec.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/launch.png"))); // NOI18N
        jbtnExec.setText(AppLocal.getIntString("label.previewtransformation")); // NOI18N
        jbtnExec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExecActionPerformed(evt);
            }
        });

        jcboxEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboxEntityActionPerformed(evt);
            }
        });

        jLabel1.setText(AppLocal.getIntString("label.entitytopreview")); // NOI18N

        jbtnSync.setText(AppLocal.getIntString("label.sync")); // NOI18N
        jbtnSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSyncActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jcboxEntity, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnClearTable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnExec)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnSync)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcboxEntity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnClearTable)
                    .addComponent(jbtnExec)
                    .addComponent(jbtnSync))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(myTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(myTable1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
        );

        jLabel2.setText(AppLocal.getIntString("label.log")); // NOI18N

        jTextLog.setLineWrap(true);
        jTextLog.setRows(8);
        jScrollPane1.setViewportView(jTextLog);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jbtnExecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExecActionPerformed
    prepareTransformations("preview");
}//GEN-LAST:event_jbtnExecActionPerformed

private void jbtnClearTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClearTableActionPerformed
    myTable1.clear();
    jTextLog.setText(null);
}//GEN-LAST:event_jbtnClearTableActionPerformed

    private void jbtnSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSyncActionPerformed
        prepareTransformations("complete");
    }//GEN-LAST:event_jbtnSyncActionPerformed

    private void jcboxEntityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboxEntityActionPerformed
        redrawSyncButton();
    }//GEN-LAST:event_jcboxEntityActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextLog;
    private javax.swing.JButton jbtnClearTable;
    private javax.swing.JButton jbtnExec;
    private javax.swing.JButton jbtnSync;
    private javax.swing.JComboBox jcboxEntity;
    private com.openbravo.sync.panel.MyTable myTable1;
    // End of variables declaration//GEN-END:variables
}
