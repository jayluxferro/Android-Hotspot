package org.sperixlabs.androidhotspotapi;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Config extends Fragment {
    private EditText ssid, password;
    private SQLiteHandler db;
    private CheckBox showPassword;
    private Button updateConfigBtn;

    public Config() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return loadConfig(inflater,container, savedInstanceState);
    }

    public View loadConfig(final LayoutInflater inflater, final ViewGroup container,
                           final Bundle savedInstanceState){
        View configView = inflater.inflate(R.layout.fragment_config, container, false);
        ssid = configView.findViewById(R.id.ssid);
        password = configView.findViewById(R.id.password);
        showPassword = configView.findViewById(R.id.showPassword);
        updateConfigBtn = configView.findViewById(R.id.updateConfigBtn);

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        showPassword.setClickable(false);

        showPassword.setVisibility(View.GONE);
        //db
        db = new SQLiteHandler(this.getContext());
        Cursor mCursor = db.getConfiguration();
        if(mCursor.getCount() >= 1){
            mCursor.moveToFirst();
            ssid.setText(mCursor.getString(mCursor.getColumnIndex("ssid")));
            password.setText(mCursor.getString(mCursor.getColumnIndex("password")));
            mCursor.close();
        }

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                    showPassword.setClickable(false);
                }
            }
        });

        updateConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newSsid = ssid.getText().toString();
                String newPassword = password.getText().toString();
                if(db.updateConfiguration(newSsid, newPassword)){
                    Toast.makeText(view.getContext(), "Configuration updated", Toast.LENGTH_LONG).show();
                    loadConfig(inflater, container, savedInstanceState);
                }else{
                    Toast.makeText(view.getContext(), "Process failed..Try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return configView;
    }
}
