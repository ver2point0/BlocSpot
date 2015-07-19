package com.ver2point0.android.blocspot.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.ver2point0.android.blocspot.R;

public class Utils {

    private static Context context = null;

    public static void setContext(Context context) {
        Utils.context = context;
    }

    public static void setColorString(String color, TextView colorLabel) {
        if (context != null) {
            if(color.equals(Constants.CYAN)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.cyan));
            } else if(color.equals(Constants.BLUE)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.blue));
            } else if(color.equals(Constants.GREEN)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else if(color.equals(Constants.MAGENTA)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.magenta));
            } else if(color.equals(Constants.ORANGE)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.orange));
            } else if(color.equals(Constants.RED)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.red));
            } else if(color.equals(Constants.ROSE)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.rose));
            } else if(color.equals(Constants.VIOLET)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.violet));
            } else if(color.equals(Constants.YELLOW)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            }
        }
    }

    public static boolean haveNetworkConnection() {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : networkInfo) {
            if (ni.getTypeName().equalsIgnoreCase(Constants.NETWORK_WIFI)) {
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase(Constants.NETWORK_MOBILE)) {
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void checkIfConnected() {
        if (!haveNetworkConnection()) {
            Toast.makeText(context, context.getString(R.string.toast_no_network), Toast.LENGTH_SHORT).show();
        }
    }
}
