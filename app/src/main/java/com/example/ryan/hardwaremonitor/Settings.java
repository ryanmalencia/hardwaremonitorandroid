package com.example.ryan.hardwaremonitor;

import java.io.Serializable;

class Settings implements Serializable{

    String max_temp;
    String max_core;
    String max_ram;
    String max_mem;
    String IP;
    String mach_name;
    private static final long serialVersionUID = 12345678;

    Settings()
    {
        max_temp = "100";
        max_core = "1000";
        max_ram = "16384";
        max_mem = "1500";
        IP = "10.0.0.1";
        mach_name = "New Machine";
    }
}
