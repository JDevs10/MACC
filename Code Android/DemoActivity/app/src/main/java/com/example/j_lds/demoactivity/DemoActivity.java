package com.example.j_lds.demoactivity;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DemoActivity extends AppCompatActivity {

    private final String TAG = DemoActivity.class.getSimpleName();
    private UsbSerialDriver mSerialDevice;
    private UsbManager mUsbManager;

    private TextView mTitleTextView;
    private TextView mDumpTextView;
    private ScrollView mScrollView;

    //stored an ascii character
    private String ascii1;
    //have the full message from arduino which is initialized to nothing
    private String arduinoFullMessage = "";

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    //get the serial input from arduino
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                //a thread that's listening for a message from arduino which comes in byte array
                //pass the byte array message to updateReceivedData and start the methode
                @Override
                public void onNewData(final byte[] data) {
                    DemoActivity.this.runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            DemoActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };

    //once the application is created
    //set the layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mTitleTextView = (TextView) findViewById(R.id.demoTitle);
        mDumpTextView = (TextView) findViewById(R.id.demoText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);

    }

    //stop Serial SnputOutput Manager
    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (mSerialDevice != null) {
            try {
                mSerialDevice.close();
            } catch (IOException e) {
                // Ignore.
            }
            mSerialDevice = null;
        }
    }

    //recommence Serial inputOutput Manager
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        mSerialDevice = UsbSerialProber.acquire(mUsbManager);
        Log.d(TAG, "Resumed, mSerialDevice=" + mSerialDevice);
        //if the serial is not connected
        if (mSerialDevice == null) {
            mTitleTextView.setText("No serial device.");
        } else {
            //if serial is connected
            //try opening the connecting
            try {
                mSerialDevice.open();
            } catch (IOException e) {
                //if serial faild to open, notify the user
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    //try closing the connection
                    mSerialDevice.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                //set serial to null
                mSerialDevice = null;
                return;
            }
            //serial opened and connected
            mTitleTextView.setText("Serial device: " + mSerialDevice);
        }
        //start serial inputOutput manager
        onDeviceStateChange();
    }

    //stop Serial SnputOutput Manager
    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    //start Serial SnputOutput Manager
    private void startIoManager() {
        if (mSerialDevice != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(mSerialDevice, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    //stop then start inputOuputManager
    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    //Receiving message from arduino in byte array
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateReceivedData(byte[] data) {
        try {
            //Returns true if the caller has permission to access the accessory
            mUsbManager.hasPermission(mSerialDevice.getDevice());
            //Set serial speed 9600 baud bps (bits per second)
            mSerialDevice.setBaudRate(9600);

            synchronized (mSerialIoManager.getListener()) {
                //String ascii = new String(data,0,data.length,"ASCII");
                ascii1 = new String(data, "UTF-8");
                arduinoFullMessage += ascii1;

                if (arduinoFullMessage.endsWith("adrduino is ending")){
                    arduinoFullMessage = arduinoFullMessage.replaceAll("adrduino is ending","");
                    String msg = "Arduino Data length : " + ascii1.length() + " \nArduino Data :'" + arduinoFullMessage + "'\n\n";
                    mDumpTextView.append(msg);
                    mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
                    arduinoFullMessage = "";
                }
            }
                /*
                final String message = "Read " + data.length + " bytes: \n"
                        + HexDump.dumpHexString(data) + "\n\n";
                mDumpTextView.append(message);
                mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
                */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
