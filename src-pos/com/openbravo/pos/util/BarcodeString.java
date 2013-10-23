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

package com.openbravo.pos.util;

import java.io.UnsupportedEncodingException;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class BarcodeString {

    public static String getBarcodeStringEAN13(String message) {
        String m_sBarcode = normalizationEANDigit(alignDigitBarCode(message, 13).substring(0, 12));
        return m_sBarcode;
    }

    public static String getBarcodeStringEAN8(String message) {
        String m_sBarcode = normalizationEANDigit(alignDigitBarCode(message, 8).substring(0, 7));
        return m_sBarcode;
    }

    public static String getBarcodeStringCode39(String message) {
        if (message.length() > 255) {
            message.substring(0, 255);
        }
        String m_sBarcode = normalizationCodeDigitAndUpperCharacter(message);
        return m_sBarcode;
    }

    public static String getBarcodeStringCode128(String message) {
        if (message.length() > 255) {
            message.substring(0, 255);
        }
        String m_sBarcode = normalizationCodeDigitAndCharacter(message);
        return m_sBarcode;
    }
    
    public static String getBarcodeStringDataMatrix(String message) {
        String m_sBarcode = "";
        try {
            m_sBarcode = new String(message.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
        }
        return m_sBarcode;
    }        

    public static String getBarcodeStringQRCode(String message) {
        String m_sBarcode = "";
        try {
            m_sBarcode = new String(message.getBytes("utf-8"), "Cp1251");
        } catch (UnsupportedEncodingException ex) {
        }
        return m_sBarcode;
    }
    
    private static String alignDigitBarCode(String sLine, int iSize) {
        if (sLine.length() > iSize) {
            return sLine.substring(0, iSize);
        } else {
            return StringUtils.getWhiteString(iSize - sLine.length(), '0') + sLine;
        }
    }

    private static String normalizationEANDigit(String value) {
        String m_sResult = "";
        if (value == null) {
            return null;
        } else {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c >= '0' && c <= '9') {
                    m_sResult = m_sResult.concat(Character.toString(c));
                } else {
                    m_sResult = m_sResult.concat(Character.toString('0'));
                }
            }
            return m_sResult;
        }
    }

    private static String normalizationCodeDigitAndUpperCharacter(String value) {
        String m_sResult = "";

        if (value == null) {
            return null;
        } else {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c == '$' || c == '%'|| c == '+'|| c == '-'|| c == '.'|| c == '/') {
                    m_sResult = m_sResult.concat(Character.toString(c));
                } else {
                    m_sResult = m_sResult.concat(Character.toString('-'));
                }
            }
            return m_sResult;
        }
    }

    private static String normalizationCodeDigitAndCharacter(String value) {
        String m_sResult = "";

        if (value == null) {
            return null;
        } else {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c >= 0 && c <= 127) {
                    m_sResult = m_sResult.concat(Character.toString(c));
                } else {
                    m_sResult = m_sResult.concat(Character.toString('?'));
                }
            }
            return m_sResult;
        }
    }
}
