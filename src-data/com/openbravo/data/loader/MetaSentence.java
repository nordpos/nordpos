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
import java.util.*; 
import javax.sql.DataSource;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.JDBCSentence.JDBCDataResultSet;

public class MetaSentence extends JDBCSentence {
    
    private String m_sSentence;
    protected SerializerRead m_SerRead = null;
    protected SerializerWrite m_SerWrite = null;

    /** Creates a new instance of MetaDataSentence */
    public MetaSentence(Session s, String sSentence, SerializerWrite serwrite, SerializerRead serread) {
        super(s);
        m_sSentence = sSentence;
        m_SerWrite = serwrite;
        m_SerRead = serread;
    }
    public MetaSentence(Session s, String sSentence, SerializerRead serread) {
        this(s, sSentence, null, serread);
    }
    
    private static class MetaParameter implements DataWrite {

        private ArrayList m_aParams;

        /** Creates a new instance of MetaParameter */
        public MetaParameter() {
            m_aParams = new ArrayList();
        }
        
        public void setDouble(int paramIndex, Double dValue) throws BasicException {
            throw new BasicException(LocalRes.getIntString("exception.noparamtype"));
        }
        public void setBoolean(int paramIndex, Boolean bValue) throws BasicException {
            throw new BasicException(LocalRes.getIntString("exception.noparamtype"));
        }
        public void setInt(int paramIndex, Integer iValue) throws BasicException {
            throw new BasicException(LocalRes.getIntString("exception.noparamtype"));
        }   
        public void setString(int paramIndex, String sValue) throws BasicException {
            ensurePlace(paramIndex - 1);
            m_aParams.set(paramIndex - 1, sValue);
        }
        public void setTimestamp(int paramIndex, java.util.Date dValue) throws BasicException {
            throw new BasicException(LocalRes.getIntString("exception.noparamtype"));
        }
//        public void setBinaryStream(int paramIndex, java.io.InputStream in, int length) throws DataException {
//             throw new DataException("Param type not allowed");
//       }
        public void setBytes(int paramIndex, byte[] value) throws BasicException {
             throw new BasicException(LocalRes.getIntString("exception.noparamtype"));
       }
        public void setObject(int paramIndex, Object value) throws BasicException {
            setString(paramIndex, (value == null) ? null : value.toString());
        }

        public String getString(int index) {
            return (String) m_aParams.get(index);
        }    
        
        private void ensurePlace(int i) {
            m_aParams.ensureCapacity(i);
            while (i >= m_aParams.size()){
                m_aParams.add(null);
            }
        }
    }

    public DataResultSet openExec(Object params) throws BasicException {
        
        closeExec();
        
        try {
            DatabaseMetaData db = m_s.getConnection().getMetaData();

            MetaParameter mp = new MetaParameter();               
            if (params != null) {
                // si m_SerWrite fuera null deberiamos cascar
                m_SerWrite.writeValues(mp, params);  
            }

            // Catalogs Has Schemas Has Objects

            // Lo generico de la base de datos
            if ("getCatalogs".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getCatalogs(), m_SerRead);
            } else if ("getSchemas".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getSchemas(), m_SerRead);
            } else if ("getTableTypes".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getTableTypes(), m_SerRead);
            } else if ("getTypeInfo".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getTypeInfo(), m_SerRead);

            // Los objetos por catalogo, esquema

            // Los tipos definidos por usuario
            } else if ("getUDTs".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getUDTs(mp.getString(0), mp.getString(1), null, null), m_SerRead);      
            } else if ("getSuperTypes".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getSuperTypes(mp.getString(0), mp.getString(1), mp.getString(2)), m_SerRead);      

            // Los atributos
            } else if ("getAttributes".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getAttributes(mp.getString(0), mp.getString(1), null, null), m_SerRead);

            // Las Tablas y sus objetos relacionados
            } else if ("getTables".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getTables(mp.getString(0), mp.getString(1), null, null), m_SerRead);
            } else if ("getSuperTables".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getSuperTables(mp.getString(0), mp.getString(1), mp.getString(2)), m_SerRead);       
            } else if ("getTablePrivileges".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getTablePrivileges(mp.getString(0), mp.getString(1), mp.getString(2)), m_SerRead);
            } else if ("getBestRowIdentifier".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getBestRowIdentifier(mp.getString(0), mp.getString(1), mp.getString(2), 0, true), m_SerRead);
            } else if ("getPrimaryKeys".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getPrimaryKeys(mp.getString(0), mp.getString(1), mp.getString(2)), m_SerRead);
            } else if ("getColumnPrivileges".equals(m_sSentence)) {            
                return new JDBCDataResultSet(db.getColumnPrivileges(mp.getString(0), mp.getString(1), mp.getString(2), null), m_SerRead);
            } else if ("getColumns".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getColumns(mp.getString(0), mp.getString(1), mp.getString(2), null), m_SerRead);
            } else if ("getVersionColumns".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getVersionColumns(mp.getString(0), mp.getString(1), mp.getString(2)), m_SerRead);
            } else if ("getIndexInfo".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getIndexInfo(mp.getString(0), mp.getString(1), mp.getString(2), false, false), m_SerRead);
            } else if ("getExportedKeys".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getExportedKeys(mp.getString(0), mp.getString(1), mp.getString(2)), m_SerRead);
            } else if ("getImportedKeys".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getImportedKeys(mp.getString(0), mp.getString(1), mp.getString(2)), m_SerRead);
            } else if ("getCrossReference".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getCrossReference(mp.getString(0), mp.getString(1), mp.getString(2), null, null, null), m_SerRead);

            // Los procedimientos y sus objetos relacionados
            } else if ("getProcedures".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getProcedures(mp.getString(0), mp.getString(1), null), m_SerRead);
            } else if ("getProcedureColumns".equals(m_sSentence)) {
                return new JDBCDataResultSet(db.getProcedureColumns(mp.getString(0), mp.getString(1), mp.getString(2), null), m_SerRead);

            } else {
                return null;
            }
        } catch (SQLException eSQL) {
            throw new BasicException(eSQL);
        }
    }  
    
    public void closeExec() throws BasicException {
    }
    
    public DataResultSet moreResults() throws BasicException {
        return null;
    }
}
