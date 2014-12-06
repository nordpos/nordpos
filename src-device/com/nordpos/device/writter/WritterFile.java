/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2013 Nord Trading Ltd. <http://www.nordpos.com>
 *
 * This file is part of NORD POS.
 *
 * NORD POS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * NORD POS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * NORD POS. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nordpos.device.writter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class WritterFile extends Writter {
    
    private static final Logger logger = Logger.getLogger(WritterFile.class.getName());
    
    private final String m_sFilePrinter;
    private OutputStream m_out;
    
    public WritterFile(String sFilePrinter) {
        m_sFilePrinter = sFilePrinter;
        m_out = null;
    }
    
    @Override
    protected void internalWrite(byte[] data) {
        try {
            if (m_out == null) {
                m_out = new FileOutputStream(m_sFilePrinter);  // No poner append = true.
            }
            
            if (data != null) {
                m_out.write(data);
            } else {
                m_out.write(0x00);
            }
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    @Override
    protected void internalFlush() {
        try {
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    @Override
    protected void internalClose() {
        try {
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
