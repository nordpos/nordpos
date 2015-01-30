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

    private final BaseSentence sent1;
    private final BaseSentence sent2;
    private final BaseSentence sent3;
    private final BaseSentence sent4;
    private final BaseSentence sent5;

    public SequenceForDerby(Session s, String sSeqTable) {
        sent1 = new StaticSentence(s, "SELECT MAX(ID) FROM " + sSeqTable, null, SerializerReadInteger.INSTANCE);
        sent2 = new StaticSentence(s, "ALTER TABLE " + sSeqTable + " ALTER COLUMN ID RESTART WITH ?", SerializerWriteInteger.INSTANCE, null);
        sent3 = new StaticSentence(s, "DELETE FROM " + sSeqTable);
        sent4 = new StaticSentence(s, "INSERT INTO " + sSeqTable + " VALUES (DEFAULT)");
        sent5 = new StaticSentence(s, "SELECT ID FROM " + sSeqTable, null, SerializerReadInteger.INSTANCE);
    }

    // Funciones de bajo nivel
    @Override
    public DataResultSet openExec(Object params) throws BasicException {
        int i = (Integer) sent1.find();
        sent2.exec(i + 1);
        sent3.exec();
        sent4.exec();
        return sent5.openExec(null);
    }

    @Override
    public DataResultSet moreResults() throws BasicException {
        return sent3.moreResults();
    }

    @Override
    public void closeExec() throws BasicException {
        sent3.closeExec();
    }
}
