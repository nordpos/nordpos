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

import com.openbravo.basic.BasicException;
import java.util.Date;

/**
 *
 * @author adrianromero
 */
public abstract class DataParams implements DataWrite {
    
    protected DataWrite dw;
    
    public abstract void writeValues() throws BasicException;

    public void setInt(int paramIndex, Integer iValue) throws BasicException {
        dw.setInt(paramIndex, iValue);
    }

    public void setString(int paramIndex, String sValue) throws BasicException {
        dw.setString(paramIndex, sValue);
    }

    public void setDouble(int paramIndex, Double dValue) throws BasicException {
        dw.setDouble(paramIndex, dValue);
    }

    public void setBoolean(int paramIndex, Boolean bValue) throws BasicException {
        dw.setBoolean(paramIndex, bValue);
    }

    public void setTimestamp(int paramIndex, Date dValue) throws BasicException {
        dw.setTimestamp(paramIndex, dValue);
    }

    public void setBytes(int paramIndex, byte[] value) throws BasicException {
        dw.setBytes(paramIndex, value);
    }

    public void setObject(int paramIndex, Object value) throws BasicException {
        dw.setObject(paramIndex, value);
    }

    public DataWrite getDataWrite() {
        return dw;
    }

    public void setDataWrite(DataWrite dw) {
        this.dw = dw;
    }
}
