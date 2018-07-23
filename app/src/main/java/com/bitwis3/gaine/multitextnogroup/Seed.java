package com.bitwis3.gaine.multitextnogroup;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.Display;
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
        Context contextIn;

        public CrashHandler(CrashHandlingInterface methodToExecute, Context contextIn) {
             this.methodToExecute = methodToExecute;
             this.contextIn = contextIn;

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

                        SharedPreferences.Editor editor = contextIn.getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE).edit();
                        editor.putString("CRASH", stackTraceToString(ex));
                        editor.apply();
                        // re-throw critical exception further to the os (important)

                        defaultUEH.uncaughtException(thread, ex);
                    }
                };

        interface CrashHandlingInterface{
             void executeOnCrash();
        }
    }
    //convert stacktrace to String
    static String stackTraceToString(Throwable e){
        StringBuilder sb = new StringBuilder("\n\nTime: "+ System.currentTimeMillis() +" \n\nlocal message: "+ e.getLocalizedMessage() + "\n\n\n");
        for(StackTraceElement el : e.getStackTrace()){
            sb.append(el.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    static class ScreenSizeHelper{
        int displayWidth, displayHeight;
        public ScreenSizeHelper(Activity activity) {
            Display display =
                    activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            //below is px
            displayWidth = size.x;
            displayHeight = size.y;

        }

        public int getDisplayWidth() {
            return displayWidth;
        }

        public int getDisplayHeight() {
            return displayHeight;
        }

        public int pxToDp(int px) {

            return (int) (px / Resources.getSystem().getDisplayMetrics().density);
        }
        public int dpToPx(int dp) {

            int px = Math.round(dp * Resources.getSystem().getDisplayMetrics().density);
            return px;
        }
    }

    public void copyToClipTray(Context context, String label, String body){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, body);
        clipboard.setPrimaryClip(clip);
    }


    /**
     * Get the network info
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     * @param context
     * @return
     */
    public static boolean isDataConnected(Context context){
        NetworkInfo info = Seed.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     * @param context
     * @param type
     * @return
     */
    public static boolean isDataConnectedWifi(Context context){
        NetworkInfo info = Seed.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     * @param context
     * @param type
     * @return
     */
    public static boolean isDataConnectedMobile(Context context){
        NetworkInfo info = Seed.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is fast connectivity
     * @param context
     * @return
     */
    public static boolean isDataConnectedFast(Context context){
        NetworkInfo info = Seed.getNetworkInfo(context);
        return (info != null && info.isConnected() && Seed.isConnectionFast(info.getType(),info.getSubtype()));
    }

    /**
     * Check if the connection is fast
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }

    }

    //specifically checks for texting/calling network
    public static boolean isTelephonyMobileConnected(Context context) {
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return (tel.getNetworkOperator() != null && !tel.getNetworkOperator().equals(""));
    }


}
