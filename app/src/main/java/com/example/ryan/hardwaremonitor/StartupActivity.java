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
    private List<String> last_IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        last_IP = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        layout = (LinearLayout)findViewById(R.id.startup_layout);
        myDatagramReceiver = new MyDatagramReceiver();
        myDatagramReceiver.start();
    }

    public void onClick(View view)
    {
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
        String receivedMessage = myDatagramReceiver.getLastMessage();
        final String IP = receivedMessage.split(":")[0];
        final String name = receivedMessage.split(":")[1];
        if(last_IP.size() == 0)
        {
            layout.removeAllViews();
        }
        if (!last_IP.contains(IP)) {
            Button button = new Button(getApplicationContext());
            button.setText(name);
            button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if (IP.split("\\.").length >= 4) {

                          File file = new File(IP.replace(".", "") + ".bin");

                          if(!file.exists()) {
                              try {
                                  Settings settings = new Settings();
                                  settings.mach_name = name;
                                  settings.IP = IP;
                                  FileOutputStream fos = openFileOutput(IP + ".bin", MODE_PRIVATE);
                                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                                  oos.writeObject(settings);
                                  oos.flush();
                                  oos.close();
                              } catch (Exception e) {
                                  System.out.println("Error saving file");
                              }
                          }

                          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                          intent.putExtra("port_number", IP);
                          intent.putExtra("mach_name",name);
                          startActivity(intent);
                          finish();
                      } else {
                            System.out.println("Bad IP number");
                      }
                  }
                }
            );
            layout.addView(button);
        }
        last_IP.add(IP);
        }
    };
}
