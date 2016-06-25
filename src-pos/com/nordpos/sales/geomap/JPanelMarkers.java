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
import com.openbravo.data.gui.ListCellRendererBasic;
import com.openbravo.data.loader.TableDefinition;
import com.openbravo.data.loader.Vectorer;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.data.user.ListProvider;
import com.openbravo.data.user.ListProviderCreator;
import com.openbravo.data.user.SaveProvider;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.panels.JPanelTable;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.1
 */
public class JPanelMarkers extends JPanelTable {

    private TableDefinition tmarkers;
    private MarkersEditor jeditor;

    public JPanelMarkers() {
        JPanel jPanCont;
        jPanCont = new JPanel();
        jPanCont.applyComponentOrientation(getComponentOrientation());
    }

    @Override
    protected void init() {
        DataLogicGeomap dlGeomap = (DataLogicGeomap) app.getBean(DataLogicGeomap.class.getName());
        tmarkers = dlGeomap.getTableMarkers();
        jeditor = new MarkersEditor(dlGeomap, dirty);
    }

    @Override
    public ListProvider getListProvider() {
        return new ListProviderCreator(tmarkers);
    }

    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(tmarkers);
    }

    @Override
    public Vectorer getVectorer() {
        return tmarkers.getVectorerBasic(new int[]{1});
    }

    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(tmarkers.getRenderStringBasic(new int[]{1}));
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Geomarkers");
    }

    @Override
    public void activate() throws BasicException {
        jeditor.activate(); // primero activo el editor
        super.activate();   // segundo activo el padre
    }

}
