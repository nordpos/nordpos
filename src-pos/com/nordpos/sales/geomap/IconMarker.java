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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 *
 * @version NORD POS 3.1
 */
public class IconMarker extends MapMarkerCircle implements MapMarker {

    private Image image;

    public IconMarker(Layer layer, Coordinate coord, Image image) {
        this(layer, coord, 1, image);
    }

    public IconMarker(Layer layer, Coordinate coord, double radius, Image image) {
        super(layer, coord, radius);
        this.image = image;
    }

    @Override
    public void paint(Graphics g, Point position, int radio) {
        double r = this.getRadius();
        int width = (int) (this.image.getWidth(null) * r);
        int height = (int) (this.image.getHeight(null) * r);
        int w2 = width / 2;
        int h2 = height / 2;
        g.drawImage(this.image, position.x - w2, position.y - h2, width, height, null);
        this.paintText(g, position);
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
