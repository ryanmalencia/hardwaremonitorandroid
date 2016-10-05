package com.example.ryan.hardwaremonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

public class ListSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_settings);
        String path = this.getFilesDir().toString();
        File f = new File(path);
        File [] files = f.listFiles();
        LinearLayout layout = (LinearLayout)findViewById(R.id.the_layout);
        for(File file : files)
        {
            Button button = new Button(getApplicationContext());
            button.setText(file.getName().replace(".bin",""));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MySettings.class);
                    intent.putExtra("port_number",((Button)v).getText());
                    startActivity(intent);
                    finish();
                }
            });
            layout.addView(button);
        }
    }
}
