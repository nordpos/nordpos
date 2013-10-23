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

import java.util.Comparator;

public class ComparatorCreatorBasic implements ComparatorCreator {
    
    private String[] m_sHeaders;
    private Datas[] m_aDatas;
    private int[] m_iAvailableIndexes;
    
    /** Creates a new instance of ComparatorCreatorBasic */
    public ComparatorCreatorBasic(String[] sHeaders, Datas[] aDatas, int[] iAvailableIndexes) {
        
        m_sHeaders = sHeaders;
        m_aDatas = aDatas;
        m_iAvailableIndexes = iAvailableIndexes;
    }
    public ComparatorCreatorBasic(String[] sHeaders, Datas[] aDatas) {
        m_sHeaders = sHeaders;        
        m_aDatas = aDatas;
        m_iAvailableIndexes = new int[aDatas.length];
        for (int i = 0; i < aDatas.length; i++) {
            m_iAvailableIndexes[i] = i;
        }
    }
    
    public String[] getHeaders() {
        
        String[] sTempHeaders = new String[m_iAvailableIndexes.length];
        
        for (int i = 0; i < m_iAvailableIndexes.length; i++) {
            sTempHeaders[i] = m_sHeaders[m_iAvailableIndexes[i]];
        }         
        return sTempHeaders;
    }
    
    public Comparator createComparator(int[] aiOrderBy) {
        return new ComparatorBasic(aiOrderBy);
    }
    
    public class ComparatorBasic implements Comparator {

        private int[] m_aiOrderBy;

        /** Creates a new instance of ComparatorBasic */
        public ComparatorBasic(int[] aiOrderBy) {
            m_aiOrderBy = aiOrderBy;
        }
        public int compare(Object o1, Object o2) {
            if (o1 == null) {
                if (o2 == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (o2 == null) {
                return +1;
            } else {
                // ninguno de los dos es nulo...
                Object[] ao1 = (Object[]) o1;
                Object[] ao2 = (Object[]) o2;
                for (int i = 0; i < m_aiOrderBy.length; i++) {
                    int result = m_aDatas[m_iAvailableIndexes[m_aiOrderBy[i]]].compare(ao1[m_iAvailableIndexes[m_aiOrderBy[i]]], ao2[m_iAvailableIndexes[m_aiOrderBy[i]]]);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        }
    }    
}
