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

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.sales.JTicketsBag;
import com.openbravo.pos.sales.TicketsEditor;
import com.openbravo.pos.util.RoundUtils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
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
public class JTicketsBagLocationMap extends JTicketsBag implements JMapViewerEventListener {

    private java.util.List<Geolayer> m_alayers;

    private final JMapViewerTree treeMap;
    private final JTicketsBagLocation m_location;
    private final Layer currentLayer;
    private final MapMarker currentMarker;
    private final DataLogicGeomap dlGeomap;

    public JTicketsBagLocationMap(AppView app, TicketsEditor panelticket) {

        super(app, panelticket);

        dlGeomap = (DataLogicGeomap) app.getBean(DataLogicGeomap.class.getName());

        treeMap = new JMapViewerTree("Tickets");
        m_location = new JTicketsBagLocation(app, this);
        initComponents();
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

        currentLayer = new Layer("Current");
        currentMarker = new MapMarkerDot(currentLayer, new Coordinate(0, 0));

        try {
            reload();
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagLocationMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void JMapViewerMouseClicked(java.awt.event.MouseEvent evt) {
        ICoordinate icoord = map().getPosition(evt.getPoint());
        if (SwingUtilities.isRightMouseButton(evt) && evt.getClickCount() == 1) {
            map().removeMapMarker(currentMarker);
            currentMarker.setLat(icoord.getLat());
            currentMarker.setLon(icoord.getLon());
            map().addMapMarker(currentMarker);
        }
    }

    @Override
    public void activate() {
        m_panelticket.setActiveTicket(null, null);
        m_location.activate();
        showView("geomap");
    }

    @Override
    public boolean deactivate() {
        return true;
    }

    @Override
    protected JComponent getNullComponent() {
        return this;
    }

    @Override
    public void deleteTicket() {
        m_panelticket.setActiveTicket(null, null);
    }

    @Override
    protected JComponent getBagComponent() {
        return m_location;
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout) (getLayout());
        cl.show(this, view);
    }

    private void reload() throws BasicException {
        map().removeAllMapMarkers();
        m_alayers = dlGeomap.getLayersList().list();
        for (Geolayer geolayer : m_alayers) {
            if (geolayer.isVisible()) {
                Layer visibleLayer = new Layer(geolayer.getName());
                visibleLayer.setVisibleTexts(Boolean.TRUE);
                Style style = new Style();
                style.setColor(Color.GRAY);
                style.setBackColor(geolayer.getColor());
                for (Geomarker geomarker : dlGeomap.getMarkers(geolayer.getId())) {
                    if (geomarker.isVisible()) {
                        if (geolayer.getIcon() != null) {
                            map().addMapMarker(new IconMarker(visibleLayer, new Coordinate(geomarker.getLatitude(), geomarker.getLongtitude()),
                                    geolayer.getIcon()));
                        } else {
                            MapMarkerDot markerDot = new MapMarkerDot(visibleLayer, geomarker.getName(), new Coordinate(geomarker.getLatitude(), geomarker.getLongtitude()), style);
                            map().addMapMarker(markerDot);
                        }
                    }
                }
            }
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelMap = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jbtnRefresh = new javax.swing.JButton();
        m_jText = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());

        m_jPanelMap.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_jbtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        m_jbtnRefresh.setText(AppLocal.getIntString("button.reloadticket")); // NOI18N
        m_jbtnRefresh.setFocusPainted(false);
        m_jbtnRefresh.setFocusable(false);
        m_jbtnRefresh.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnRefresh.setRequestFocusEnabled(false);
        m_jbtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnRefreshActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnRefresh);
        jPanel2.add(m_jText);

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_START);

        m_jPanelMap.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(m_jPanelMap, "geomap");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnRefreshActionPerformed
        try {
            reload();
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagLocationMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_m_jbtnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel m_jPanelMap;
    private javax.swing.JLabel m_jText;
    private javax.swing.JButton m_jbtnRefresh;
    // End of variables declaration//GEN-END:variables

}
