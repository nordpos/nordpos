/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nordpos.server.jetty;

import java.lang.management.ManagementFactory;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

/**
 *
 * @author svininykh-av
 */
public class ServerApp {

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

    public void stop() throws Exception {
        if (server.isRunning()) {
            Thread t = new Thread(new Runnable() {
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
