package org.sperixlabs.androidhotspotapi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Main extends Fragment {
    private Button startBtn, stopBtn;
    private TextView displayMsg;
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

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Starting..", Toast.LENGTH_LONG).show();
                displayMsg.setText("Starting Hotspot...");
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Stopping..", Toast.LENGTH_LONG).show();
                displayMsg.setText("Stopping Hotspot...");
            }
        });

        return mainConfig;
    }

}
