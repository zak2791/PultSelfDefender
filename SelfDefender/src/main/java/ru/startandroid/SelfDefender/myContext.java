package ru.startandroid.SelfDefender;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class myContext extends Application {
    static private  Context context;
    static private  WifiManager wm;
    static private WifiInfo conInf;
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }
    public static int getIP() {
        conInf = wm.getConnectionInfo();
        int ipAddress = conInf.getIpAddress() | (255 << 24);
        return ipAddress;
    }
}