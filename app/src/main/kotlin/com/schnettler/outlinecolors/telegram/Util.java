package com.schnettler.outlinecolors.telegram;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.graphics.ColorUtils;

public class Util {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String arrayToString(String[] array) {
        StringBuilder result = new StringBuilder();
        result.append("\n\n");
        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
            if (i == array.length -1) {
                result.append(": ");
            } else {
                result.append(", ");
            }
        }
        return result.toString();
    }

    public static String blendColor(String colorForeground, String colorBackground, float amount) {
        int test = ColorUtils.blendARGB(Color.parseColor(colorForeground), Color.parseColor(colorBackground), 1 - amount);
        return String.format("#%06X", (0xFFFFFF & test));
    }

    public static boolean isPackageInstalled(String packageName , PackageManager packageManager) {
        try {
            return packageManager.getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
