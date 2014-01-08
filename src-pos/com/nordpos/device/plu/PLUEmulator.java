/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2013 Nord Trading Ltd. <http://www.nordpos.com>
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
package com.nordpos.device.plu;

import com.nordpos.device.PLUInterface;
import com.nordpos.device.util.SerialPortParameters;
import com.openbravo.pos.util.StringParser;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class PLUEmulator implements PLUInterface {

    @Override
    public DevicePLU getPLU(String sProperty) throws Exception {
        StringParser pluProperty = new StringParser(sProperty);
        String sDevicePLUType = pluProperty.nextToken(':');
        String sDevicePLUParam1 = pluProperty.nextToken(',');
        Integer iDevicePLUSerialPortSpeed = SerialPortParameters.getSpeed(pluProperty.nextToken(','));
        Integer iDevicePLUSerialPortDataBits = SerialPortParameters.getDataBits(pluProperty.nextToken(','));
        Integer iDevicePLUSerialPortStopBits = SerialPortParameters.getStopBits(pluProperty.nextToken(','));
        Integer iDevicePLUSerialPortParity = SerialPortParameters.getParity(pluProperty.nextToken(','));

        switch (sDevicePLUType) {
            case "scanpal2":
                return new DeviceScannerComm(sDevicePLUParam1, iDevicePLUSerialPortSpeed, iDevicePLUSerialPortDataBits, iDevicePLUSerialPortStopBits, iDevicePLUSerialPortParity);
            default:
                return new DevicePLUNull();
        }
    }
}
