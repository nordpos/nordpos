/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.pludevice;

import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.util.SerialPortParameters;
import com.openbravo.pos.util.StringParser;
import gnu.io.SerialPort;
import java.awt.Component;

/**
 *
 * @author administrator
 */
//    private DevicePLU m_scale;
public class DevicePLUsFactory {

    private DevicePLUsFactory() {
    }

    public static DevicePLUs createInstance(Component parent, AppProperties props) {

        String sDevicePLUType = "";
        Integer iDevicePLUSerialPortSpeed = 115200;
        Integer iDevicePLUSerialPortDataBits = SerialPort.DATABITS_8;
        Integer iDevicePLUSerialPortStopBits = SerialPort.STOPBITS_1;
        Integer iDevicePLUSerialPortParity = SerialPort.PARITY_NONE;

        StringParser sd = null;
        sd = new StringParser(props.getProperty("machine.pludevice"));

        if (sd == null) {
            sd = new StringParser(props.getProperty("machine.scanner"));
        }

        sDevicePLUType = sd.nextToken(':');
        String sDevicePLUParam1 = sd.nextToken(',');
        // String sScannerParam2 = sd.nextToken(',');
        iDevicePLUSerialPortSpeed = SerialPortParameters.getSpeed(sd.nextToken(','));
        iDevicePLUSerialPortDataBits = SerialPortParameters.getDataBits(sd.nextToken(','));
        iDevicePLUSerialPortStopBits = SerialPortParameters.getStopBits(sd.nextToken(','));
        iDevicePLUSerialPortParity = SerialPortParameters.getParity(sd.nextToken(','));

        if ("scanpal2".equals(sDevicePLUType)) {
            return new DeviceScannerComm(sDevicePLUParam1, iDevicePLUSerialPortSpeed, iDevicePLUSerialPortDataBits, iDevicePLUSerialPortStopBits, iDevicePLUSerialPortParity);
        } else {
            return null;
        }
    }
}
