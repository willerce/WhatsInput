package com.buscode.whatsinput;

import android.app.Application;

/**
 * User: fanxu
 * Date: 12-10-28
 */
public class App extends Application {
    /*private static final String PATH_LOGGER_FILE = "/sdcard/airdroid/air_input.log";

    static {

        try {

            final LogConfigurator lc = new LogConfigurator();
            //lc.setFileName(PushConfig.getInstance().getPathLoggerFile());
            lc.setFileName(PATH_LOGGER_FILE);
            // %C{1} is Class name exclude the package
            // %M is the method name
            // %d is date
            // %m%n is log message
            //			lc.setFilePattern("%d - [%p::%C{1}.%M] - %m%n");
            lc.setFilePattern("%d - [%-6p-%c] - %m%n");
            lc.setMaxBackupSize(2);
            lc.setMaxFileSize(1024 * 1024);
            lc.setRootLevel(Level.DEBUG);
            // Set log level of a specific logger
            lc.setLevel("org.apache", Level.DEBUG);
            lc.configure();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
