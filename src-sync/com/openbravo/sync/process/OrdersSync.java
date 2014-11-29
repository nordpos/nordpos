//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2009 Openbravo, S.L.
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
package com.openbravo.sync.process;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.ProcessAction;
import com.openbravo.pos.util.AltEncrypter;
import com.openbravo.sync.kettle.KettleTransformation;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.logging.LogLevel;

/**
 *
 * @author Mikel Irurita
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class OrdersSync implements ProcessAction {

    private final AppProperties appProp;
    private final Properties hostProp;
    private final DataLogicSystem dlSystem;

    private String sERPUrl;
    private String sERPClientId;
    private String sERPOrgId;
    private String sERPPos;
    private String sERPUser;
    private String sERPPassword;
    private String sExportDirectory;
    private String sExportType;

    public OrdersSync(AppView app) {
        dlSystem = (DataLogicSystem) app.getBean(DataLogicSystem.class.getName());
        appProp = app.getProperties();
        hostProp = dlSystem.getResourceAsProperties(appProp.getHost() + "/properties");
    }

    @Override
    public MessageInf execute() throws BasicException {

        try {
            initSyncParameters();
            initSyncFolders();
            initERPParameters();
            
            File f = createTransFile("/com/openbravo/transformations/" + sExportType + "/" + "ORDERS.ktr");
            
            if (f == null) {
                return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.ktrnotexist"));
            } else {

                KettleTransformation kt = new KettleTransformation(f, LogLevel.DETAILED, null);
                
                kt.setVariable("erp.URL", sERPUrl);
                kt.setVariable("erp.id", sERPClientId);
                kt.setVariable("erp.org", sERPOrgId);
                kt.setVariable("erp.pos", sERPPos);
                kt.setVariable("erp.user", sERPUser);
                kt.setVariable("erp.password", sERPPassword);
                
                kt.setVariable("sync.exportdir", sExportDirectory);
                
                kt.setVariable("db.URL", appProp.getDBURL());
                kt.setVariable("db.driver", appProp.getDBDriver());
                kt.setVariable("db.user", appProp.getDBUser());
                kt.setVariable("db.password", appProp.getDBPassword());
                
                kt.runLocal();
                
                String log = kt.getLogContent();
                
                if (kt.hasFinish()) {
                    return new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.syncok"), log);
                } else {
                    return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.syncerror"), log);
                }
            }
        } catch (IOException ex) {
            return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.syncerror"), ex.getMessage());
        }

    }

    private int getOrderNumSync(KettleTransformation kt) {
        int cont = 0;
        Map<String, String> ord = new HashMap<>();
        for (RowMetaAndData r : kt.getStepRows("Parse ExternalPOS", "write")) {
            Object[] t = r.getData();
            if (!ord.containsKey((String) t[0])) {
                ord.put((String) t[0], (String) t[0]);
                cont++;
            }
        }
        return cont;
    }

    private void initSyncFolders() {
        sExportDirectory = hostProp.getProperty("sync.exportdir", System.getProperty("user.home"));
    }

    private void initERPParameters() {
        sERPUrl = hostProp.getProperty("erp.url");
        sERPClientId = hostProp.getProperty("erp.id");
        sERPOrgId = hostProp.getProperty("erp.org");
        sERPPos = hostProp.getProperty("erp.pos");
        sERPUser = hostProp.getProperty("erp.user");
        if (sERPUser != null && sERPPassword != null && sERPPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sERPUser);
            sERPPassword = cypher.decrypt(sERPPassword.substring(6));
        }
    }

    private void initSyncParameters() {
        sExportType = hostProp.getProperty("sync.type", "generaterows").toLowerCase();
    }

    private File createTransFile(String path) throws IOException {
        FileWriter fw = null;
        File f = null;
        try {
            byte[] bytes = ImageUtils.getBytesFromResource(path);
            if (bytes == null) {
                return null;
            } else {
                f = File.createTempFile("ORDERS", ".ktr");
                fw = new FileWriter(f);
                String text = new String(bytes);
                fw.write(text);
            }
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
        return f;
    }
}
