package com.example.androidgeotest.activities.Util;

import android.content.Context;
import android.graphics.Color;
import android.os.Debug;
import android.util.Log;

import java.text.AttributedCharacterIterator;
import java.util.Arrays;

/**
 * Created by r.sciamanna on 10/08/2016.
 */

public final class Logga {

    private static String name;
    private static String method;
    private static String lineNumber;
    private static final String DEBUGTAG = "\u2660";

    public static void isIn(Context context){
        fvd(context,"Sono qui");
    }

    /***************************************************************
     Simple Verbose Debug
     Shows Tag and message
     ****************************************************************/
    public static void svd(String tag, String message) {
        Log.i(DEBUGTAG, tag + " : " + message);
    }

    /***************************************************************
      Full Verbose Debug
        Shows ClassName, Method() [Linenumber] and message
    ****************************************************************/
    public static void fvd(Context context, String message) {
        name = context.getClass().getName();
        method = context.getClass().getSimpleName();
        svd(getStackTrace(name, method,true),message);
    }

    public static void fvd(Context context, String tag,String message) {
        name = context.getClass().getName();
        method = context.getClass().getSimpleName();
        svd(getStackTrace(name, method,true)+" *"+tag+"*",message);
    }

    /***************************************************************
     Advanced Verbose Debug
     Shows ClassName, Method() [Linenumber] and message
     ****************************************************************/
    public static void avd(Context context, String message) {
        name = context.getClass().getName();
        method = context.getClass().getSimpleName();
        svd(getStackTrace(name, method,false),message);
    }

    public static void avd(Context context, String tag,String message) {
        name = context.getClass().getName();
        method = context.getClass().getSimpleName();
        svd(getStackTrace(name, method,false)+" *"+tag+"*",message);
    }

    public static String getStackTrace(final String className, final String classSimpleName,boolean linenumber) {
        String output="";
        final StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
        int index = 0;
        for (StackTraceElement ste : steArray) {
            if (ste.getClassName().equals(className)) {
                break;
            }
            index++;
        }
        if (index >= steArray.length) {
            // Little Hacky
//            Log.i(classSimpleName, Arrays.toString(new String[]{steArray[3].getMethodName(), String.valueOf(steArray[3].getLineNumber())}));
            name = classSimpleName;
            method = steArray[3].getMethodName();
            lineNumber = String.valueOf(steArray[3].getLineNumber());
        } else {
            // Legitimate
//            Log.i(classSimpleName, Arrays.toString(new String[]{steArray[index].getMethodName(), String.valueOf(steArray[index].getLineNumber())}));
            name = classSimpleName;
            method = steArray[index].getMethodName();
            lineNumber = String.valueOf(steArray[index].getLineNumber());
        }
        if(linenumber){
            output = name+" , "+method+"() ["+lineNumber+"]";
        }
        else{
            output = name+" , "+method+"()";
        }

        return output;
    }
}
