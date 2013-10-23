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

package com.openbravo.data.gui;

import java.util.regex.*;
import com.openbravo.data.loader.Vectorer;
import com.openbravo.basic.BasicException;
import com.openbravo.data.user.Finder;

public class FindInfo implements Finder {
    
    public static final int MATCH_STARTFIELD = 0;
    public static final int MATCH_WHOLEFIELD = 1;
    public static final int MATCH_ANYPARTFIELD = 2;
    public static final int MATCH_REGEXP = 3;
    
    private String m_sTextCompare;
    private Pattern m_TextPattern;
    
    private String m_sText; // Texto a buscar
    private int m_iField;   // Campo de busqueda
    private int m_iMatch;   // Tipo de busqueda
    private boolean m_bMatchCase; // Mayusculas / Minusculas
    
    private Vectorer m_vec;
    
    /** Creates a new instance of FindInfo */
    public FindInfo(Vectorer vec, String sText, int iField, boolean bMatchCase, int iMatch) {
        m_vec = vec;
        m_sText = sText;
        m_iField = iField;
        m_bMatchCase = bMatchCase;
        m_iMatch = iMatch;
        
        if (iMatch == MATCH_REGEXP) {          
            m_TextPattern = m_bMatchCase 
                ? Pattern.compile(m_sText) 
                : Pattern.compile(m_sText, Pattern.CASE_INSENSITIVE);
        } else {
            m_sTextCompare = m_bMatchCase
                ? m_sText
                : m_sText.toUpperCase();
        }
    }
    
    /** Creates a new instance of FindInfo */
    public FindInfo(Vectorer vec) {
        this(vec,  "", 0, true, MATCH_ANYPARTFIELD);
    }
    
    public Vectorer getVectorer() {
        return m_vec;
    }
    public String getText() {
        return m_sText;
    }
    public int getField() {
        return m_iField;
    }
    public boolean isMatchCase() {
        return m_bMatchCase;
    }
    public int getMatch() {
        return m_iMatch;
    }
   
    public boolean match(Object obj) throws BasicException {
        
        String[] v = m_vec.getValues(obj);
        
        String sField = m_bMatchCase
            ? v[m_iField]
            : v[m_iField].toUpperCase();
        
        switch (m_iMatch) {
        case MATCH_STARTFIELD:
            return sField.startsWith(m_sTextCompare);
        case MATCH_WHOLEFIELD:
            return sField.equals(m_sTextCompare);
        case MATCH_ANYPARTFIELD:   
            return sField.indexOf(m_sTextCompare) >= 0;
        case MATCH_REGEXP:
            return m_TextPattern.matcher(sField).matches();
        default:
            return false;
        }       
    }
    
}
