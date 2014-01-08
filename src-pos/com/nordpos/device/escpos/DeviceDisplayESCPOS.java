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

package com.nordpos.device.escpos;

import com.nordpos.device.display.DeviceDisplaySerial;
import com.nordpos.device.writter.Writter;
import com.openbravo.pos.util.StringUtils;

public class DeviceDisplayESCPOS extends DeviceDisplaySerial {

    private final UnicodeTranslator trans;

    public DeviceDisplayESCPOS(Writter display, UnicodeTranslator trans) {
        this.trans = trans;
        init(display);
    }

    @Override
    public void initVisor() {
        display.init(Commands.INIT);
        display.write(Commands.SELECT_DISPLAY);
        display.write(trans.getCodeTable());
        display.write(Commands.VISOR_HIDE_CURSOR);
        display.write(Commands.VISOR_CLEAR);
        display.write(Commands.VISOR_HOME);
        display.flush();
    }

    @Override
    public void repaintLines() {
        display.write(Commands.SELECT_DISPLAY);
        display.write(Commands.VISOR_CLEAR);
        display.write(Commands.VISOR_HOME);
        display.write(trans.transString(StringUtils.alignLeft(m_displaylines.getLine1(), 20)));
        display.write(trans.transString(StringUtils.alignLeft(m_displaylines.getLine2(), 20)));
        display.flush();
    }
}
