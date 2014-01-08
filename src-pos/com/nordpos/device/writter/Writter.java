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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3.0
 */
public abstract class Writter {

    private boolean initialized = false;

    private final ExecutorService exec;

    public Writter() {
        exec = Executors.newSingleThreadExecutor();
    }

    protected abstract void internalWrite(byte[] data);

    protected abstract void internalFlush();

    protected abstract void internalClose();

    public void init(final byte[] data) {
        if (!initialized) {
            write(data);
            initialized = true;
        }
    }

    public void write(String sValue) {
        write(sValue.getBytes());
    }

    public void write(final byte[] data) {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                internalWrite(data);
            }
        });
    }

    public void flush() {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                internalFlush();
            }
        });
    }

    public void close() {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                internalClose();
            }
        });
        exec.shutdown();
    }
}
