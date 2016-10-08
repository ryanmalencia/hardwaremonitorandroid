package com.example.ryan.hardwaremonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MySettings extends AppCompatActivity {
    TextView core;
    TextView ram;
    TextView temp;
    Settings mySettings;
    String IP;
    String mach_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        IP = getIntent().getStringExtra("port_number");
        mach_name = getIntent().getStringExtra("mach_name");
        core = (TextView) findViewById(R.id.maxCore);
        ram = (TextView) findViewById(R.id.maxRam);
        temp = (TextView) findViewById(R.id.maxTemp);
        setTitle("Settings");
        File file = new File(getFilesDir() + "/" + IP + ".bin");
        if(file.exists()) {
            try {
                System.out.println("restoring from binary");
                FileInputStream fis = openFileInput(IP + ".bin");
                ObjectInputStream ois = new ObjectInputStream(fis);
                mySettings = (Settings) ois.readObject();
                ois.close();
            }
            catch(Exception e)
            {
                System.out.println("File not found");
            }
        }
        else
        {
            mySettings = new Settings();
        }
        core.setText(mySettings.max_core);
        ram.setText(mySettings.max_ram);
        temp.setText(mySettings.max_temp);
    }

    private void saveSettings()
    {
        try {
            Settings tempSettings = new Settings();

            tempSettings.max_temp = temp.getText().toString();
            tempSettings.max_ram = ram.getText().toString();
            tempSettings.max_core = core.getText().toString();
            tempSettings.IP = IP;
            tempSettings.mach_name = mach_name;

            FileOutputStream fos = openFileOutput(IP + ".bin", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tempSettings);
            oos.flush();
            oos.close();

        }catch(IOException e)
        {
            System.out.println("Serialization gone bad");
            System.out.println(e.getMessage());
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("port_number", IP);
        intent.putExtra("mach_name", mach_name);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            saveSettings();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
