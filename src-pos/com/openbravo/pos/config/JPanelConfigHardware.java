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
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.util.ReportUtils;
import com.openbravo.pos.util.StringParser;
import java.awt.CardLayout;
import java.awt.Component;

/**
 *
 * @author adrianromero
 * @author Andrey Svininykh <svininykh@gmail.com>\
 * @version NORD POS 3
 */
public class JPanelConfigHardware extends javax.swing.JPanel implements PanelConfig {

    private final DirtyManager dirty = new DirtyManager();

    private final ParametersConfig printer1printerparams;
    private ParametersConfig printer2printerparams;
    private ParametersConfig printer3printerparams;

    String[] modelDisplayName = {"screen",
        "window",
        "Not defined"};
    String[] modelPrinterName = {"screen",
        "printer",
        "Not defined"};
    String[] modelFiscalPrinterName = {"Not defined"};
    String[] modelLabelPrinterName = {"Not defined"};

    public JPanelConfigHardware() {

        initComponents();

        String[] printernames = ReportUtils.getPrintNames();

        jcboMachineDisplay.addActionListener(dirty);

        jcboMachinePrinter.addActionListener(dirty);
        printer1printerparams = new ParametersPrinter(printernames);
        printer1printerparams.addDirtyManager(dirty);
        m_jPrinterParams1.add(printer1printerparams.getComponent(), "printer");

        jcboMachinePrinter2.addActionListener(dirty);
        printer2printerparams = new ParametersPrinter(printernames);
        printer2printerparams.addDirtyManager(dirty);
        m_jPrinterParams2.add(printer2printerparams.getComponent(), "printer");

        jcboMachinePrinter3.addActionListener(dirty);
        printer3printerparams = new ParametersPrinter(printernames);
        printer3printerparams.addDirtyManager(dirty);
        m_jPrinterParams3.add(printer3printerparams.getComponent(), "printer");

        jcboMachineFiscalPrinter.addActionListener(dirty);

        jcboMachineLabelPrinter.addActionListener(dirty);

        jcboMachineScale.addActionListener(dirty);

        jcboMachinePLUDevice.addActionListener(dirty);

        cboPrinters.addActionListener(dirty);

        // Scale
        jcboMachineScale.addItem("screen");
        jcboMachineScale.addItem("Not defined");

        // Scanner and Device with PLUs
        jcboMachinePLUDevice.addItem("Not defined");

        // Printers
        cboPrinters.addItem("(Default)");
        cboPrinters.addItem("(Show dialog)");
        for (String name : printernames) {
            cboPrinters.addItem(name);
        }
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
        return AppLocal.getIntString("Label.POSHardware");
    }

    @Override
    public void loadProperties(AppConfig config) {

        StringParser p = new StringParser(config.getProperty("machine.printer"));
        String sParamPrinter1 = p.nextToken(':');
        jcboMachinePrinter.setSelectedItem(sParamPrinter1);
        if (sParamPrinter1.equals("printer")) {
            printer1printerparams.setParameters(p);
        }
        
        p = new StringParser(config.getProperty("machine.printer.2"));
        String sParamPrinter2 = p.nextToken(':');
        jcboMachinePrinter2.setSelectedItem(sParamPrinter2);
        if (sParamPrinter2.equals("printer")) {
            printer2printerparams.setParameters(p);
        }

        p = new StringParser(config.getProperty("machine.printer.3")); 
        String sParamPrinter3 = p.nextToken(':');
        jcboMachinePrinter3.setSelectedItem(sParamPrinter3);
        if (sParamPrinter3.equals("printer")) {
            printer3printerparams.setParameters(p);
        }

        p = new StringParser(config.getProperty("machine.fiscalprinter"));
        jcboMachineFiscalPrinter.setSelectedItem(p.nextToken(':'));

        p = new StringParser(config.getProperty("machine.labelprinter"));
        jcboMachineLabelPrinter.setSelectedItem(p.nextToken(':'));

        p = new StringParser(config.getProperty("machine.display"));
        jcboMachineDisplay.setSelectedItem(p.nextToken(':'));

        p = new StringParser(config.getProperty("machine.scale"));        
        jcboMachineScale.setSelectedItem(p.nextToken(':'));

        p = new StringParser(config.getProperty("machine.pludevice"));
        jcboMachinePLUDevice.setSelectedItem(p.nextToken(':'));

        cboPrinters.setSelectedItem(config.getProperty("machine.printername"));

        dirty.setDirty(false);
    }

