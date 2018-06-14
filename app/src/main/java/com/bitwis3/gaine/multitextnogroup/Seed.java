package com.bitwis3.gaine.multitextnogroup;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;

public class Seed {
    Context context;

    public Seed(Context context) {
        this.context = context;
    }

    public Seed() {

    }

    //returns date in formatted string
    public String getLocaleDateString(long timeInMillis){
        return DateFormat.getDateTimeInstance().format(timeInMillis);
    }

    //hides the keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    static class CrashHandler {
        //declare variable for uncaught exception handler
        private Thread.UncaughtExceptionHandler defaultUEH;
        private CrashHandlingInterface methodToExecute;

        public CrashHandler(CrashHandlingInterface methodToExecute) {
             this.methodToExecute = methodToExecute;

            // uncaught exception handler variable initialized
            defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

            // setup handler for uncaught exception
            Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
        }


        private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler =
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {

                        //YOUR CUSTOM CODE HERE FOR EXCEPTION
                        methodToExecute.executeOnCrash();

                        // re-throw critical exception further to the os (important)
                        defaultUEH.uncaughtException(thread, ex);
                    }
                };

        interface CrashHandlingInterface{
             void executeOnCrash();
        }
    }
}
