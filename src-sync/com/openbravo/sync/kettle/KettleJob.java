//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008 Openbravo, S.L.
//    http://sourceforge.net/projects/openbravopos
//
//    This program is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin Street, Fifth floor, Boston, MA  02110-1301  USA

package com.openbravo.sync.kettle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pentaho.di.core.JndiUtil;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.Log4jBufferAppender;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LogMessage;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.*;

/**
 *
 * @author Mikel Irurita
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class KettleJob {

    private Job job;
    private JobMeta jobMeta;
    private LogWriter log;
    private String logInFile;
    private Log4jBufferAppender bufferAppender;
    private LogMessage logMessage;
    private boolean finishOk;

    private static final Logger logger = Logger.getLogger(KettleJob.class.getName());

    public KettleJob(File jobXML, LogLevel logLevel, String logFilePath) {

        try {
            if (logFilePath != null) {
                File f = new File(logFilePath);
                if (f.exists() && f.isFile() && f.canWrite() && logFilePath != null) {
                    this.logInFile = f.getAbsolutePath();
                    LogWriter.getInstance(logInFile, true); // 4.x
                } else {
                    logger.log(Level.SEVERE, "{0} doesn't exist, please check the path and create the file with write permissions", f.getAbsolutePath());
                    exit(0);
                }
            }
        } catch (KettleException ex) {
            logger.log(Level.SEVERE, "Creating log " + logFilePath, ex);
            exit(0);
        }

        try {
            KettleEnvironment.init(); // 4.x

            EnvUtil.environmentInit();
            JndiUtil.initJNDI();

        } catch (KettleException e) {
            logger.log(Level.SEVERE, "Error loading steps...", e);
            exit(0);
        }

        try {
            if (jobXML.isFile() && jobXML.exists() && jobXML.canRead()) {
                jobMeta = new JobMeta(jobXML.getAbsolutePath(), null);
                job = new Job(null, jobMeta);
                job.setLogLevel(logLevel);
            } else {
                logger.log(Level.SEVERE, "Reading .kjb file, please make sure the file exist and you have read permissions");
                exit(0);
                throw new RuntimeException();
            }
        } catch (KettleException ex) {
            logger.log(Level.SEVERE, "Creating Job from XML file.", ex);
            exit(0);
        }

        log = LogWriter.getInstance(); // 4.x
        bufferAppender = new Log4jBufferAppender(1024); // 4.x
        log.addAppender(bufferAppender);
        printLogMessage("Ready to start Job " + job.getFilename());
    }

    public void runLocal() {

        Result result = new Result();
        Date start, stop;
        Calendar cal;
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        cal = Calendar.getInstance();
        start = cal.getTime();

        job.initializeVariablesFrom(null);
        job.getJobMeta().setInternalKettleVariables(job);

        job.start();
        job.waitUntilFinished();

        result = job.getResult();

        if (result.getNrErrors() != 0) {
            finishOk = false;
            printLogMessage("Finished Job " + job.getFilename() + " with " + result.getNrErrors() + " errors.");
        } else {
            finishOk = true;
            printLogMessage("Finished Job " + job.getFilename() + " without errors.");
        }

        cal = Calendar.getInstance();
        stop = cal.getTime();
        String begin = df.format(start).toString();
        String end = df.format(stop).toString();

        printLogMessage("Start time:" + begin + " Stop time:" + end); // 4.x

        long seconds = (stop.getTime() - start.getTime()) / 1000;
        if (seconds <= 60) {
            printLogMessage("Processing ended after " + String.valueOf(seconds) + " seconds."); // 4.x
        } else if (seconds <= 60 * 60) {
            int min = (int) (seconds / 60);
            int rem = (int) (seconds % 60);
            printLogMessage("Processing ended after " + String.valueOf(min) + " minutes and " + rem + " seconds (" + seconds + " seconds total)."); // 4.x
        } else if (seconds <= 60 * 60 * 24) {
            int rem;
            int hour = (int)(seconds / (60 * 60));
            rem = (int)(seconds % (60 * 60));
            int min = rem / 60;
            rem = rem % 60;
            printLogMessage("Processing ended after " + String.valueOf(hour) + " hours, " + String.valueOf(min) + " minutes and " + String.valueOf(rem) + " seconds (" + String.valueOf(seconds) + " seconds total)."); // 4.x
        } else {
            int rem;
            int days = (int)(seconds / (60 * 60 * 24));
            rem = (int)(seconds % (60 * 60 * 24));
            int hour = rem / (60 * 60);
            rem = rem % (60 * 60);
            int min = rem / 60;
            rem = rem % 60;
            printLogMessage("Processing ended after " +  String.valueOf(days) + " days, " + String.valueOf(hour) + " hours, " + String.valueOf(min) + " minutes and " + String.valueOf(rem) + " seconds (" + String.valueOf(seconds) + " seconds total)."); // 4.x
        }

        exit(1);
    }

    public void setVariable(String name, String value) {
        job.setVariable(name, value);
    }

    private void exit(int exitCode) {
        // Close the open appenders...
        LogWriter.getInstance().close();

        switch(exitCode) {
            case 0:
                finishOk = false;
                throw new RuntimeException("There were errors during transformation execution. See the log.");
            default:
                break;
        }

    }

    public boolean hasFinish() {
        return finishOk;
    }

    public String getLogContent() {
        return bufferAppender.getBuffer().toString(); // 4.x
    }

    private void printLogMessage(String sLogMessage) {
        logMessage = new LogMessage(sLogMessage, job.getLogChannelId(), job.getLogLevel());
        log.println(logMessage, job.getLogLevel());
    }
}
