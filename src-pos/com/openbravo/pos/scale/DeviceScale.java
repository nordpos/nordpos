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

package com.openbravo.pos.scale;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.util.SerialPortParameters;
import com.openbravo.pos.util.StringParser;
import gnu.io.SerialPort;
import java.awt.Component;

public class DeviceScale {

    private Scale m_scale;
    private String sScaleType;

    /** Creates a new instance of DeviceScale */
    public DeviceScale(Component parent, AppProperties props) {

        Integer iScaleSerialPortSpeed = 4800;
        Integer iScaleSerialPortDataBits = SerialPort.DATABITS_8;
        Integer iScaleSerialPortStopBits = SerialPort.STOPBITS_1;
        Integer iScaleSerialPortParity = SerialPort.PARITY_ODD;

        StringParser sd = new StringParser(props.getProperty("machine.scale"));
        sScaleType = sd.nextToken(':');
        String sScaleParam1 = sd.nextToken(',');
        // String sScaleParam2 = sd.nextToken(',');

        iScaleSerialPortSpeed = SerialPortParameters.getSpeed(sd.nextToken(','));
        iScaleSerialPortDataBits =  SerialPortParameters.getDataBits(sd.nextToken(','));
        iScaleSerialPortStopBits = SerialPortParameters.getStopBits(sd.nextToken(','));
        iScaleSerialPortParity = SerialPortParameters.getParity(sd.nextToken(','));

        if ("dialog1".equals(sScaleType)) {
            m_scale = new ScaleComm(sScaleParam1, iScaleSerialPortSpeed, iScaleSerialPortDataBits, iScaleSerialPortStopBits, iScaleSerialPortParity);
        } else if ("samsungesp".equals(sScaleType)) {
            m_scale = new ScaleSamsungEsp(sScaleParam1, iScaleSerialPortSpeed, iScaleSerialPortDataBits, iScaleSerialPortStopBits, iScaleSerialPortParity);
        } else if ("fake".equals(sScaleType)) { // a fake scale for debugging purposes
            m_scale = new ScaleFake();
        } else if ("screen".equals(sScaleType)) { // on screen scale
            m_scale = new ScaleDialog(parent);
        } else {
            m_scale = null;
        }
    }

    public boolean existsScale() {
        return m_scale != null;
    }

    public Double readWeight() throws ScaleException {

        if (m_scale == null) {
            throw new ScaleException(AppLocal.getIntString("scale.notdefined"));
        } else {
            Double result = m_scale.readWeight();
            if (result == null) {
                return null; // Canceled by the user / scale
            } else if ((result.doubleValue() < 0.002) && "massak".equals(sScaleType) == false) {
                // invalid result. nothing on the scale
                throw new ScaleException(AppLocal.getIntString("scale.invalidvalue"));
            } else if ("massak".equals(sScaleType)) {
                if ((result >= 0.04 && result <= 15000.0) || (result <= -0.04 && result >= -15000.0)) {
                    return result;
                } else {
                    throw new ScaleException(AppLocal.getIntString("scale.invalidvalue"));
                }
            } else {
                // valid result
                return result;
            }
        }
    }
}
