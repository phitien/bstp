package com.bosch.si.emobility.bstp.helper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.app.Application;

/**
 * Created by sgp0458 on 4/12/15.
 */
public class Utils {
    public static class Notifier {
        public static int DURATION = 1000;

        public static void notify(String title, String message) {
            Context context = Application.getInstance().getCurrentContext();
            if (context != null) {
                //noinspection ResourceType
                Toast toast = Toast.makeText(context, message, DURATION);
                toast.show();
            }
        }

        public static void notify(String message) {
            notify("", message);
        }

    }

    public static class Log {
        public static void e(String tag, String msg) {
            android.util.Log.e(tag, msg);
        }

        public static void e(String tag, String msg, Throwable tr) {
            android.util.Log.e(tag, msg, tr);
        }
    }

    public static class Indicator {
        protected static Dialog overlayDialog;
        protected static String dialogTitle;
        protected static String dialogMessage;

        public static String getDialogTitle() {
            return dialogTitle;
        }

        public static void setDialogTitle(String dialogTitle) {
            Indicator.dialogTitle = dialogTitle;
        }

        public static void show() {
            try {
                Context context = Application.getInstance().getCurrentContext();
                if (context != null) {
                    hide();
                    String title = dialogTitle;
                    String message = dialogMessage != null ? dialogMessage : Utils.getString(R.string.please_wait);
                    overlayDialog = ProgressDialog.show(context, title, message, true);
                    overlayDialog.show();
                    dialogTitle = null;
                    dialogMessage = null;
                }
            } catch (Exception e) {
                Utils.Log.e("BSTP_Utils_Indicator_show", e.getMessage());
            }
        }

        public static void hide() {
            if (overlayDialog != null) {
                overlayDialog.hide();
                overlayDialog.dismiss();
            }
        }
    }

    public static Context getContext() {
        return Application.getInstance().getApplicationContext();
    }

    public static int getImage(String name) {
        return getIdentifier(name, "drawable");
    }

    public static int getColor(int resInt) {
        return getResources().getColor(resInt);
    }

    public static int getColor(String name) {
        return getColor(getIdentifier(name, "color"));
    }

    public static String getString(int resInt) {
        return getContext().getString(resInt);
    }

    public static String getString(String name) {
        return getString(getIdentifier(name, "string"));
    }

    public static File getFile(String directoryName, String fileName) {
        File appFileDirectory = getContext().getFilesDir();
        File newDirectory = new File(appFileDirectory.getAbsolutePath() + File.separator + directoryName);
        if (newDirectory.exists() == false) {
            newDirectory.mkdirs();
        }
        return new File(newDirectory.getAbsolutePath() + File.separator + fileName);
    }

    public static String getFileContent(String path) {
        //reading text from file
        try {
            FileInputStream fileIn = new FileInputStream(path);
            InputStreamReader streamReader = new InputStreamReader(fileIn);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            streamReader.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getFileContent(String directoryName, String fileName) {
        //reading text from file
        File appFileDirectory = getContext().getFilesDir();
        File newDirectory = new File(appFileDirectory.getAbsolutePath() + File.separator + directoryName);
        if (newDirectory.exists() == false) {
            newDirectory.mkdirs();
        }
        String path = newDirectory.getAbsolutePath() + File.separator + fileName;
        return getFileContent(path);
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    public static String getPackageName() {
        return getContext().getPackageName();
    }

    public static int getIdentifier(String name, String defType, String defPackage) {
        return getResources().getIdentifier(name, defType, defPackage);
    }

    public static int getIdentifier(String name, String defType) {
        return getIdentifier(name, defType, getPackageName());
    }

}
