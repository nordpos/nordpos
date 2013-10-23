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

package com.openbravo.pos.printer.ticket;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PrintItemImage implements PrintItem {

    protected BufferedImage image;
    protected double scale;

    /** Creates a new instance of PrintItemImage
     * @param image
     */
    public PrintItemImage(BufferedImage image, double scale) {
        this.image = image;
        this.scale = scale;
    }

    @Override
    public void draw(Graphics2D g, int x, int y, int width) {
        g.drawImage(image, x + (width - (int)(image.getWidth() * scale)) / 2, y, (int)(image.getWidth() * scale), (int)(image.getHeight() * scale), null);
    }

    @Override
    public int getHeight() {
        return (int) (image.getHeight() * scale);
    }
}
