package com.android.celltechmobileservicesapp.json;


//        Public marca As String
//        Public model As String
//        Public culoare As String
//        Public imei As String
//        Public teste As Object
//        Public setari As Object
//        Public [Error] As String
//        Public success As String
//        Public info As String
//        Public teste_windows As List(Of testwindows)

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class InJsonModel implements Serializable {
    String model;
    String action;
    Params params;
    String error;
    String success;
    String info;
    int status;
    String version;
    String memo;
    String baseband;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;
    private static final long T = G * K;

    public static String convertToStringRepresentation(final double value) {
        final long[] dividers = new long[]{T, G, M, K, 1};
        final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};
        if (value < 1)
            throw new IllegalArgumentException("Invalid file size: " + value);
        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    private static String format(final double value,
                                 final long divider,
                                 final String unit) {
        final double result =
                divider > 1 ? value / (double) divider : value;
        return new DecimalFormat("#,##0.#").format(result) + " " + unit;
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long BlockSize = stat.getBlockSizeLong();
        long TotalBlocks = stat.getBlockCountLong();

        double kb = TotalBlocks * BlockSize / 1024;
        double mb = kb / 1024;
        double gb = mb / 1024;
        //DecimalFormat df = new DecimalFormat("#.##");
        //return convertToStringRepresentation(TotalBlocks * BlockSize);
        // return df.format(gb) + "GB";
        return nextPowerOf2((int) Math.floor(gb)) + "GB";
    }

    public static int nextPowerOf2(int n) {
        int count = 0;

        if (n > 0 && (n & (n - 1)) == 0)
            return n;

        while (n != 0) {
            n >>= 1;
            count += 1;
        }

        return 1 << count;
    }
}
