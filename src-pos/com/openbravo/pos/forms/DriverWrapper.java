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

package com.openbravo.pos.forms;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
public class DriverWrapper implements Driver {

    private Driver driver;

    public DriverWrapper(Driver d) {
        driver = d;
    }
    public boolean acceptsURL(String u) throws SQLException {
        return driver.acceptsURL(u);
    }
    public Connection connect(String u, Properties p) throws SQLException {
        return driver.connect(u, p);
    }
    public int getMajorVersion() {
        return driver.getMajorVersion();
    }
    public int getMinorVersion() {
        return driver.getMinorVersion();
    }
    public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
        return driver.getPropertyInfo(u, p);
    }
    public boolean jdbcCompliant() {
        return driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}