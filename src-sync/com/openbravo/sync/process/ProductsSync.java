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
import com.openbravo.sync.kettle.KettleJob;
import com.openbravo.sync.util.TransformationFileCreate;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.pentaho.di.core.logging.LogLevel;

/**
 *
 * @author Mikel Irurita
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class ProductsSync implements ProcessAction {

    private AppProperties appProp;
    private final Properties hostProp;
    private final DataLogicSystem dlSystem;
    private String sERPUrl;
    private String sERPClientId;
    private String sERPOrgId;
    private String sERPPos;
    private String sERPUser;
    private String sERPPassword;
    private String sImportDirectory;
    private String sImportType;
    private String sTaxCatId;
    private String sProdCatId;
    private String sLocation;

    public ProductsSync(AppView app) {
        dlSystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        appProp = app.getProperties();
        hostProp = dlSystem.getResourceAsProperties(appProp.getHost() + "/properties");
    }

    @Override
    public MessageInf execute() throws BasicException {

        initSyncParameters();
        initSyncFolders();
        initERPParameters();

        File f = new File("");

        if (sImportType.equals("generaterows")) {
            TransformationFileCreate.createTransFile(sImportType, "Product Categories.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Products.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Warehouse.ktr");
            f = TransformationFileCreate.createTransFile(sImportType, "IMPORT PRODUCTS JOB.kjb");
        } else if (sImportType.equals("atol")) {
            TransformationFileCreate.createTransFile(sImportType, "Check Content Import File.ktr");
            TransformationFileCreate.createTransFile(sImportType, "CHECK IMPORT FILES.kjb");
            TransformationFileCreate.createTransFile(sImportType, "Check Import Variables.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Get Commands and Rows.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Import Products.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Import Tax Categories.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Import Taxes.ktr");
            TransformationFileCreate.createTransFile(sImportType, "IMPORT TAXES JOB.kjb");
            f = TransformationFileCreate.createTransFile(sImportType, "IMPORT PRODUCTS JOB.kjb");
        } else if (sImportType.equals("csv")) {
        } else if (sImportType.equals("openbravoerp")) {
            TransformationFileCreate.createTransFile(sImportType, "Attribute Instance.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Attributes.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Attribute Set.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Attribute Set Instance.ktr");
            TransformationFileCreate.createTransFile(sImportType, "ATTRIBUTES JOB.kjb");
            TransformationFileCreate.createTransFile(sImportType, "Attribute Use.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Attribute Values.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Customers.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Initialization.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Inventory.ktr");
            TransformationFileCreate.createTransFile(sImportType, "ORDERS.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Preview.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Product Categories.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Products.ktr");
            TransformationFileCreate.createTransFile(sImportType, "PRODUCTS JOB.kjb");
            TransformationFileCreate.createTransFile(sImportType, "Tax Categories.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Tax Customer Categories.ktr");
            TransformationFileCreate.createTransFile(sImportType, "Taxes.ktr");
            TransformationFileCreate.createTransFile(sImportType, "TAXES JOB.kjb");
            TransformationFileCreate.createTransFile(sImportType, "Warehouse.ktr");
            f = TransformationFileCreate.createTransFile(sImportType, "ALL SYNCHRONIZATION.kjb");
        } else {
            f = null;
        }

        if (f == null) {
            return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.kjbnotexist"));
        } else {

            KettleJob kj = new KettleJob(f, LogLevel.BASIC, null);

            kj.setVariable("db.URL", appProp.getDBURL());
            kj.setVariable("db.driver", appProp.getDBDriver());
            kj.setVariable("db.user", appProp.getDBUser());
            kj.setVariable("db.password", appProp.getDBPassword());

            kj.setVariable("sync.importdir", sImportDirectory);

            kj.setVariable("erp.URL", sERPUrl);
            kj.setVariable("erp.id", sERPClientId);
            kj.setVariable("erp.org", sERPOrgId);
            kj.setVariable("erp.pos", sERPPos);
            kj.setVariable("erp.user", sERPUser);
            kj.setVariable("erp.password", sERPPassword);

            kj.setVariable("taxcategoryid", sTaxCatId);
            kj.setVariable("productcategoryid", sProdCatId);
            kj.setVariable("location", sLocation);

            kj.runLocal();

            String log = kj.getLogContent();

            if (kj.hasFinish()) {
                return new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.syncok"), log);
            } else {
                return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.syncerror"), log);
            }
        }

    }

    private void initSyncParameters() {
        sImportType = hostProp.getProperty("sync.type", "generaterows").toLowerCase();

        sTaxCatId = hostProp.getProperty("taxcategoryid", "000").toLowerCase();
        sProdCatId = hostProp.getProperty("productcategoryid", "000").toLowerCase();
        sLocation = hostProp.getProperty("location", "0").toLowerCase();
    }

    private void initSyncFolders() {
        sImportDirectory = hostProp.getProperty("sync.importdir", System.getProperty("user.home"));
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
}
