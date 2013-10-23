/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.util;

//    Исходный код для Openbravo POS, автоматизированной системы продаж для работы
//    с сенсорным экраном, предоставлен ТОО "Норд-Трейдинг ЛТД", Республика Казахстан,
//    в период 2008-2011 годов на условиях лицензионного соглашения GNU LGPL.
//
//    Исходный код данного файл разработан в рамках проекта Openbravo POS ru
//    для использования системы Openbravo POS на территории бывшего СССР
//    <http://code.google.com/p/openbravoposru/>.
//
//    Openbravo POS является свободным программным обеспечением. Вы имеете право
//    любым доступным образом его распространять и/или модифицировать соблюдая
//    условия изложенные в GNU Lesser General Public License версии 3 и выше.
//
//    Данный исходный распространяется как есть, без каких либо гарантий на его
//    использование в каких либо целях, включая коммерческое применение. Данный
//    исход код может быть использован для связи с сторонними библиотеками
//    распространяемыми под другими лицензионными соглашениями. Подробности
//    смотрите в описании лицензионного соглашение GNU Lesser General Public License.
//
//    Ознакомится с условиями изложенными в GNU Lesser General Public License
//    вы можете в файле lgpl-3.0.txt каталога licensing проекта Openbravo POS ru.
//    А также на сайте <http://www.gnu.org/licenses/>.

import gnu.io.SerialPort;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 */

public class SerialPortParameters {

    private SerialPortParameters() {
    }

    Integer iSerialPortParity = SerialPort.PARITY_NONE;  

    public static int getSpeed(String sSpeedParam) {
        if ("2400".equals(sSpeedParam)) return 2400;
        else if ("4800".equals(sSpeedParam)) return 4800;
        else if ("9600".equals(sSpeedParam)) return 9600;
        else if ("19200".equals(sSpeedParam)) return 19200;
        else if ("38400".equals(sSpeedParam)) return 38400;
        else if ("57600".equals(sSpeedParam)) return 57600;
        else if ("115200".equals(sSpeedParam)) return 115200;
        else return 9600;
    }
    
    public static int getDataBits(String sBitsParam) {
        if ("5".equals(sBitsParam)) return SerialPort.DATABITS_5;
        else if ("6".equals(sBitsParam)) return SerialPort.DATABITS_6;
        else if ("7".equals(sBitsParam)) return SerialPort.DATABITS_7;                
        else if ("8".equals(sBitsParam)) return SerialPort.DATABITS_8;                
        else return SerialPort.DATABITS_8;
    }
    
    public static int getStopBits(String sStopBitsParam) {
        if ("1".equals(sStopBitsParam)) return SerialPort.STOPBITS_1;            
        else if ("2".equals(sStopBitsParam)) return SerialPort.STOPBITS_2;
        else return SerialPort.STOPBITS_1;        
    }
    
    public static int getParity(String sPortParity) {
        if ("none".equals(sPortParity)) return SerialPort.PARITY_NONE;            
        else if ("even".equals(sPortParity)) return SerialPort.PARITY_EVEN;
        else if ("odd".equals(sPortParity)) return SerialPort.PARITY_ODD;                
        else return SerialPort.PARITY_NONE;        
    }
}
