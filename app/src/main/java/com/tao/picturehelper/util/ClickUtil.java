package com.tao.picturehelper.util;

public class ClickUtil {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {

        long time = System.currentTimeMillis();

        if ( time - lastClickTime < 500) {

            return true;

        }

        lastClickTime = time;

        return false;

    }
}
