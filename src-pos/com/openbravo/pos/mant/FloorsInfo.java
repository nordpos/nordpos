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

package com.openbravo.pos.mant;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.SerializableRead;

/**
 *
 * @author adrianromero
 * Created on 26 de febrero de 2007, 23:49
 *
 */
public class FloorsInfo implements SerializableRead, IKeyed {
    
    private static final long serialVersionUID = 8906929819402L;
    private String m_sID;
    private String m_sName;
    
    /** Creates a new instance of FloorsInfo */
    public FloorsInfo() {
        m_sID = null;
        m_sName = null;
    }
   
    public Object getKey() {
        return m_sID;
    }
    
    public void readValues(DataRead dr) throws BasicException {
        m_sID = dr.getString(1);
        m_sName = dr.getString(2);
    } 
    
    public void setID(String sID) {
        m_sID = sID;
    }
    
    public String getID() {
        return m_sID;
    }

    public String getName() {
        return m_sName;
    }
    
    public void setName(String sName) {
        m_sName = sName;
    } 
    
    public String toString(){
        return m_sName;
    }       
}
