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

import java.sql.*;
import com.openbravo.basic.BasicException;
import java.util.logging.Logger;

/**
 *
 * @author  adrianromero
 */
public class StaticSentence extends JDBCSentence {

    private static Logger logger = Logger.getLogger("com.openbravo.data.loader.StaticSentence");
    
    private ISQLBuilderStatic m_sentence;
    protected SerializerWrite m_SerWrite = null;
    protected SerializerRead m_SerRead = null;

    // Estado
    private Statement m_Stmt;
    
    /** Creates a new instance of StaticSentence */
    public StaticSentence(Session s, ISQLBuilderStatic sentence, SerializerWrite serwrite, SerializerRead serread) {
        super(s);
        m_sentence = sentence;
        m_SerWrite = serwrite;
        m_SerRead = serread;
        m_Stmt = null;
    }    
    /** Creates a new instance of StaticSentence */
    public StaticSentence(Session s, ISQLBuilderStatic sentence) {
        this(s, sentence, null, null);
    }     
    /** Creates a new instance of StaticSentence */
    public StaticSentence(Session s, ISQLBuilderStatic sentence, SerializerWrite serwrite) {
        this(s, sentence, serwrite, null);
    }     
    /** Creates a new instance of StaticSentence */
    public StaticSentence(Session s, String sentence, SerializerWrite serwrite, SerializerRead serread) {
        this(s, new NormalBuilder(sentence), serwrite, serread);
    }
    /** Creates a new instance of StaticSentence */
    public StaticSentence(Session s, String sentence, SerializerWrite serwrite) {
        this(s, new NormalBuilder(sentence), serwrite, null);
    }
    /** Creates a new instance of StaticSentence */
    public StaticSentence(Session s, String sentence) {
        this(s, new NormalBuilder(sentence), null, null);
    }
    
    public DataResultSet openExec(Object params) throws BasicException {
        // true -> un resultset
        // false -> un updatecount (si -1 entonces se acabo)
        
        closeExec();
            
        try {
            m_Stmt = m_s.getConnection().createStatement();

            String sentence = m_sentence.getSQL(m_SerWrite, params);
            
            logger.info("Executing static SQL: " + sentence);

            if (m_Stmt.execute(sentence)) {
                return new JDBCDataResultSet(m_Stmt.getResultSet(), m_SerRead);
            } else { 
                int iUC = m_Stmt.getUpdateCount();
                if (iUC < 0) {
                    return null;
                } else {
                    return new SentenceUpdateResultSet(iUC);
                }
            }
        } catch (SQLException eSQL) {
            throw new BasicException(eSQL);
        }
    }
    
    public void closeExec() throws BasicException {
        
        if (m_Stmt != null) {
            try {
                m_Stmt.close();
           } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            } finally {
                m_Stmt = null;
            }
        }
    }
    
    public DataResultSet moreResults() throws BasicException {

        try {
            if (m_Stmt.getMoreResults()){
                // tenemos resultset
                return new JDBCDataResultSet(m_Stmt.getResultSet(), m_SerRead);
            } else {
                // tenemos updatecount o si devuelve -1 ya no hay mas
                int iUC = m_Stmt.getUpdateCount();
                if (iUC < 0) {
                    return null;
                } else {
                    return new SentenceUpdateResultSet(iUC);
                }
            }
        } catch (SQLException eSQL) {
            throw new BasicException(eSQL);
        }
    }    
    
}
