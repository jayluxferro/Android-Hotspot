package org.sperixlabs.androidhotspotapi;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.sperixlabs.androidhotspotapi.api.WifiAddresses;
import org.sperixlabs.androidhotspotapi.api.WifiHotspots;
import org.sperixlabs.androidhotspotapi.api.WifiStatus;


/**
 * A simple {@link Fragment} subclass.
 */
public class Main extends Fragment {
    private Button startBtn, stopBtn;
    private TextView displayMsg, ssidView, ip, mac;
    private WifiHotspots hotutil;
    private WifiStatus ws;
    private SQLiteHandler db;
    private String ssid = null;
    private String password = null;
    private WifiAddresses wAddr;

    public Main() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return loadMain(inflater, container, savedInstanceState);
    }

    private View loadMain(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View mainConfig =  inflater.inflate(R.layout.fragment_main, container, false);

        displayMsg = mainConfig.findViewById(R.id.status);
        startBtn = mainConfig.findViewById(R.id.startBtn);
        stopBtn = mainConfig.findViewById(R.id.stopBtn);
        wAddr = new WifiAddresses(this.getContext());
        ssidView = mainConfig.findViewById(R.id.ssid);
        ip = mainConfig.findViewById(R.id.gateway);
        mac = mainConfig.findViewById(R.id.mac);

        hotutil = new WifiHotspots(this.getContext());
        ws = new WifiStatus(this.getContext());
        //checking if device supports wifi ad-hoc
        if(!ws.checkWifi(ws.SUPPORT_WIFI)){
            displayMsg.setText("WiFi not supported");
            displayMsg.setTextColor(getResources().getColor(R.color.red));
            Toast.makeText(getContext(), "WiFi not supported", Toast.LENGTH_LONG).show();
            startBtn.setClickable(false);
            stopBtn.setClickable(false);
        }

        db = new SQLiteHandler(this.getContext());

        //getting ssid and password
        Cursor mCursor = db.getConfiguration();
        if(mCursor.getCount() >= 1){
            mCursor.moveToFirst();
            ssid = mCursor.getString(mCursor.getColumnIndex("ssid"));
            password = mCursor.getString(mCursor.getColumnIndex("password"));
            mCursor.close();
        }else{
            Toast.makeText(getContext(), "Error encountered!", Toast.LENGTH_LONG).show();
            return mainConfig;
        }

        //checking if hotspot is on
        if(db.getStatus()){
            Toast.makeText(getContext(), "Hotspot is on", Toast.LENGTH_LONG).show();
            displayMsg.setText("Hotspot Started!");
            displayMsg.setTextColor(getResources().getColor(R.color.green));
            ssidView.setText(ssid);
            ip.setText(wAddr.getDeviceIPAddress());
            mac.setText(wAddr.getDeviceMacAddress());
        }else{
            Toast.makeText(getContext(), "Hotspot is off", Toast.LENGTH_LONG).show();
            displayMsg.setText("Hotspot disabled!");
            displayMsg.setTextColor(getResources().getColor(R.color.red));
        }

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Starting..", Toast.LENGTH_LONG).show();
                displayMsg.setText("Starting Hotspot...");
                //setting hotspot configuration using ssid and password
                if(hotutil.setHotSpot(ssid, password)){
                    Toast.makeText(getContext(), "Hotspot profile created", Toast.LENGTH_LONG).show();
                    displayMsg.setText("Hotspot profile created!");
                    displayMsg.setTextColor(getResources().getColor(R.color.blue));

                    //starting hotspot
                    if(hotutil.startHotSpot(true)){
                        displayMsg.setText("Hotspot Started!");
                        displayMsg.setTextColor(getResources().getColor(R.color.green));
                        db.updateStatus("1");
                        ssidView.setText(ssid);
                        ip.setText(wAddr.getDeviceIPAddress());
                        mac.setText(wAddr.getGatWayMacAddress());
                    }else{
                        displayMsg.setText("Could not start hotspot");
                        displayMsg.setTextColor(getResources().getColor(R.color.red));
                    }
                }else{
                    Toast.makeText(getContext(), "Could not create hotspot profile", Toast.LENGTH_LONG).show();
                    displayMsg.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Stopping..", Toast.LENGTH_LONG).show();
                displayMsg.setText("Stopping Hotspot...");
                if(hotutil.startHotSpot(false)){
                    displayMsg.setText("Hotspot disabled");
                    db.updateStatus("0");
                    ssidView.setText("SSID");
                    ip.setText("IP Address");
                    mac.setText("MAC Address");
                }else{
                    displayMsg.setText("Could not stop hotspot");
                }
                displayMsg.setTextColor(getResources().getColor(R.color.red));
            }
        });

        return mainConfig;
    }

}
