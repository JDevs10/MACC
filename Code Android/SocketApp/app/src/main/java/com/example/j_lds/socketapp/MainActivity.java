package com.example.j_lds.socketapp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText et;
    Button b;

    String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.editText);
        b = (Button) findViewById(R.id.button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        //start thread to receive data from the server
        new Thread(receiveServerData).start();
    }

    //create Runnable to receive a socket from the server
    Runnable receiveServerData = new Runnable() {
        Socket s;
        ServerSocket ss;
        InputStreamReader isr;
        BufferedReader br;
        Handler h = new Handler();

        @Override
        public void run() {
            try {
                //listening on port 1061 for a serverSocket
                ss = new ServerSocket(1061);

                //infinity
                while (true) {
                    s = ss.accept();
                    isr = new InputStreamReader(s.getInputStream());
                    br = new BufferedReader(isr);

                    message = br.readLine();

                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            //decrypt the message
                            decryptServerData();
                            //show crypt message from the server
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    };


    public void send(){
        MessageSender messageSender = new MessageSender();
        messageSender.execute(et.getText().toString());
    }

    public void decryptServerData(){
        //decrypting the message by splitting it
        //the server must use ";" to separate the ACs name
        String[] serverDataStr = message.split(";");
        //get the fist separate info that is the number of ACs + 1 (itself)
        int totalACs = Integer.parseInt(serverDataStr[0]);

        //create a list of items for the spinner.
        ArrayList<String> arrayList = new ArrayList<String>();

        //store in the list all the Air Conditioner names the serve send me
        for (int i = 2; i < totalACs; i++ ){
            arrayList.add(serverDataStr[i]);
        }

        //get the spinner from the xml.
        Spinner spinner = findViewById(R.id.spinner_ac);

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);

        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter);

        //when spinner selected an item in the list
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAC = parent.getItemAtPosition(position).toString();
                Toast.makeText(getBaseContext(), "AC selected : " + selectedAC, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });

    }

}
