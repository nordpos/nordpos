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

import java.io.UnsupportedEncodingException;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
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
            message = message.substring(0, 255);
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
