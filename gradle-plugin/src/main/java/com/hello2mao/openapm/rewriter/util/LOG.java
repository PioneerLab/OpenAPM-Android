package com.hello2mao.openapm.rewriter.util;

public class LOG {

    private static final String TAG = "==OpenAPM-Rewriter== ";

    public static void info(String message) {
        System.out.println(TAG + message);
    }

    public static void debug(String msg) {
        System.out.println(TAG + msg);
    }

    public static void warning(String msg) {
        System.out.println(TAG + msg);
    }

    public static void error(String msg) {
        System.out.println(TAG + msg);
    }

}