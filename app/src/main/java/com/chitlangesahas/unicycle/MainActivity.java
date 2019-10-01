package com.chitlangesahas.unicycle;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int LOCATION = 1;

    BroadcastReceiver myBroadcastReceiver = new WifiReceiverClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); // svg

        setContentView(R.layout.activity_main);

        final ProgressBar connectProgressBar = findViewById(R.id.connectProgressBar);
        connectProgressBar.setVisibility(View.GONE);

        // set the onclick listener to the connectButton.
        final Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectProgressBar.setVisibility(View.VISIBLE);
                connectToVehicle();
            }
        });


        // check if has location permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission from user
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
        }

        /* Register the Broadcast receiver */

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        final ProgressBar connectProgressBar = findViewById(R.id.connectProgressBar);
        connectProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("OnDestroy", "onDestroy Called");
        unregisterReceiver(myBroadcastReceiver);
    }


    private void connectToVehicle() {
        final String ssid = getString(R.string.ssid);
        final String key = getString(R.string.key);

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        int netId;

        // connect to the network. Check if its already in the config, if not then add it and then connect
        if (getExistingNetworkId(wifiConfig.SSID) != -1) {
            netId = getExistingNetworkId(wifiConfig.SSID);
        } else {
            netId = wifiManager.addNetwork(wifiConfig);
        }

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.disconnect(); // disconnect from any connected wifi networks
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    private int getExistingNetworkId(String SSID) {
        WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (existingConfig.SSID.equals(SSID)) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }


    private class WifiReceiverClass extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                if (info.isConnected()) {
                    //Do your work.

                    WifiManager cm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                    WifiInfo networkInfo = cm.getConnectionInfo();
                    String ssid = networkInfo.getSSID();

                    Log.v("Conn Info", ssid);
                    Log.v("Conn Info", context.getString(R.string.ssid));

                    if (ssid.equals("\"" + context.getString(R.string.ssid) + "\"")) {
                        Toast.makeText(context, context.getString(R.string.connectionSuccessText), Toast.LENGTH_SHORT).show();

                        // Launch the new activity to display the VESC_INTERFACE information page.
                        Intent i = new Intent(context, VESC_INTERFACE.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    } else {
                        Log.d("INFO", "else taken");
                        Toast.makeText(context, context.getString(R.string.connectionFailureText), Toast.LENGTH_SHORT).show();
                        ProgressBar progressBar = ((Activity) context).findViewById(R.id.connectProgressBar);
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }
        }

    }
}
