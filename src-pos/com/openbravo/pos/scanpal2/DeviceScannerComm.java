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

package com.openbravo.pos.scanpal2;

import gnu.io.*;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import com.openbravo.pos.util.StringUtils;

public class DeviceScannerComm implements DeviceScanner, SerialPortEventListener {

    private CommPortIdentifier m_PortIdPrinter;
    private SerialPort m_CommPortPrinter;  
    
    private String m_sPort;
    private OutputStream m_out;
    private InputStream m_in;
    
    private static final byte[] COMMAND_READ = new byte[] {0x52, 0x45, 0x41, 0x44};
    private static final byte[] COMMAND_CIPHER = new byte[] {0x43, 0x49, 0x50, 0x48, 0x45, 0x52};
    private static final byte[] COMMAND_OVER = new byte[] {0x4F, 0x56, 0x45, 0x52};
    private static final byte[] COMMAND_ACK = new byte[] {0x41, 0x43, 0x4B};
    
    private Queue<byte[]> m_aLines;
    private ByteArrayOutputStream m_abuffer;
    private int m_iStatus;
//    private static final int STATUS_WAITING = 0;
//    private static final int STATUS_LINEREADY = 1;
//    private static final int STATUS_READING = 2;
    
    private int m_iProductOrder;
    
    /** Creates a new instance of ScanDeviceComm */
    DeviceScannerComm(String sPort) {
        m_sPort = sPort;
        
        m_PortIdPrinter = null;
        m_CommPortPrinter = null;
        m_out = null;
        m_in = null;
    }

    public void connectDevice() throws DeviceScannerException {    
        
        try {
            // Conecto con el puerto
            m_PortIdPrinter = CommPortIdentifier.getPortIdentifier(m_sPort); // Tomamos el puerto                   
            m_CommPortPrinter = (SerialPort) m_PortIdPrinter.open("PORTID", 2000); // Abrimos el puerto       

            m_out = m_CommPortPrinter.getOutputStream(); // Tomamos el chorro de escritura   
            m_in = m_CommPortPrinter.getInputStream();

            m_CommPortPrinter.addEventListener(this);
            m_CommPortPrinter.notifyOnDataAvailable(true);

            m_CommPortPrinter.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE); // Configuramos el puerto
//        } catch (NoSuchPortException e) {
//            e.printStackTrace();
//        } catch (PortInUseException e) {
//            e.printStackTrace();
//        } catch (UnsupportedCommOperationException e) {
//            e.printStackTrace();
//        } catch (TooManyListenersException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        } catch (Exception e) {
            m_PortIdPrinter = null;
            m_CommPortPrinter = null;
            m_out = null;
            m_in = null;
            throw new DeviceScannerException(e);
        }         
        
