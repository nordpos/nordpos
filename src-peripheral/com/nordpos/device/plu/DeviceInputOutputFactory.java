/**
 *
 * NORD POS is a fork of Openbravo POS.
 *
 * Copyright (C) 2009-2012 Nord Trading Ltd. <http://www.nordpos.com>
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
package com.nordpos.device.plu;

import com.openbravo.pos.forms.AppProperties;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author administrator
 */
public class DeviceInputOutputFactory {

    private static final Logger logger = Logger.getLogger(DeviceInputOutputFactory.class.getName());

    public static DeviceInputOutput createInstance(AppProperties props) {

        ServiceLoader<InputOutputInterface> pluLoader = ServiceLoader.load(InputOutputInterface.class);

        DeviceInputOutput m_plu = new DeviceInputOutputNull();

        for (InputOutputInterface machineInterface : pluLoader) {
            try {
                m_plu = machineInterface.getDeviceIO(props.getProperty("machine.pludevice"));
            } catch (Exception ex) {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return m_plu;
    }
}
