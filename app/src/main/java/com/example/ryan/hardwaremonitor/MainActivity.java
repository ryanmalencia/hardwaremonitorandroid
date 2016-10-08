package com.example.ryan.hardwaremonitor;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.*;
import java.net.*;

public class MainActivity extends AppCompatActivity {

    private int max_clock;
    private int max_ram;
    private int max_temp;
    private Settings mySettings;
    private String IP;
    private String mach_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IP = getIntent().getStringExtra("port_number");
        mach_name = getIntent().getStringExtra("mach_name");

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


        max_clock = Integer.parseInt(mySettings.max_core);
        max_ram = Integer.parseInt(mySettings.max_ram);
        max_temp = Integer.parseInt(mySettings.max_temp);

        new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {

                MachData();
            }

            public void onFinish() {
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //call to get data
    public void MachData() {
        new GetData().execute("MachData", IP);
    }

    //open settings activity and finish this activity
    public void openSettings(MenuItem item)
    {
        Intent intent = new Intent(this, MySettings.class);
        intent.putExtra("port_number", IP);
        intent.putExtra("mach_name", mach_name);
        startActivity(intent);
        finish();
    }

    //retrieve data from the server
    class GetData extends AsyncTask<String, Void, String>{
        String the_input;
        String port;
        @Override
        protected String doInBackground(String... input) {
            the_input = input[0];
            port = input[1];
            try(
                Socket kkSocket = new Socket()
            ){
                kkSocket.connect(new InetSocketAddress(port,9999), 1000);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
                String fromServer;
                out.println(input[0]);
                while ((fromServer = in.readLine()) != null) {
                    if (!fromServer.equals(""))
                        return fromServer;
                }
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + port);
                return "Bad hostname";
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + port);
                return "No connection to server";
            }
            return "Oops";
        }
        protected void onPostExecute(String input) {
            if(the_input.equals("MachData"))
            {
                String [] types = input.split("!");
                LinearLayout layout = (LinearLayout)findViewById((R.id.data));
                assert layout != null;
                layout.removeAllViewsInLayout();
                String output = "";
                for(String type: types)
                {
                    TextView temp = new TextView(getApplicationContext());
                    temp.setText(type);
                    temp.setTextSize(20);
                    temp.setPadding(0,20,0,20);
                    temp.setTextColor(Color.BLACK);
                    output = output + type + "\n";
                    layout.addView(temp);
                    if(type.contains("CPU"))
                    {
                        ProgressBar usage = new ProgressBar(getApplicationContext(),null, android.R.attr.progressBarStyleHorizontal);

                        String usage_amount = type.split(":")[1].trim();
                        int usage_int;
                        if(usage_amount.contains(".")) {
                            System.out.println(usage_amount);
                             usage_int = Integer.parseInt(usage_amount.split("\\.")[0]);
                        }else
                        {
                             usage_int = Integer.parseInt(usage_amount.split("%")[0]);
                        }
                        if(usage_int > 50)
                        {
                            usage.setProgressTintList(ColorStateList.valueOf(Color.RED));
                        }
                        usage.setProgress(usage_int);
                        layout.addView(usage);
                    }
                    else if(type.contains("RAM Free"))
                    {
                        ProgressBar usage = new ProgressBar(getApplicationContext(),null, android.R.attr.progressBarStyleHorizontal);

                        String usage_amount = type.split(":")[1].trim();
                        int usage_int;
                        if(usage_amount.contains(".")) {

                            usage_int = Integer.parseInt(usage_amount.split("\\.")[0]);
                        }else
                        {
                            usage_int = Integer.parseInt(usage_amount.split("M")[0]);
                        }
                        usage_int = max_ram -usage_int;
                        usage_int = (usage_int*100)/ max_ram;
                        System.out.println(usage_int);

                        if(usage_int > 50)
                        {
                            usage.setProgressTintList(ColorStateList.valueOf(Color.RED));
                        }
                        usage.setProgress(usage_int);
                        layout.addView(usage);
                    }
                    else if(type.contains("GPU Usage"))
                    {
                        ProgressBar usage = new ProgressBar(getApplicationContext(),null, android.R.attr.progressBarStyleHorizontal);

                        String usage_amount = type.split(":")[1].trim();
                        int usage_int;
                        if(usage_amount.contains(".")) {
                            System.out.println(usage_amount);
                            usage_int = Integer.parseInt(usage_amount.split("\\.")[0]);
                        }else
                        {

                            String [] temp_usage = usage_amount.split("%");
                            if(temp_usage.length > 0)
                                usage_int = Integer.parseInt(usage_amount.split("%")[0]);
                            else
                                usage_int = 0;
                        }
                        if(usage_int > 85)
                        {
                            usage.setProgressTintList(ColorStateList.valueOf(Color.RED));
                        }
                        usage.setProgress(usage_int);
                        layout.addView(usage);
                    }
                    else if(type.contains("GPU Temp"))
                    {
                        ProgressBar usage = new ProgressBar(getApplicationContext(),null, android.R.attr.progressBarStyleHorizontal);

                        String usage_amount = type.split(":")[1].trim();
                        int usage_int;
                        String [] usage_temp = usage_amount.split("C");

                        if(usage_temp.length > 0)
                            usage_int = Integer.parseInt(usage_amount.split("C")[0]);
                        else
                            usage_int = 0;

                        usage_int = (usage_int * 100)/ max_temp;

                        if(usage_int > 65)
                        {
                            usage.setProgressTintList(ColorStateList.valueOf(Color.RED));
                        }
                        usage.setProgress(usage_int);
                        layout.addView(usage);
                    }
                    else if(type.contains("Core Clock"))
                    {
                        ProgressBar usage = new ProgressBar(getApplicationContext(),null, android.R.attr.progressBarStyleHorizontal);

                        String usage_amount = type.split(":")[1].trim();
                        double usage_double;

                        try {
                            usage_double = Double.parseDouble(usage_amount);
                        }catch(NumberFormatException e)
                        {
                            usage_double = 0;
                        }

                        int usage_int = (int)(usage_double*100)/ max_clock;

                        if(usage_int > 65)
                        {
                            usage.setProgressTintList(ColorStateList.valueOf(Color.RED));
                        }
                        usage.setProgress(usage_int);
                        layout.addView(usage);
                    }
                }
            }
            else {
                System.out.println(input);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(this, StartupActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}