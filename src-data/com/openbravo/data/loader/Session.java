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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author adrianromero
 * Created on February 6, 2007, 4:06 PM
 *
 */
public class Session {
    
    private String m_surl;
    private String m_suser;
    private String m_spassword;
    
    private Connection m_c;
    private boolean m_bInTransaction;

    public final SessionDB DB;
    
    /** Creates a new instance of Session */
    public Session(String url, String user, String password) throws SQLException {
        m_surl = url;
        m_suser = user;
        m_spassword = password;
        
        m_c = null;
        m_bInTransaction = false;
        
        connect(); // no lazy connection

        DB = getDiff();
    }
    
    public void connect() throws SQLException {
        
        // primero cerramos si no estabamos cerrados
        close();
        
        // creamos una nueva conexion.
        m_c = (m_suser == null && m_spassword == null)
        ? DriverManager.getConnection(m_surl)
        : DriverManager.getConnection(m_surl, m_suser, m_spassword);         
        m_c.setAutoCommit(true);
        m_bInTransaction = false;
    }     

    public void close() {
        
        if (m_c != null) {
            try {
                if (m_bInTransaction) {
                    m_bInTransaction = false; // lo primero salimos del estado
                    m_c.rollback();
                    m_c.setAutoCommit(true);  
                }            
                m_c.close();
            } catch (SQLException e) {
                // me la como
            } finally {
                m_c = null;
            }
        }
    }
    
    public Connection getConnection() throws SQLException {
        
        if (!m_bInTransaction) {
            ensureConnection();
        }
        return m_c;
    }
    
    public void begin() throws SQLException {
        
        if (m_bInTransaction) {
            throw new SQLException("Already in transaction");
        } else {
            ensureConnection();
            m_c.setAutoCommit(false);
            m_bInTransaction = true;
        }
    }
    public void commit() throws SQLException {
        if (m_bInTransaction) {
            m_bInTransaction = false; // lo primero salimos del estado
            m_c.commit();
            m_c.setAutoCommit(true);          
        } else {
            throw new SQLException("Transaction not started");
        }
    }
    public void rollback() throws SQLException {
        if (m_bInTransaction) {
            m_bInTransaction = false; // lo primero salimos del estado
            m_c.rollback();
            m_c.setAutoCommit(true);            
        } else {
            throw new SQLException("Transaction not started");
        }
    }
    public boolean isTransaction() {
        return m_bInTransaction;
    }
    
    private void ensureConnection() throws SQLException {
        // solo se invoca si isTransaction == false
        
        boolean bclosed;
        try {
            bclosed = m_c == null || m_c.isClosed();
        } catch (SQLException e) {
            bclosed = true;
        }

        // reconnect if closed
        if (bclosed) {
            connect();
        }
    }  

    public String getURL() throws SQLException {
        return getConnection().getMetaData().getURL();
    }

    private SessionDB getDiff() throws SQLException {

        String sdbmanager = getConnection().getMetaData().getDatabaseProductName();

        if ("HSQL Database Engine".equals(sdbmanager)) {
            return new SessionDBHSQLDB();
        } else if ("MySQL".equals(sdbmanager)) {
            return new SessionDBMySQL();
        } else if ("PostgreSQL".equals(sdbmanager)) {
            return new SessionDBPostgreSQL();
        } else if ("Oracle".equals(sdbmanager)) {
            return new SessionDBOracle();
        } else if ("Apache Derby".equals(sdbmanager)) {
            return new SessionDBDerby();
        } else {
            return new SessionDBGeneric(sdbmanager);
        }
    }
}
