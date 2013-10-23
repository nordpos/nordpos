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

package com.openbravo.pos.reports;

import java.util.HashMap;
import java.util.Map;
import com.openbravo.pos.forms.AppLocal;

public class ReportFieldsArray implements ReportFields {
    
    private Map m_keys = null;
    
    /** Creates a new instance of ReportFieldsArray */
    public ReportFieldsArray(String[] afields) {
               
        // Creo el mapa de claves
        m_keys = new HashMap();
        for (int i = 0; i < afields.length; i++) {
            m_keys.put(afields[i], new Integer(i));
        }
    }
    
    public Object getField(Object record, String field) throws ReportException {
        
        Integer i = (Integer) m_keys.get(field);
        if (i == null) {
            throw new ReportException(AppLocal.getIntString("exception.unavailablefield", new Object[] {field}));
        } else {
            Object[] arecord = (Object[]) record;
            if (arecord == null || i.intValue() < 0 || i.intValue() >= arecord.length) {
                throw new ReportException(AppLocal.getIntString("exception.unavailablefields"));
            } else {
                return arecord[i.intValue()];
            }
        }        
    }
}
