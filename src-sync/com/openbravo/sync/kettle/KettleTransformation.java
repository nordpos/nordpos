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

package com.openbravo.sync.kettle;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.Log4jBufferAppender;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LogMessage;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.*;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;

/**
 *
 * @author Mikel Irurita
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class KettleTransformation {

    private TransMeta transMeta;
    private Trans trans;
    private Map<String, RowStepCollector> steps;

    private Log4jBufferAppender bufferAppender;
    private LogWriter log;
    private LogMessage logMessage;

    private String logInFile;
    private boolean finishOk;

    private static final Logger logger = Logger.getLogger(KettleJob.class.getName());

    public KettleTransformation(File transXML, LogLevel logLevel, String logFilePath) {

        try {
            if (logFilePath != null) {
                File f = new File(logFilePath);
                if (f.exists() && f.isFile() && f.canWrite() && logFilePath != null) {
                    this.logInFile = f.getAbsolutePath();
                    LogWriter.getInstance(logInFile, true); // 4.x
                } else {
                    logger.log(Level.SEVERE, "{0} doesn't exist, please check the path and create the file with write permissions.", f.getAbsolutePath()); // 4.x
                    exit(0);
                }
            }

        } catch (KettleException ex) {
            logger.log(Level.SEVERE, "Creating log " + logFilePath, ex); // 4.x
            exit(0);
        }

        try {
            KettleEnvironment.init(); // 4.x
            EnvUtil.environmentInit(); // 4.x
        } catch (KettleException e) {
            logger.log(Level.SEVERE, "Error loading steps...", e);
            exit(0);
        }

        try {
            if (transXML.isFile() && transXML.exists() && transXML.canRead()) {
                    this.transMeta = new TransMeta(transXML.getAbsolutePath());
                    this.trans = new Trans(transMeta);
                    steps = new HashMap<String, RowStepCollector>();
            } else {
                logger.log(Level.SEVERE, "Reading .ktr file, please make sure the file exist and you have read permissions");
                log.println(logMessage, logLevel); // 4.x
                exit(0);
            }
        } catch (KettleException ex) {
            logger.log(Level.SEVERE, "Creating Transformation from XML file.", ex); // 4.x
            exit(0);
        }

        log = LogWriter.getInstance(); // 4.x
        bufferAppender = new Log4jBufferAppender(1024); // 4.x
        log.addAppender(bufferAppender); // 4.x
    }

    public void runLocal() {

        try {
            //Initialize Logging
//            Log4jBufferAppender appender = new Log4jBufferAppender(1024); // 4.x
//            LogWriter.getInstance().addAppender(appender); // 4.x
            //prepareExecution() and startThread()
            trans.prepareExecution(null);

            //Add listener to each step
            transMeta.getStepNames();
            StepMeta[] abc = transMeta.getStepsArray();
            StepInterface si;
            RowStepCollector dummyRsc;
            for(StepMeta sm : abc) {
                si = trans.getStepInterface(sm.getName(), 0);
                dummyRsc = new RowStepCollector();
                si.addRowListener(dummyRsc);
                steps.put(sm.getName(), dummyRsc);
            }

            //Starting threads
            trans.startThreads();

            //wait until finished
            trans.waitUntilFinished();

            if ( trans.getErrors() > 0 ) {
                finishOk = false;
                //throw new RuntimeException( "There were errors during transformation execution. See the log." );
            } else {
                finishOk = true;
            }

        } catch (KettleException ex) {
            finishOk = false;
            exit(0);
        }

        exit(1);
    }

    public void setVariable(String name, String value) {
        trans.setVariable(name, value);
    }

    public Map<String, RowStepCollector> getStepListeners() {
        return this.steps;
    }

    public List<RowMetaAndData> getStepRows(String stepName, String readOrWrite) {
        List<RowMetaAndData> result = null;

        if (steps.containsKey(stepName)) {
            if (readOrWrite.equals("read")) {
                result = steps.get(stepName).getRowsRead();
            } else if (readOrWrite.equals("write")) {
                result = steps.get(stepName).getRowsWritten();
            } else if (readOrWrite.equals("error")) {
                result = steps.get(stepName).getRowsError();
            }
        }

        return result;
    }

    private void exit(int exitCode) {
        // Close the open appenders...
        LogWriter.getInstance().close();

        switch(exitCode) {
            case 0:
                //throw new RuntimeException("There were errors during transformation execution. See the log.");
                break;
            default:
                break;
        }
    }

    public String getLogContent() {
        return bufferAppender.getBuffer().toString(); // 4.x
    }

    public boolean hasFinish() {
        return finishOk;
    }
}
