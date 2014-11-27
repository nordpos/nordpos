/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nordpos.server.jetty;

import java.io.File;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 *
 * @author svininykh-av
 */
public class AppContextBuilder {

    private WebAppContext webAppContext;

    public WebAppContext buildWebAppContext(String context, String folder, String name) {
        webAppContext = new WebAppContext();
        if (!name.equals("ROOT")) {
            webAppContext.setContextPath(context.concat(name));
        }
        webAppContext.setExtractWAR(true);
        webAppContext.setWar(new File(folder.concat("/" + name)).getAbsolutePath());
        webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");
        webAppContext.setAttribute("webContext", webAppContext);
        return webAppContext;
    }
}
