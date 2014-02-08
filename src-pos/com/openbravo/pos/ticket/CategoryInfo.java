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
package com.openbravo.pos.ticket;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import java.awt.image.*;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.data.loader.SerializerRead;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class CategoryInfo implements IKeyed {

    private static final long serialVersionUID = 8612449444103L;
    private String m_sID;
    private String m_sName;
    private String m_sCode;
    private BufferedImage m_Image;

    public CategoryInfo(String id, String name, String code, BufferedImage image) {
        m_sID = id;
        m_sName = name;
        m_sCode = code;
        m_Image = image;
    }

    public Object getKey() {
        return m_sID;
    }

    public void setID(String sID) {
        m_sID = sID;
    }

    public String getID() {
        return m_sID;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String sName) {
        m_sName = sName;
    }

    public String getCode() {
        return m_sCode;
    }

    public void setCode(String sCode) {
        m_sCode = sCode;
    }

    public BufferedImage getImage() {
        return m_Image;
    }

    public void setImage(BufferedImage img) {
        m_Image = img;
    }

    @Override
    public String toString() {
        return m_sName;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new CategoryInfo(dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        ImageUtils.readImage(dr.getBytes(4)));
            }
        };
    }
}
