package com.example.cnchat;


import android.app.Application;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

public class SocketInstance extends Application {
    private Socket iSocket;

    //192.168.0.255
    private static final String URL =  "";
    @Override


    public void onCreate() {
        super.onCreate();
    }

    public Socket getSocketInstance(){
        return iSocket;
    }
}