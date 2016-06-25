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

import com.openbravo.pos.forms.AppLocal;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.1
 */
public class JMarkerEdit extends javax.swing.JDialog implements JMapViewerEventListener {

    private boolean ok;
    private JMapViewerTree treeMap;
    private MapMarker mapMarker;

    private JMarkerEdit(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    private JMarkerEdit(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    private void init() {
        setTitle(AppLocal.getIntString("label.marker"));
        treeMap = new JMapViewerTree("Markers");
        initComponents();
        getRootPane().setDefaultButton(m_jButtonOK);

        map().addJMVListener(this);
        map().setScrollWrapEnabled(true);

        JPanel jLocations = new JPanel();
        jLocations.applyComponentOrientation(getComponentOrientation());
        jLocations.setLayout(new BorderLayout());
        jLocations.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)),
                new javax.swing.border.TitledBorder(treeMap.getName())));
        JPanel jPanCont = new JPanel();
        jPanCont.applyComponentOrientation(getComponentOrientation());

        m_jPanelMap.add(jLocations, BorderLayout.CENTER);
        jLocations.add(treeMap, BorderLayout.CENTER);

        map().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JMapViewerMouseClicked(e);
            }
        });
    }

    public static JMarkerEdit getMarkerLocation(Component parent) {

        Window window = SwingUtilities.getWindowAncestor(parent);

        JMarkerEdit myMsg;
        if (window instanceof Frame) {
            myMsg = new JMarkerEdit((Frame) window, true);
        } else {
            myMsg = new JMarkerEdit((Dialog) window, true);
        }
        myMsg.init();
        myMsg.applyComponentOrientation(parent.getComponentOrientation());
        return myMsg;
    }

    public void editMarker(Geolayer layer, Geomarker marker) {
        Coordinate coordinate = new Coordinate(marker.getLatitude(), marker.getLongtitude());
        Layer mapLayer = new Layer(layer.getName());
        mapLayer.setVisibleTexts(Boolean.TRUE);
        Style markerStyle = new Style();
        markerStyle.setColor(Color.GRAY);
        markerStyle.setBackColor(layer.getColor());        
        mapMarker = new MapMarkerDot(mapLayer, marker.getName(), coordinate, markerStyle);
        if (layer.getIcon() != null) {
            map().addMapMarker(new IconMarker(mapMarker.getLayer(), mapMarker.getCoordinate(), layer.getIcon()));
        } else {            
            map().addMapMarker(mapMarker);
        }        
        map().setDisplayPosition(coordinate, JMapViewer.MAX_ZOOM / 2);
    }
    
        public MapMarker getMapMarker() {
        return mapMarker;
    }

    public boolean isOK() {
        return ok;
    }

    private void JMapViewerMouseClicked(java.awt.event.MouseEvent evt) {
        ICoordinate icoord = map().getPosition(evt.getPoint());
        if (SwingUtilities.isRightMouseButton(evt) && evt.getClickCount() == 1) {
            map().removeMapMarker(mapMarker);
            mapMarker.setLat(icoord.getLat());
            mapMarker.setLon(icoord.getLon());
            map().addMapMarker(mapMarker);
        }
    }

    private JMapViewer map() {
        return treeMap.getViewer();
    }

    private void updateZoomParameters() {

    }

    @Override
    public void processCommand(JMVCommandEvent command) {
        if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM)
                || command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
            updateZoomParameters();
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

        jPanel5 = new javax.swing.JPanel();
        m_jPanelMap = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jButtonOK = new javax.swing.JButton();
        m_jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel5.setLayout(new java.awt.BorderLayout());

        m_jPanelMap.setPreferredSize(new java.awt.Dimension(600, 300));
        m_jPanelMap.setLayout(new java.awt.BorderLayout());
        jPanel5.add(m_jPanelMap, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        m_jButtonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/button_ok.png"))); // NOI18N
        m_jButtonOK.setText(AppLocal.getIntString("Button.OK")); // NOI18N
        m_jButtonOK.setFocusPainted(false);
        m_jButtonOK.setFocusable(false);
        m_jButtonOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonOK.setRequestFocusEnabled(false);
        m_jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonOKActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonOK);

        m_jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/button_cancel.png"))); // NOI18N
        m_jButtonCancel.setText(AppLocal.getIntString("Button.Cancel")); // NOI18N
        m_jButtonCancel.setFocusPainted(false);
        m_jButtonCancel.setFocusable(false);
        m_jButtonCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        m_jButtonCancel.setRequestFocusEnabled(false);
        m_jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jButtonCancelActionPerformed(evt);
            }
        });
        jPanel1.add(m_jButtonCancel);

        jPanel5.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(609, 390));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonOKActionPerformed
        ok = true;
        dispose();
    }//GEN-LAST:event_m_jButtonOKActionPerformed

    private void m_jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jButtonCancelActionPerformed

        dispose();
    }//GEN-LAST:event_m_jButtonCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton m_jButtonCancel;
    private javax.swing.JButton m_jButtonOK;
    private javax.swing.JPanel m_jPanelMap;
    // End of variables declaration//GEN-END:variables

}