    @Override
    public void saveProperties(AppConfig config) {

        String sMachinePrinter = comboValue(jcboMachinePrinter.getSelectedItem());
        if (sMachinePrinter.equals("printer")) {
            config.setProperty("machine.printer", sMachinePrinter + ":" + printer1printerparams.getParameters());
        } else {
            config.setProperty("machine.printer", sMachinePrinter);
        }

        String sMachinePrinter2 = comboValue(jcboMachinePrinter2.getSelectedItem());
        if (sMachinePrinter2.equals("printer")) {
            config.setProperty("machine.printer.2", sMachinePrinter2 + ":" + printer2printerparams.getParameters());
        } else {
            config.setProperty("machine.printer.2", sMachinePrinter2);
        }

        String sMachinePrinter3 = comboValue(jcboMachinePrinter3.getSelectedItem());
        if (sMachinePrinter3.equals("printer")) {
            config.setProperty("machine.printer.3", sMachinePrinter3 + ":" + printer3printerparams.getParameters());
        } else {
            config.setProperty("machine.printer.3", sMachinePrinter3);
        }

        String sMachineFiscalPrinter = comboValue(jcboMachineFiscalPrinter.getSelectedItem());
        config.setProperty("machine.fiscalprinter", sMachineFiscalPrinter);

        String sMachineLabelPrinter = comboValue(jcboMachineLabelPrinter.getSelectedItem());
        config.setProperty("machine.labelprinter", sMachineLabelPrinter);

        String sMachineDisplay = comboValue(jcboMachineDisplay.getSelectedItem());
        config.setProperty("machine.display", sMachineDisplay);

        // La bascula
        String sMachineScale = comboValue(jcboMachineScale.getSelectedItem());
        config.setProperty("machine.scale", sMachineScale);

        // Scanner and Device with PLUs
        String sMachinePLUDevice = comboValue(jcboMachinePLUDevice.getSelectedItem());
        config.setProperty("machine.pludevice", sMachinePLUDevice);

        config.setProperty("machine.printername", comboValue(cboPrinters.getSelectedItem()));

        dirty.setDirty(false);
    }

    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jcboMachineDisplay = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jcboMachinePrinter = new javax.swing.JComboBox();
        m_jPrinterParams1 = new javax.swing.JPanel();
        jEmptyPrinter1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jcboMachinePrinter2 = new javax.swing.JComboBox();
        m_jPrinterParams2 = new javax.swing.JPanel();
        jEmptyPrinter2 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jcboMachinePrinter3 = new javax.swing.JComboBox();
        m_jPrinterParams3 = new javax.swing.JPanel();
        jEmptyPrinter3 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jcboMachineFiscalPrinter = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        jcboMachineScale = new javax.swing.JComboBox();
        jLabel26 = new javax.swing.JLabel();
        jcboMachinePLUDevice = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        cboPrinters = new javax.swing.JComboBox();
        jLabel50 = new javax.swing.JLabel();
        jcboMachineLabelPrinter = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jEmptyDisplay = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));

        jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));

        jLabel15.setText(AppLocal.getIntString("Label.MachineDisplay")); // NOI18N

        jcboMachineDisplay.setModel(new javax.swing.DefaultComboBoxModel(modelDisplayName));

        jLabel7.setText(AppLocal.getIntString("Label.MachinePrinter")); // NOI18N

        jcboMachinePrinter.setMaximumRowCount(10);
        jcboMachinePrinter.setModel(new javax.swing.DefaultComboBoxModel(modelPrinterName));
        jcboMachinePrinter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboMachinePrinterActionPerformed(evt);
            }
        });

        m_jPrinterParams1.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jEmptyPrinter1Layout = new javax.swing.GroupLayout(jEmptyPrinter1);
        jEmptyPrinter1.setLayout(jEmptyPrinter1Layout);
        jEmptyPrinter1Layout.setHorizontalGroup(
            jEmptyPrinter1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jEmptyPrinter1Layout.setVerticalGroup(
            jEmptyPrinter1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        m_jPrinterParams1.add(jEmptyPrinter1, "empty");

        jLabel18.setText(AppLocal.getIntString("Label.MachinePrinter2")); // NOI18N

        jcboMachinePrinter2.setMaximumRowCount(10);
        jcboMachinePrinter2.setModel(new javax.swing.DefaultComboBoxModel(modelPrinterName));
        jcboMachinePrinter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboMachinePrinter2ActionPerformed(evt);
            }
        });

        m_jPrinterParams2.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jEmptyPrinter2Layout = new javax.swing.GroupLayout(jEmptyPrinter2);
        jEmptyPrinter2.setLayout(jEmptyPrinter2Layout);
        jEmptyPrinter2Layout.setHorizontalGroup(
            jEmptyPrinter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jEmptyPrinter2Layout.setVerticalGroup(
            jEmptyPrinter2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        m_jPrinterParams2.add(jEmptyPrinter2, "empty");

        jLabel19.setText(AppLocal.getIntString("Label.MachinePrinter3")); // NOI18N

        jcboMachinePrinter3.setMaximumRowCount(10);
        jcboMachinePrinter3.setModel(new javax.swing.DefaultComboBoxModel(modelPrinterName));
        jcboMachinePrinter3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboMachinePrinter3ActionPerformed(evt);
            }
        });

        m_jPrinterParams3.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jEmptyPrinter3Layout = new javax.swing.GroupLayout(jEmptyPrinter3);
        jEmptyPrinter3.setLayout(jEmptyPrinter3Layout);
        jEmptyPrinter3Layout.setHorizontalGroup(
            jEmptyPrinter3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jEmptyPrinter3Layout.setVerticalGroup(
            jEmptyPrinter3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        m_jPrinterParams3.add(jEmptyPrinter3, "empty");

        jLabel29.setText(AppLocal.getIntString("Label.MachineFiscalPrinter")); // NOI18N

        jcboMachineFiscalPrinter.setModel(new javax.swing.DefaultComboBoxModel(modelFiscalPrinterName));

        jLabel25.setText(AppLocal.getIntString("label.scale")); // NOI18N

        jLabel26.setText(AppLocal.getIntString("label.pludevice")); // NOI18N

        jLabel1.setText(AppLocal.getIntString("label.reportsprinter")); // NOI18N

        jLabel50.setText(AppLocal.getIntString("Label.MachineLabelPrinter")); // NOI18N

        jcboMachineLabelPrinter.setMaximumRowCount(10);
        jcboMachineLabelPrinter.setModel(new javax.swing.DefaultComboBoxModel(modelLabelPrinterName));

        jPanel3.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jEmptyDisplayLayout = new javax.swing.GroupLayout(jEmptyDisplay);
        jEmptyDisplay.setLayout(jEmptyDisplayLayout);
        jEmptyDisplayLayout.setHorizontalGroup(
            jEmptyDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jEmptyDisplayLayout.setVerticalGroup(
            jEmptyDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel3.add(jEmptyDisplay, "empty");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcboMachineDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboMachinePrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboMachinePrinter2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboMachinePrinter3, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboMachineFiscalPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboMachineLabelPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboMachineScale, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboMachinePLUDevice, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboPrinters, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jPrinterParams2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jPrinterParams1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jPrinterParams3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(jcboMachineDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(jcboMachinePrinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jPrinterParams1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18)
                    .addComponent(jcboMachinePrinter2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jPrinterParams2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel19)
                    .addComponent(jcboMachinePrinter3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jPrinterParams3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel29)
                    .addComponent(jcboMachineFiscalPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel50)
                    .addComponent(jcboMachineLabelPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel25)
                    .addComponent(jcboMachineScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jcboMachinePLUDevice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboPrinters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jcboMachinePrinterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboMachinePrinterActionPerformed
        CardLayout cl = (CardLayout) (m_jPrinterParams1.getLayout());
        if (jcboMachinePrinter.getSelectedItem().equals("printer")) {
            cl.show(m_jPrinterParams1, "printer");
        } else {
            cl.show(m_jPrinterParams1, "empty");
        }
    }//GEN-LAST:event_jcboMachinePrinterActionPerformed

    private void jcboMachinePrinter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboMachinePrinter2ActionPerformed
        CardLayout cl = (CardLayout) (m_jPrinterParams2.getLayout());

        if (jcboMachinePrinter2.getSelectedItem().equals("printer")) {
            cl.show(m_jPrinterParams2, "printer");
        } else {
            cl.show(m_jPrinterParams2, "empty");
        }
    }//GEN-LAST:event_jcboMachinePrinter2ActionPerformed

    private void jcboMachinePrinter3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboMachinePrinter3ActionPerformed
        CardLayout cl = (CardLayout) (m_jPrinterParams3.getLayout());

        if (jcboMachinePrinter3.getSelectedItem().equals("printer")) {
            cl.show(m_jPrinterParams3, "printer");
        } else {
            cl.show(m_jPrinterParams3, "empty");
        }
    }//GEN-LAST:event_jcboMachinePrinter3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboPrinters;
    private javax.swing.JPanel jEmptyDisplay;
    private javax.swing.JPanel jEmptyPrinter1;
    private javax.swing.JPanel jEmptyPrinter2;
    private javax.swing.JPanel jEmptyPrinter3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox jcboMachineDisplay;
    private javax.swing.JComboBox jcboMachineFiscalPrinter;
    private javax.swing.JComboBox jcboMachineLabelPrinter;
    private javax.swing.JComboBox jcboMachinePLUDevice;
    private javax.swing.JComboBox jcboMachinePrinter;
    private javax.swing.JComboBox jcboMachinePrinter2;
    private javax.swing.JComboBox jcboMachinePrinter3;
    private javax.swing.JComboBox jcboMachineScale;
    private javax.swing.JPanel m_jPrinterParams1;
    private javax.swing.JPanel m_jPrinterParams2;
    private javax.swing.JPanel m_jPrinterParams3;
    // End of variables declaration//GEN-END:variables
}
