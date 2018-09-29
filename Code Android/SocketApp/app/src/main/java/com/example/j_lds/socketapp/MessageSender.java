package com.example.j_lds.socketapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender extends AsyncTask<String,Void,Void> {

    Socket s;
    DataOutputStream dos;
    PrintWriter pw;

    @Override
    protected Void doInBackground(String... voids) {
        String msg = voids[0];

        try {
            s = new Socket("192.168.1.74", 1060);
            pw = new PrintWriter(s.getOutputStream());
            pw.write(msg);
            pw.flush();
            pw.close();
            s.close();


        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
