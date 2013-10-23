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

package com.openbravo.data.loader;

import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;

public class VectorerBasic implements Vectorer {
    
    private int[] m_aiIndex;
    private String[] m_asHeaders;
    private Formats[] m_aFormats;
    
    public VectorerBasic(String[] asHeaders, Formats[] aFormats, int[] aiIndex) {
        m_asHeaders = asHeaders;
        m_aFormats = aFormats;
        m_aiIndex = aiIndex;
    }
      
    public String[] getHeaders() throws BasicException {
        
        String[] asvalues = new String[m_aiIndex.length];
        for (int i = 0; i < m_aiIndex.length; i++) {
            asvalues[i] = m_asHeaders[m_aiIndex[i]];
        }
        
        return asvalues;
    }
    
    public String[] getValues(Object obj) throws BasicException {
        Object[] avalues = (Object[]) obj;
        String[] asvalues = new String[m_aiIndex.length];
        for (int i = 0; i < m_aiIndex.length; i++) {
            asvalues[i] = m_aFormats[m_aiIndex[i]].formatValue(avalues[m_aiIndex[i]]);
        }
        
        return asvalues;
    }    
}
