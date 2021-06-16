package com.android.celltechmobileservicesapp;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.android.celltechmobileservicesapp.Constants.keepMBytesFree;
import static com.android.celltechmobileservicesapp.Constants.toBytes;

public class Utils {

    public static String getActionNameByCode(Context mContext, String code) {
        int i = -1;
        for (String cc : mContext.getResources().getStringArray(R.array.codes)) {
            i++;
            if (cc.equals(code))
                break;
        }
        return mContext.getResources().getStringArray(R.array.names)[i];
    }


    public static void appendLogFile(Context mContext, String text) {
        try {
            //BufferedWriter for performance, true to set append to file flag
            File dir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/wipapp/");
            } else {
                dir = new File(mContext.getExternalFilesDir(null) + "/");
            }
            dir.mkdirs();
            File logFile = new File(dir + "/log.file");
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while (true) {
                if (!((line = reader.readLine()) != null)) break;
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getLogFromFile(Context mContext) {
        String ret = "";
        FileInputStream fin = null;
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/wipapp/");
        } else {
            dir = new File(mContext.getExternalFilesDir(null) + "/");
        }
        dir.mkdirs();
        File logFile = new File(dir + "/log.file");
        try {
            fin = new FileInputStream(logFile);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void clearFileContent(Context mContext) {
        //File logFile = new File(mContext.getExternalFilesDir(null) + "/log.file");
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/wipapp/");
        } else {
            dir = new File(mContext.getExternalFilesDir(null) + "/");
        }
        dir.mkdirs();
        File logFile = new File(dir + "/log.file");

        logFile.delete();
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getCurrentAvailableSpace() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (stat.getBlockSizeLong() * stat.getAvailableBlocksLong()) - (keepMBytesFree * toBytes);
        long megAvailable = bytesAvailable / (1024 * 1024);
        return megAvailable;
    }

    public static long getAvailableSpaceForZero() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (stat.getBlockSizeLong() * stat.getAvailableBlocksLong()) - (keepMBytesFree * toBytes);
        long bytesa = bytesAvailable / Constants.ddBS;
        return bytesa;
    }
}
