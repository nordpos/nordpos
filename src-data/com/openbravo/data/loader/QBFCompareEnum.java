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

/**
 *
 * @author  adrian
 */
public abstract class QBFCompareEnum {
    
    public final static QBFCompareEnum COMP_NONE = new QBFCompareEnum(0, "qbf.none") { 
        public String getExpression(String sField, String sSQLValue) { return null; }
    };
    public final static QBFCompareEnum COMP_ISNULL = new QBFCompareEnum(1, "qbf.null") {
        public String getExpression(String sField, String sSQLValue) { return sField + " IS NULL"; }
    };
    public final static QBFCompareEnum COMP_ISNOTNULL = new QBFCompareEnum(2, "qbf.notnull") {
        public String getExpression(String sField, String sSQLValue) { return sField + " IS NOT NULL"; }
    };
    public final static QBFCompareEnum COMP_RE = new QBFCompareEnum(3, "qbf.re") {
        public String getExpression(String sField, String sSQLValue) { return sField + " LIKE " + sSQLValue; }
    };
    public final static QBFCompareEnum COMP_EQUALS = new QBFCompareEnum(3, "qbf.equals") {
        public String getExpression(String sField, String sSQLValue) { return sField + " = " + sSQLValue; }
    };
    public final static QBFCompareEnum COMP_DISTINCT = new QBFCompareEnum(4, "qbf.distinct") {
        public String getExpression(String sField, String sSQLValue) { return sField + " <> " + sSQLValue; }
    };
    public final static QBFCompareEnum COMP_GREATER = new QBFCompareEnum(5, "qbf.greater") {
        public String getExpression(String sField, String sSQLValue) { return sField + " > " + sSQLValue; }
    };
    public final static QBFCompareEnum COMP_LESS = new QBFCompareEnum(6, "qbf.less") {
        public String getExpression(String sField, String sSQLValue) { return sField + " < " + sSQLValue; }
    };
    public final static QBFCompareEnum COMP_GREATEROREQUALS = new QBFCompareEnum(7, "qbf.greaterequals") {
        public String getExpression(String sField, String sSQLValue) { return sField + " >= " + sSQLValue; }
    };
    public final static QBFCompareEnum COMP_LESSOREQUALS = new QBFCompareEnum(8, "qbf.lessequals") {
        public String getExpression(String sField, String sSQLValue) { return sField + " <= " + sSQLValue; }
    };
//    public final static QBFCompareEnum COMP_STARTSWITH = new QBFCompareEnum(9, "qbf.startswith") {
//        public String getExpression(String sField, String sSQLValue) { return sField + " LIKE " ... + sSQLValue; }
//    };
//    public final static int COMP_ENDSWITH = 12;
//    public final static int COMP_CONTAINS = 13;    
    
    private int m_iValue; 
    private String m_sKey;
    
    private QBFCompareEnum(int iValue, String sKey) {
        m_iValue = iValue;
        m_sKey = sKey;
    }
    public int getCompareInt() {
        return m_iValue;
    }
    public String toString() {
        return LocalRes.getIntString(m_sKey);
    }
    public abstract String getExpression(String sField, String sSQLValue) ;
}
