/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nordpos.sync.util;

import com.openbravo.data.loader.ImageUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class TransformationFileCreate {

    public static File createTransFile(String sTransType, String sTransFileName) {
        FileWriter fw = null;
        File f = null;

        try {
            byte[] bytes = ImageUtils.getBytesFromResource("/com/openbravo/transformations/" + sTransType + "/" + sTransFileName);
            if (bytes == null) {
                return null;
            } else {
                f = new File(getTempDir() + sTransFileName);
                f.createNewFile();
                fw = new FileWriter(f);
                String text = new String(bytes);
                fw.write(text);
            }
        } catch (IOException ex) {
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
            }
        }
        return f;
    }

    private static String getTempDir() {
        String tmpDir = System.getProperty("java.io.tmpdir");

        if (!(tmpDir.endsWith("/") || tmpDir.endsWith("\\"))) {
            tmpDir = tmpDir + System.getProperty("file.separator");
        }

        return tmpDir;
    }
}