        synchronized(this) {
            // m_iStatus = STATUS_WAITING;
            m_aLines = new LinkedList<byte[]>();
            m_abuffer = new ByteArrayOutputStream();
        }       
    }
    
    public void disconnectDevice() {
        
        try {
            m_out.close();
            m_in.close();
            m_CommPortPrinter.close();
        } catch (IOException e) {
        } 
        
        synchronized(this) {
            // m_iStatus = STATUS_WAITING;
            m_aLines = null;
            m_abuffer = null;
        }

        m_PortIdPrinter = null;
        m_CommPortPrinter = null;
        m_out = null;
        m_in = null;      
    }
    
    public void startDownloadProduct() throws DeviceScannerException {
        writeLine(COMMAND_READ); // writeLine(COMMAND_READ);
        readCommand(COMMAND_ACK);
    }
    public ProductDownloaded recieveProduct() throws DeviceScannerException {
        byte[] line = readLine();
        if (checkCommand(COMMAND_OVER, line)) {
            // La Scanpal a terminado.
            return null;
        } else {
            // procesamos la linea
            ProductDownloaded p = new ProductDownloaded();
            try {
                String sLine = new String(line, 1, line.length - 3, "ISO-8859-1");
                StringTokenizer T = new StringTokenizer(sLine, "|");
                while (T.hasMoreTokens()) {
                    String sToken = T.nextToken();
                    if (sToken.startsWith("IEAN")) {
                        p.setCode(sToken.substring(4).trim());
                    } else if (sToken.startsWith("ICANT")) {
                        try {
                            p.setQuantity(Double.parseDouble(sToken.substring(5).trim()));
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
            }
            writeLine(COMMAND_ACK);
            return p;
        }
    }
    
    public void startUploadProduct() throws DeviceScannerException {
        // Inicializamos la conversacion
        writeLine(COMMAND_CIPHER);
        readCommand(COMMAND_ACK);      
        m_iProductOrder = 0;
    }
    public void sendProduct(String sName, String sCode, Double dPrice) throws DeviceScannerException {
        
        m_iProductOrder++;
        
        ByteArrayOutputStream lineout = new ByteArrayOutputStream();
        try {
            lineout.write(convert(Integer.toString(m_iProductOrder)));
            lineout.write(0x7c); // El Pipe "|"
            lineout.write(convert(sName));
            lineout.write(0x7c); // El Pipe "|"
            lineout.write(convert(sCode));
            lineout.write(0x7c); // El Pipe "|"
            lineout.write(0x7c); // El Pipe "|"
            lineout.write(0x7c); // El Pipe "|"
            lineout.write(0x7c); // El Pipe "|"
            lineout.write(convert(dPrice.toString()));
            lineout.write(0x7c); // El Pipe "|"
            // Mandamos el checksum
            lineout.write(calcCheckSum1(lineout.toByteArray()));
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
        
        writeLine(lineout.toByteArray());
        readCommand(COMMAND_ACK);
    }
    public void stopUploadProduct() throws DeviceScannerException {
        // Cerramos la conversacion
        writeLine(COMMAND_OVER);
        readCommand(COMMAND_ACK);
    }
    
    private void readCommand(byte[] cmd) throws DeviceScannerException {
        byte[] b = readLine();
        if (!checkCommand(cmd, b)) {
            // excepcion que te crio.
            throw new DeviceScannerException("Command not expected");
        }
    }
    
    private void writeLine(byte[] aline) throws DeviceScannerException {
        
        if (m_CommPortPrinter == null) {
            throw new DeviceScannerException("No Serial port opened");
        } else {
        
            synchronized(this) {               
                try {
                    m_out.write(aline);
                    m_out.write(0x0D);
                    m_out.flush();
                } catch (IOException e) {
                    throw new DeviceScannerException(e);
                }
            }
        }
    }
    
    private byte[] readLine() throws DeviceScannerException {
        
        synchronized (this) {
            
            if (!m_aLines.isEmpty()) {              
                return m_aLines.poll();
            }
           
            // esperamos un ratito
            try {
                wait(1000);
            } catch (InterruptedException e) {
            }  
            
            if (m_aLines.isEmpty()) {  
                throw new DeviceScannerException("Timeout");
            } else {
                return m_aLines.poll();
            }            
        }
    }
    
    private byte[] convert(String sdata) {
        
        // return sdata.getBytes("ISO-8859-1");
        if (sdata == null) {
            return new byte[0];
        } else {
            byte[] result = new byte[sdata.length()];

            for (int i = 0; i < sdata.length(); i++) {
                char c = sdata.charAt(i);
                if (c == 0x7c) { // El pipe |
                    result[i] = '.';
                } else if ((c >= 0x0020) && (c < 0x0080)) {
                    result[i] = (byte) c;
                } else {
                    result[i] = ' ';
                }
            }
            return result;
        }
    }
    
    private byte[] calcCheckSum1(byte[] adata) {
        
        int isum = 0;
        for (int i = 0; i < adata.length; i++) {
            isum += adata[i];
        }
        
        byte high = (byte) ((isum & 0xFF00) >> 8);
        if (high == 0x0D) high = 0x0E;
        byte low = (byte) (isum & 0x00FF);
        if (low == 0x0D) low = 0x0E;
        
        byte[] result = new byte[2];
        result[0] = high;
        result[1] = low;
        return result;
    }
    
    private boolean checkCommand(byte[] bcommand, byte[] brecieved) {
       
        if (bcommand.length == brecieved.length) {
            for (int i = 0; i < bcommand.length; i++) {
                if (bcommand[i] != brecieved[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false; 
        }      
    }
    
    public void serialEvent(SerialPortEvent e) {

	// Determine type of event.
	switch (e.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                try {
                    while (m_in.available() > 0) {
                        int b = m_in.read();
                        synchronized(this) {
                            if (b == 0x0D) {
                                m_aLines.add(m_abuffer.toByteArray());
                                m_abuffer.reset();
                                notifyAll();
                            } else {
                                m_abuffer.write(b);
                            }                                                             
                        }
                    }
                } catch (IOException eIO) {}
                break;
        }
    }
}
