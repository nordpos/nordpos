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

package com.openbravo.format;

import java.text.ParseException;

public class FormatsValidate extends Formats {
    
    private Formats m_fmt;
    private FormatsConstrain[] m_aConstrains;
    
    /** Creates a new instance of FormatsValidate */
    public FormatsValidate(Formats fmt, FormatsConstrain[] constrains) {
        m_fmt = fmt;
        m_aConstrains = constrains;
    }
    /** Creates a new instance of FormatsValidate */
    public FormatsValidate(Formats fmt) {
        this(fmt, new FormatsConstrain[0]);
    }
    /** Creates a new instance of FormatsValidate */
    public FormatsValidate(Formats fmt, FormatsConstrain constrain) {
        this(fmt, new FormatsConstrain[]{constrain});
    }
    
    protected String formatValueInt(Object value) {
        return m_fmt.formatValueInt(value);
    }
    protected Object parseValueInt(String value) throws ParseException {
        // Primero obtenemos el valor        
        Object val = m_fmt.parseValueInt(value);        
        for (int i = 0; i < m_aConstrains.length; i++) {
            val = m_aConstrains[i].check(val);
        }
        
        return val;
    }
    public int getAlignment() {
        return m_fmt.getAlignment();
    }
}
