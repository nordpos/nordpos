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

package com.openbravo.data.user;
import com.openbravo.data.loader.TableDefinition;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.SentenceExec;

public class SaveProvider {
    
    protected SentenceExec m_sentupdate;
    protected SentenceExec m_sentinsert;
    protected SentenceExec m_sentdelete;
    
    /** Creates a new instance of SavePrSentence */
    public SaveProvider(SentenceExec sentupdate, SentenceExec sentinsert, SentenceExec sentdelete) {
        m_sentupdate = sentupdate;
        m_sentinsert = sentinsert;
        m_sentdelete = sentdelete;
    }
    public SaveProvider(TableDefinition table) {
        m_sentupdate = table.getUpdateSentence();
        m_sentdelete = table.getDeleteSentence();
        m_sentinsert = table.getInsertSentence();
    }
    public SaveProvider(TableDefinition table, int[] fields) {
        m_sentupdate = table.getUpdateSentence(fields);
        m_sentdelete = table.getDeleteSentence();
        m_sentinsert = table.getInsertSentence(fields);
    }
    
    public boolean canDelete() {
        return m_sentdelete != null;      
    }

    public int deleteData(Object value) throws BasicException {
        return m_sentdelete.exec(value);
    }
    
    public boolean canInsert() {
        return m_sentinsert != null;          
    }
    
    public int insertData(Object value) throws BasicException {
        return m_sentinsert.exec(value);
    }
    
    public boolean canUpdate() {
        return m_sentupdate != null;      
    }    
    
    public int updateData(Object value) throws BasicException {
        return m_sentupdate.exec(value);
    }
}
