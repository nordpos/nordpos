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

/**
 *
 * @author adrianromero
 * Created on February 6, 2007, 4:06 PM
 *
 */
public abstract class SentenceExecTransaction implements SentenceExec {
    
    private Session m_s;
    
    public SentenceExecTransaction(Session s) {
        m_s = s;
    }
    
    public final int exec() throws BasicException {
        return exec((Object) null);
    }

    public final int exec(Object... params) throws BasicException {
        return exec((Object) params);
    }

    public final int exec(final Object params) throws BasicException {
        
        Transaction<Integer> t = new Transaction<Integer>(m_s) {
            public Integer transact() throws BasicException{
                return execInTransaction(params);
            }
        };
        
        return t.execute();
    }
    
    protected abstract int execInTransaction(Object params) throws BasicException; 
}

