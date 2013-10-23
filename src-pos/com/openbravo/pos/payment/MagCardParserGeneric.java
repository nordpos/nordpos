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

package com.openbravo.pos.payment;

import com.openbravo.pos.util.LuhnAlgorithm;
import com.openbravo.pos.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adrian
 */
public class MagCardParserGeneric implements MagCardParser {

    private String m_sHolderName;
    private String m_sCardNumber;
    private String m_sExpirationDate;
    private StringBuffer track1;
    private StringBuffer track2;
    private StringBuffer track3;

    private static final int READING_STARTSENTINEL1 = 0;
    private static final int READING_STARTSENTINEL2 = 1;
    private static final int READING_STARTSENTINEL3 = 2;
    private static final int READING_CARDTYPE = 3;
    private static final int READING_TRACK1 = 4;
    private static final int READING_TRACK2 = 5;
    private static final int READING_TRACK3 = 6;
    private static final int READING_END = 7;
    private int m_iAutomState;

    private List m_aTrack1;
    private List m_aTrack2;
    private List m_aTrack3;
    private StringBuffer m_sField;
    private char m_cCardType;

    /** Creates a new instance of GenericMagCardReader */
    public MagCardParserGeneric() {
        reset();
    }

    @Override
    public void reset() {
        m_aTrack1 = null;
        m_aTrack2 = null;
        m_aTrack3 = null;
        m_sField = null;
        m_cCardType = ' ';

        m_sHolderName = null;
        m_sCardNumber = null;
        m_sExpirationDate = null;
        m_iAutomState = READING_STARTSENTINEL1;
    }

    @Override
    public void append(char c) {
        // Mastercard
        // %B1111222233334444^PUBLIC/JOHN?;1111222233334444=99121010000000000000?
        //  *---------------- -----------                   ----***
        // Visa
        // %B1111222233334444^PUBLIC/JOHN^9912101xxxxxxxxxxxxx?;1111222233334444=9912101xxxxxxxxxxxxx?
        //  *---------------- ----------- ----                                       ***

        if (c == '%') { // && READING_STARTSENTINEL1;
            track1 = new StringBuffer();
            track2 = new StringBuffer();
            track3 = new StringBuffer();
            m_aTrack1 = new ArrayList();
            m_aTrack2 = null;
            m_aTrack3 = null;
            m_sField = new StringBuffer();
            m_cCardType = ' ';
            m_sHolderName = null;
            m_sCardNumber = null;
            m_sExpirationDate = null;
            m_iAutomState = READING_CARDTYPE;
        } else if (m_iAutomState == READING_CARDTYPE) {
            m_cCardType = c;
            m_iAutomState = READING_TRACK1;
        } else if (c == ';' && m_iAutomState == READING_STARTSENTINEL2) {
            m_aTrack2 = new ArrayList();
            m_sField = new StringBuffer();
            m_iAutomState = READING_TRACK2;
        } else if (c == ';' && m_iAutomState == READING_STARTSENTINEL3) {
            m_aTrack3 = new ArrayList();
            m_sField = new StringBuffer();
            m_iAutomState = READING_TRACK3;

        } else if (c == '^' && m_iAutomState == READING_TRACK1) {
            m_aTrack1.add(m_sField.toString());
            m_sField = new StringBuffer();
        } else if (c == '=' && m_iAutomState == READING_TRACK2) {
            m_aTrack2.add(m_sField.toString());
            m_sField = new StringBuffer();
        } else if (c == '=' && m_iAutomState == READING_TRACK3) {
            m_aTrack3.add(m_sField.toString());
            m_sField = new StringBuffer();

        } else if (c == '?' && m_iAutomState == READING_TRACK1) {
            m_aTrack1.add(m_sField.toString());
            m_sField = null;
            m_iAutomState = READING_STARTSENTINEL2;
        } else if (c == '?' && m_iAutomState == READING_TRACK2) {
            m_aTrack2.add(m_sField.toString());
            m_sField = null;
            m_iAutomState = READING_STARTSENTINEL3;
            checkTracks(); // aqui ya chequeamos los paramentros que leemos...
        } else if (c == '?' && m_iAutomState == READING_TRACK3) {
            m_aTrack3.add(m_sField.toString());
            m_sField = null;
            m_iAutomState = READING_END;

        } else if (m_iAutomState == READING_TRACK1 || m_iAutomState == READING_TRACK2 || m_iAutomState == READING_TRACK3) {
            m_sField.append(c);
        }


        if (m_iAutomState == READING_CARDTYPE || m_iAutomState == READING_TRACK1 || m_iAutomState == READING_STARTSENTINEL2) {
            track1.append(c);
        } else if (m_iAutomState == READING_TRACK2 || m_iAutomState == READING_STARTSENTINEL3) {
            track2.append(c);
        } else if (m_iAutomState == READING_TRACK3 || m_iAutomState == READING_END) {
            track3.append(c);
        }
    }

    private void checkTracks() {

        // Test del tipo de tarjeta
        if (m_cCardType != 'B') return;

        // Lectura de los valores
        String sCardNumber1 = (m_aTrack1 == null || m_aTrack1.size() < 1) ? null : (String) m_aTrack1.get(0);
        String sCardNumber2 = (m_aTrack2 == null || m_aTrack2.size() < 1) ? null : (String) m_aTrack2.get(0);
        String sHolderName = (m_aTrack1 == null || m_aTrack1.size() < 2) ? null : (String) m_aTrack1.get(1);
        String sExpDate1 =  (m_aTrack1 == null || m_aTrack1.size() < 3) ? null : ((String) m_aTrack1.get(2)).substring(0, 4);
        String sExpDate2 =  (m_aTrack2 == null || m_aTrack2.size() < 2) ? null : ((String) m_aTrack2.get(1)).substring(0, 4);

        // Test del numero de tarjeta
        if (!checkCardNumber(sCardNumber1) || (sCardNumber2 != null && !sCardNumber1.equals(sCardNumber2))) return;
        // Test del nombre del propietario
        if (sHolderName == null) return;
        // Test de la fecha de expiracion
        if ((sExpDate1 != null || !checkExpDate(sExpDate2)) && (!checkExpDate(sExpDate1) || !sExpDate1.equals(sExpDate2))) return;

        m_sCardNumber = sCardNumber1;
        m_sHolderName = formatHolderName(sHolderName);
        String yymm = sExpDate1 == null ? sExpDate2 : sExpDate1;
        m_sExpirationDate = yymm.substring(2, 4) + yymm.substring(0, 2); //MMYY format
    }

    private boolean checkCardNumber(String sNumber) {
        return LuhnAlgorithm.checkCC(sNumber);
    }

    private boolean checkExpDate(String sDate) {
        return ( sDate.length()==4 && StringUtils.isNumber(sDate.trim()) );
    }

    private String formatHolderName(String sName) {

        int iPos = sName.indexOf('/');
        if (iPos >= 0) {
            return sName.substring(iPos + 1).trim() + ' ' + sName.substring(0, iPos);
        } else {
            return sName.trim();
        }
    }

    @Override
    public boolean isComplete() {
        return m_sCardNumber != null;
    }
    @Override
    public String getHolderName() {
        return m_sHolderName;
    }
    @Override
    public String getCardNumber() {
        return m_sCardNumber;
    }
    @Override
    public String getExpirationDate() {
        return m_sExpirationDate;
    }
    @Override
    public String getTrack1() {
        return track1 == null ? null : track1.toString();
    }
    @Override
    public String getTrack2() {
        return track2 == null ? null : track2.toString();
    }
    @Override
    public String getTrack3() {
        return track3 == null ? null : track3.toString();
    }
}
