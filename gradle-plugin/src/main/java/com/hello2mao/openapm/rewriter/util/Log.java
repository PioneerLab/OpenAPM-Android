package com.hello2mao.openapm.rewriter.util;

public class Log {

    private static final String TAG = "==OpenAPM-Rewriter== ";

    public void info(String message) {
        System.out.println(TAG + message);
    }

    public void debug(String msg) {
        System.out.println(TAG + msg);
    }

    public void warning(String msg) {
        System.out.println(TAG + msg);
    }

    public void error(String msg) {
        System.out.println(TAG + msg);
    }

    public void error(String msg, Throwable e) {
        System.out.println(TAG + msg);
    }

}