/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2016 Nord Trading Ltd. <http://www.nordpos.com>
 *
 * This file is part of NORD POS.
 *
 * NORD POS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * NORD POS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * NORD POS. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nordpos.sales.geomap;

import java.awt.Component;
import java.util.UUID;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.data.user.DirtyManager;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.1
 */
public class MarkersEditor extends JPanel implements EditorRecord {

    private SentenceList m_sentlayer;
    private ComboBoxValModel m_LayerModel;

    private String m_sID;

    public MarkersEditor(DataLogicGeomap dlGeomap, DirtyManager dirty) {
        initComponents();

        m_sentlayer = dlGeomap.getLayersList();
        m_LayerModel = new ComboBoxValModel();

        m_jName.getDocument().addDocumentListener(dirty);
        m_jLayer.addActionListener(dirty);
        m_jLatitude.getDocument().addDocumentListener(dirty);
        m_jLongitude.getDocument().addDocumentListener(dirty);
        m_jVisible.addActionListener(dirty);

        writeValueEOF();
    }

    public void activate() throws BasicException {
        m_LayerModel = new ComboBoxValModel(m_sentlayer.list());
        m_jLayer.setModel(m_LayerModel);
    }

    @Override
    public void refresh() {
    }

    @Override
    public final void writeValueEOF() {

        m_sID = null;
        m_jName.setText(null);
        m_LayerModel.setSelectedKey(null);
        m_jLatitude.setText(null);
        m_jLongitude.setText(null);
        m_jVisible.setSelected(false);

        m_jName.setEnabled(false);
        m_jLayer.setEnabled(false);
        m_jLatitude.setEnabled(false);
        m_jLongitude.setEnabled(false);
        m_jVisible.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {

        m_sID = UUID.randomUUID().toString();
        m_jName.setText(null);
        m_LayerModel.setSelectedFirst();
        m_jLatitude.setText(null);
        m_jLongitude.setText(null);
        m_jVisible.setSelected(true);

        m_jName.setEnabled(true);
        m_jLayer.setEnabled(true);
        m_jLatitude.setEnabled(true);
        m_jLongitude.setEnabled(true);
        m_jVisible.setEnabled(true);
    }

    @Override
    public void writeValueDelete(Object value) {

        Object[] marker = (Object[]) value;
        m_sID = Formats.STRING.formatValue(marker[0]);
        m_jName.setText(Formats.STRING.formatValue(marker[1]));
        m_jLatitude.setText(Formats.COORDINATE.formatValue(marker[2]));
        m_jLongitude.setText(Formats.COORDINATE.formatValue(marker[3]));
        m_jVisible.setSelected(((Boolean) marker[4]));
        m_LayerModel.setSelectedKey(marker[5]);

        m_jName.setEnabled(false);
        m_jLayer.setEnabled(false);
        m_jLatitude.setEnabled(false);
        m_jLongitude.setEnabled(false);
        m_jVisible.setEnabled(false);
    }

    @Override
    public void writeValueEdit(Object value) {

        Object[] marker = (Object[]) value;
        m_sID = Formats.STRING.formatValue(marker[0]);
        m_jName.setText(Formats.STRING.formatValue(marker[1]));
        m_jLatitude.setText(Formats.COORDINATE.formatValue(marker[2]));
        m_jLongitude.setText(Formats.COORDINATE.formatValue(marker[3]));
        m_jVisible.setSelected(((Boolean) marker[4]));
        m_LayerModel.setSelectedKey(marker[5]);

        m_jName.setEnabled(true);
        m_jLayer.setEnabled(true);
        m_jLatitude.setEnabled(true);
        m_jLongitude.setEnabled(true);
        m_jVisible.setEnabled(true);
    }

    @Override
    public Object createValue() throws BasicException {
        Object[] marker = new Object[6];
        marker[0] = m_sID;
        marker[1] = m_jName.getText();
        marker[2] = (Double) Formats.COORDINATE.parseValue(m_jLatitude.getText());
        marker[3] = (Double) Formats.COORDINATE.parseValue(m_jLongitude.getText());
        marker[4] = m_jVisible.isSelected();
        marker[5] = m_LayerModel.getSelectedKey();

        return marker;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jLatitude = new javax.swing.JTextField();
        m_jLongitude = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        m_jLayer = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        m_jVisible = new javax.swing.JCheckBox();
        m_jPosition = new javax.swing.JButton();

        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        jLabel3.setText(AppLocal.getIntString("label.placeposition")); // NOI18N

        jLabel1.setText(AppLocal.getIntString("label.layer")); // NOI18N

        jLabel8.setText(AppLocal.getIntString("label.visible")); // NOI18N

        m_jPosition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/applications-internet.png"))); // NOI18N
        m_jPosition.setFocusPainted(false);
        m_jPosition.setFocusable(false);
        m_jPosition.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jPosition.setRequestFocusEnabled(false);
        m_jPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPositionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_jLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_jVisible))
                .addContainerGap(135, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(m_jLayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(m_jLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(m_jVisible))
                .addContainerGap(173, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_jPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPositionActionPerformed
        try {
            JMarkerEdit editor = JMarkerEdit.getMarkerLocation(this);
            Geomarker marker = new Geomarker();
            Geolayer layer = (Geolayer) m_LayerModel.getSelectedItem();
            marker.setLayerId(layer.getId());
            marker.setLatitude((Double) (m_jLatitude.getText().isEmpty() ? 0.0 : Formats.COORDINATE.parseValue(m_jLatitude.getText())));
            marker.setLongtitude((Double) (m_jLongitude.getText().isEmpty() ? 0.0 : Formats.COORDINATE.parseValue(m_jLongitude.getText())));
            editor.editMarker(layer, marker);
            editor.setVisible(true);
            if (editor.isOK()) {                
                marker.setLatitude(BigDecimal.valueOf(editor.getMapMarker().getLat()).setScale(4,  BigDecimal.ROUND_HALF_UP).doubleValue());
                marker.setLongtitude(BigDecimal.valueOf(editor.getMapMarker().getLon()).setScale(4,  BigDecimal.ROUND_HALF_UP).doubleValue());
                m_jLatitude.setText(Formats.COORDINATE.formatValue(marker.getLatitude()));
                m_jLongitude.setText(Formats.COORDINATE.formatValue(marker.getLongtitude()));
            }
        } catch (BasicException ex) {
            Logger.getLogger(MarkersEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_m_jPositionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField m_jLatitude;
    private javax.swing.JComboBox m_jLayer;
    private javax.swing.JTextField m_jLongitude;
    private javax.swing.JTextField m_jName;
    private javax.swing.JButton m_jPosition;
    private javax.swing.JCheckBox m_jVisible;
    // End of variables declaration//GEN-END:variables

}
