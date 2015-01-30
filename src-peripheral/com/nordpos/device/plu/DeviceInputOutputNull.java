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

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public class DeviceInputOutputNull implements DeviceInputOutput {

    @Override
    public void connectDevice() throws DeviceInputOutputException {
    }

    @Override
    public void disconnectDevice() {
    }

    @Override
    public void startDownloadProduct() throws DeviceInputOutputException {
    }

    @Override
    public ProductIO recieveProduct() throws DeviceInputOutputException {
        return null;
    }

    @Override
    public void startUploadProduct() throws DeviceInputOutputException {
    }

    @Override
    public void sendProduct(ProductIO product) throws DeviceInputOutputException {
    }

    @Override
    public void stopUploadProduct() throws DeviceInputOutputException {
    }
}
