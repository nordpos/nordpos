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
package com.nordpos.server.jetty;

import com.nordpos.server.ServerInterface;
import java.lang.management.ManagementFactory;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 * @version NORD POS 3
 */
public class ServerApp implements ServerInterface {

    private Server server;

    public ServerApp() {
        this(8585);
    }

    public ServerApp(Integer runningPort) {
        server = new Server(runningPort);
    }

    public void setHandler(ContextHandlerCollection contexts) {
        server.setHandler(contexts);
    }

    @Override
    public void start() throws Exception {
        MBeanContainer mbContainer=new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);
        org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");

        if (server.isStopped()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        server.start();
                    } catch (Exception ex) {
                        // Logged by Jetty and managed by Lifecycle listener
                    }
                }
            });
            t.start();
        }
    }

    @Override
    public void stop() throws Exception {
        if (server.isRunning()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        server.stop();
                    } catch (Exception ex) {
                        // Logged by Jetty and managed by Lifecycle listener
                    }
                }
            });
            t.start();
        }
        server.join();
    }

}
