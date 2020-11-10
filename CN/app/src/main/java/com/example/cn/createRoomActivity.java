package com.example.cn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class createRoomActivity extends AppCompatActivity {


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://192.168.0.25:5000");
        } catch (URISyntaxException e) {
            Log.i("error",e.getMessage().toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        mSocket.connect();
//        Log.i("localid",)
        Log.i("socket id = ",mSocket.id().toString());
    }
}