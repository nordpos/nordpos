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
package com.nordpos.device.scale;

import com.nordpos.device.ScaleInterface;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.util.StringParser;
import java.awt.Component;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceScaleFactory {

    private static final Logger logger = Logger.getLogger(DeviceScaleFactory.class.getName());

    private DeviceScale m_scale;
    private String sScaleType;

    public DeviceScaleFactory(Component parent, AppProperties props) {

        ServiceLoader<ScaleInterface> scaleLoader = ServiceLoader.load(ScaleInterface.class);

        m_scale = new DeviceScaleNull();

        String sScaleProperty = props.getProperty("machine.scale");

        StringParser sd = new StringParser(sScaleProperty);
        sScaleType = sd.nextToken(':');

        for (ScaleInterface machineInterface : scaleLoader) {
            try {
                if (sScaleType.equals("screen")) {
                    m_scale = machineInterface.getScale(parent, sScaleProperty);
                } else {
                    m_scale = machineInterface.getScale(sScaleProperty);
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    public boolean existsScale() {
        return m_scale != null;
    }

    public Double readWeight() throws ScaleException {

        if (m_scale == null) {
            throw new ScaleException(AppLocal.getIntString("scale.notdefined"));
        } else {
            Double result = m_scale.readWeight();
            if (result == null) {
                return null; // Canceled by the user / scale
            } else if ((result.doubleValue() < 0.002) && "massak".equals(sScaleType) == false) {
                // invalid result. nothing on the scale
                throw new ScaleException(AppLocal.getIntString("scale.invalidvalue"));
            } else if ("massak".equals(sScaleType)) {
                if ((result >= 0.04 && result <= 15000.0) || (result <= -0.04 && result >= -15000.0)) {
                    return result;
                } else {
                    throw new ScaleException(AppLocal.getIntString("scale.invalidvalue"));
                }
            } else {
                // valid result
                return result;
            }
        }
    }
}
