package com.example.latinodistribuidora.Actividades;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.latinodistribuidora.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Fragment_FinalV extends Fragment {
    View vista;
    // will show the statuses like bluetooth open, close or data sent
    TextView myLabel;

    // will enable user to enter any text to be printed
    EditText myTextbox;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment__final_v, container, false);
        try {
            // more codes will be here
            // we are going to have three buttons for specific functions
            Button openButton = (Button) vista.findViewById(R.id.open);
            Button sendButton = (Button) vista.findViewById(R.id.send);
            Button closeButton = (Button) vista.findViewById(R.id.close);

            // text label and input box
            myLabel = (TextView) vista.findViewById(R.id.label);
            myTextbox = (EditText) vista.findViewById(R.id.entry);

            // open bluetooth connection
            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        findBT();
                        openBT();
                    } catch (IOException ex) {
                        Toast.makeText(getContext(),"open: "+ex.getMessage(),Toast.LENGTH_LONG).show();
                        //ex.printStackTrace();
                    }
                }
            });

            // send data typed by the user to be printed
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sendData();
                    } catch (IOException ex) {
                        Toast.makeText(getContext(),"send: "+ex.getMessage(),Toast.LENGTH_LONG).show();
                        //ex.printStackTrace();
                    }
                }
            });

            // close bluetooth connection
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        closeBT();
                    } catch (IOException ex) {
                        Toast.makeText(getContext(),"close: "+ex.getMessage(),Toast.LENGTH_LONG).show();
                        //ex.printStackTrace();
                    }
                }
            });
        }catch(Exception e) {
            Toast.makeText(getContext(),"FATAL: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
        return vista;
    }
    // this will find a bluetooth printer device
    private void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                myLabel.setText("No hay adaptador bluetooth disponible.");
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    //if (device.getName().equals("RPP300")) {
                    if (device.getName().equals("MTP-3")) {
                        mmDevice = device;
                        break;
                    }
                }
            }

            myLabel.setText("Se encontró un dispositivo Bluetooth.");

        }catch(Exception e){
            Toast.makeText(getContext(),"findBT: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    private void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();


            beginListenForData();

            myLabel.setText("Bluetooth abierto.");

        } catch (Exception e) {
            Toast.makeText(getContext(),"openBT: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }


    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    private void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            Toast.makeText(getContext(),"beginListenForData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    private void sendData() throws IOException {
        try {

            // the text typed by the user
            String msg = myTextbox.getText().toString();
            msg += "\n";

            mmOutputStream.write(msg.getBytes());
            //mmInputStream.read(msg.getBytes());

            // tell the user data were sent
            myLabel.setText("Datos enviados.");

        } catch (Exception e) {
            Toast.makeText(getContext(),"sendData: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    // close the connection to bluetooth printer.
    private void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth cerrado");
        } catch (Exception e) {
            Toast.makeText(getContext(),"closeBT: "+e.getMessage(),Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }
}