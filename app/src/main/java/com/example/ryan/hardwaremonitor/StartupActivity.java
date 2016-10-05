package com.example.ryan.hardwaremonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class StartupActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layout;
    private MyDatagramReceiver myDatagramReceiver = null;
    private List<String> lastPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lastPort = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        layout = (LinearLayout)findViewById(R.id.startup_layout);
        myDatagramReceiver = new MyDatagramReceiver();
        myDatagramReceiver.start();
        /*
        for(File temp : files)
        {
            if(temp.getName().contains(".bin")) {
                Intent intent = new Intent(this, MainActivity.class);
                String port;
                Settings mySettings = new Settings();
                try{
                    FileInputStream fis = openFileInput(temp.getName());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    mySettings = (Settings) ois.readObject();
                    ois.close();
                }catch(IOException e)
                {
                    System.out.println("Error reading settings file");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                port = mySettings.IP;
                intent.putExtra("port_number",port);
                startActivity(intent);
                finish();
                break;
            }
        }
        */
    }

    public void onClick(View view)
    {
        /*String port = ((EditText)findViewById(R.id.editText)).getText().toString();
        if(port.split("\\.").length >= 4) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("port_number", port);
            startActivity(intent);
            finish();
        }
        else
        {

        }
        */
    }



    private class MyDatagramReceiver extends Thread {
        private boolean bKeepRunning = true;
        private String lastMessage = "";

        public void run() {
            String message;
            byte[] lmessage = new byte[800];
            DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
            DatagramSocket socket;
            while (bKeepRunning) {
                try {

                    socket = new DatagramSocket(11000);
                    socket.receive(packet);
                    message = new String(lmessage, 0, packet.getLength());
                    lastMessage = message;
                    runOnUiThread(updateTextMessage);
                    socket.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        private String getLastMessage() {
            return lastMessage;
        }
    }

    private Runnable updateTextMessage = new Runnable() {
        public void run() {
        if (myDatagramReceiver == null) return;
        final String port = myDatagramReceiver.getLastMessage();
        if(lastPort.size() == 0)
        {
            layout.removeAllViews();
        }
        if (!lastPort.contains(port)) {
            Button button = new Button(getApplicationContext());
            button.setText(port);
            button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if (port.split("\\.").length >= 4) {

                          File file = new File(port.replace(".", "") + ".bin");

                          if(!file.exists()) {
                              try {
                                  Settings settings = new Settings();
                                  FileOutputStream fos = openFileOutput(port + ".bin", MODE_PRIVATE);
                                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                                  oos.writeObject(settings);
                                  oos.flush();
                                  oos.close();
                              } catch (Exception e) {
                                  System.out.println("Error saving file");
                              }
                          }

                          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                          intent.putExtra("port_number", port);
                          startActivity(intent);
                          finish();
                      } else {
                            System.out.println("Bad port number");
                      }
                  }
                }
            );
            layout.addView(button);
        }
        lastPort.add(port);
        }
    };
}
