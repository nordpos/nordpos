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
package com.nordpos.device.util;

import gnu.io.SerialPort;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class SerialPortParameters {

    public static int getSpeed(String sSpeedParam) {
        switch (sSpeedParam) {
            case "2400":
                return 2400;
            case "4800":
                return 4800;
            case "9600":
                return 9600;
            case "19200":
                return 19200;
            case "38400":
                return 38400;
            case "57600":
                return 57600;
            case "115200":
                return 115200;
            default:
                return 9600;
        }
    }

    public static int getDataBits(String sBitsParam) {
        switch (sBitsParam) {
            case "5":
                return SerialPort.DATABITS_5;
            case "6":
                return SerialPort.DATABITS_6;
            case "7":
                return SerialPort.DATABITS_7;
            case "8":
                return SerialPort.DATABITS_8;
            default:
                return SerialPort.DATABITS_8;
        }
    }

    public static int getStopBits(String sStopBitsParam) {
        switch (sStopBitsParam) {
            case "1":
                return SerialPort.STOPBITS_1;
            case "2":
                return SerialPort.STOPBITS_2;
            default:
                return SerialPort.STOPBITS_1;
        }
    }

    public static int getParity(String sPortParity) {
        switch (sPortParity) {
            case "none":
                return SerialPort.PARITY_NONE;
            case "even":
                return SerialPort.PARITY_EVEN;
            case "odd":
                return SerialPort.PARITY_ODD;
            default:
                return SerialPort.PARITY_NONE;
        }
    }
}
