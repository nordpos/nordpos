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
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.data.loader.TableDefinition;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import java.util.List;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.1
 */
public class DataLogicGeomap extends BeanFactoryDataSingle {

    protected Session s;

    @Override
    public void init(Session s) {
        this.s = s;
    }

    public final SentenceList getLayersList() {
        return new StaticSentence(s,
                "SELECT ID, NAME, VISIBLE, ICON, COLOUR FROM GEOLAYERS ORDER BY NAME",
                null,
                new SerializerReadClass(Geolayer.class));
    }

    public final TableDefinition getTableLayers() {
        return new TableDefinition(s,
                "GEOLAYERS",
                new String[]{"ID", "NAME", "VISIBLE", "ICON", "COLOUR"},
                new String[]{"ID", AppLocal.getIntString("Label.Name"), "", AppLocal.getIntString("label.image"), ""},
                new Datas[]{Datas.STRING, Datas.STRING, Datas.BOOLEAN, Datas.IMAGE, Datas.STRING},
                new Formats[]{Formats.NULL, Formats.STRING, Formats.BOOLEAN, Formats.NULL, Formats.STRING},
                "NAME",
                new int[]{0}
        );
    }

    TableDefinition getTableMarkers() {
        return new TableDefinition(s,
                "GEOMARKERS",
                new String[]{"ID", "NAME", "LATITUDE", "LONGITUDE", "VISIBLE", "GEOLAYER"},
                new String[]{"ID", AppLocal.getIntString("Label.Name"), "", "", "", ""},
                new Datas[]{Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.BOOLEAN, Datas.STRING},
                new Formats[]{Formats.NULL, Formats.STRING, Formats.DOUBLE, Formats.DOUBLE, Formats.BOOLEAN, Formats.STRING},
                "NAME",
                new int[]{0}
        );
    }

    public final List<Geomarker> getMarkers(String layerId) throws BasicException {
        return new PreparedSentence(s, "SELECT ID, NAME, LATITUDE, LONGITUDE, VISIBLE, GEOLAYER FROM GEOMARKERS WHERE GEOLAYER = ? ORDER BY NAME", SerializerWriteString.INSTANCE, new SerializerReadClass(Geomarker.class)).list(layerId);
    }

}
