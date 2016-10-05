package com.example.ryan.hardwaremonitor;

import java.io.Serializable;

class Settings implements Serializable{

    String max_temp;
    String max_core;
    String max_ram;
    String IP;
    private static final long serialVersionUID = 12345678;

    Settings()
    {
        max_temp = "100";
        max_core = "1000";
        max_ram = "16384";
        IP = "10.0.0.1";
    }
}
