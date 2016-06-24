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
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.data.loader.SerializableRead;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.1
 */
public class Geolayer implements SerializableRead, IKeyed {

    private static final long serialVersionUID = -1347816332754251261L;

    private String id;
    private String name;
    private Boolean visible;
    private BufferedImage icon;
    private String colourCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public BufferedImage getIcon() {
        return icon;
    }

    public void setIcon(BufferedImage icon) {
        this.icon = icon;
    }
    
    public Color getColor() {
        return new Color((int) Integer.decode(colourCode));
    }

    public String getColourCode() {
        return colourCode;
    }

    public void setColourCode(String colourCode) {
        this.colourCode = colourCode;
    }    
    
    
    @Override
    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        name = dr.getString(2);
        visible = dr.getBoolean(3);
        icon = ImageUtils.readImage(dr.getBytes(4));
        colourCode = dr.getString(5);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object getKey() {
        return id;
    }
}
