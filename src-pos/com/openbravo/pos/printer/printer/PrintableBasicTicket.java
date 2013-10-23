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

package com.openbravo.pos.printer.printer;

import com.openbravo.pos.printer.ticket.BasicTicket;
import com.openbravo.pos.printer.ticket.PrintItem;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 *
 * @author adrianromero
 */
public class PrintableBasicTicket implements Printable {

    private int imageable_width;
    private int imageable_height;
    private int imageable_x;
    private int imageable_y;

    private BasicTicket ticket;

    public PrintableBasicTicket(BasicTicket ticket, int imageable_x, int imageable_y, int imageable_width, int imageable_height) {
        this.ticket = ticket;
        this.imageable_x = imageable_x;
        this.imageable_y = imageable_y;
        this.imageable_width = imageable_width;
        this.imageable_height = imageable_height;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

        Graphics2D g2d = (Graphics2D) graphics;

        int line = 0;
        int currentpage = 0;
        int currentpagey = 0;
        boolean printed = false;

//        System.out.println(pageFormat.getImageableX());
//        System.out.println(pageFormat.getImageableY());
//        System.out.println(pageFormat.getImageableWidth());
//        System.out.println(pageFormat.getImageableHeight());

        g2d.translate(imageable_x, imageable_y);

        java.util.List<PrintItem> commands = ticket.getCommands();

        while (line < commands.size()) {

            int itemheight = commands.get(line).getHeight();

            if (currentpagey + itemheight <= imageable_height) {
                currentpagey += itemheight;
            } else {
                currentpage ++;
                currentpagey = itemheight;
            }

            if (currentpage < pageIndex) {
                line ++;
            } else if (currentpage == pageIndex) {
                printed = true;
                commands.get(line).draw(g2d, 0, currentpagey - itemheight, imageable_width);

                line ++;
            } else if (currentpage > pageIndex) {
                line ++;
            }
        }

        return printed
            ? Printable.PAGE_EXISTS
            : Printable.NO_SUCH_PAGE;
    }
}
