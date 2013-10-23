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

package com.openbravo.pos.inventory;

import com.openbravo.data.loader.IKeyed;

/**
 *
 * @author adrianromero
 */
public class CodeType implements IKeyed {
    
    public static final CodeType EAN13 = new CodeType("EAN13", "EAN13");
    public static final CodeType CODE128 = new CodeType("CODE128", "CODE128");

    protected String m_sKey;
    protected String m_sValue;
    
    private CodeType(String key, String value) {
        m_sKey = key;
        m_sValue = value;
    }
    public Object getKey() {
        return m_sKey;
    }
    public String getValue() {
        return m_sValue;
    }
    public String toString() {
        return m_sValue;
    }   
}