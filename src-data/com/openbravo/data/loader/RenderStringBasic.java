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

/**
 *
 * @author  adrian
 */
public class RenderStringBasic implements IRenderString {
    
    private Formats[] m_aFormats;
    private int[] m_aiIndex;
    
    /** Creates a new instance of StringnizerBasic */
    public RenderStringBasic(Formats[] fmts, int[] aiIndex) {
        m_aFormats = fmts; 
        m_aiIndex = aiIndex;
    }
    public String getRenderString(Object value) {
        
        if (value == null) {
            return null; 
        } else {
            Object [] avalue = (Object[]) value;
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < m_aiIndex.length; i++) {
                if (i > 0) {
                    sb.append(" - ");
                }
                sb.append(m_aFormats[m_aiIndex[i]].formatValue(avalue[m_aiIndex[i]]));
            }
            
            return sb.toString();
        }
    }  
   
}
