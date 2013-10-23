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


public class SequenceForDerby extends BaseSentence {

    private BaseSentence sent1;
    private BaseSentence sent2;
    private BaseSentence sent3;

    /** Creates a new instance of SequenceForMySQL */
    public SequenceForDerby(Session s, String sSeqTable) {

        sent1 = new StaticSentence(s, "DELETE FROM  " + sSeqTable);
        sent2 = new StaticSentence(s, "INSERT INTO " + sSeqTable + " VALUES (DEFAULT)");
        sent3 = new StaticSentence(s, "SELECT IDENTITY_VAL_LOCAL() FROM " + sSeqTable, null, SerializerReadInteger.INSTANCE);
    }

    // Funciones de bajo nivel
    public DataResultSet openExec(Object params) throws BasicException {
        sent1.exec();
        sent2.exec();
        return sent3.openExec(null);
    }
    public DataResultSet moreResults() throws BasicException {
        return sent3.moreResults();
    }
    public void closeExec() throws BasicException {
        sent3.closeExec();
    }
}
