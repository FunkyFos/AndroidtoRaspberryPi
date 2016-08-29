package me.kevinfoster.androidtoraspberrypi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private int clicknum; //number of clicks
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;
    private BluetoothSocket Connected;
    private NotificationManager mManager;
    private BluetoothDevice Scraw;
    private boolean foundDevice;



private Button sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) { //Builds app
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                activity();
                            }
        });
//        Scraw =
//        clicknum = 0;
        runBluetooth();//Bluetooth optimization for connecting with Raspberry Pi
        setVolumeControlStream(3);
        MediaPlayer startup = MediaPlayer.create(this,R.raw.startup);
        startup.start();

        if (foundDevice) {
            Toast.makeText(getApplicationContext(),"Connection Successful",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"Connection Failed",Toast.LENGTH_SHORT).show();
        }




    }

    /**
     * Main activity of locking Mechanism
     */
    private void activity(){
        clicknum++;
        if (clicknum%2!=0 || clicknum == 1) { //sets to Black/Green (Engages Lock)
            sendButton.setBackgroundColor(0xFF000000);
            sendButton.setTextColor(0xFF009900);
            sendButton.setText("Disengage");
            sendButton.setTextSize(50f);
            MediaPlayer engageSound = MediaPlayer.create(this,R.raw.engage);
            engageSound.start();
            runPush();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 50 milliseconds
            v.vibrate(50);
            //Todo: Sends code to Raspberry Pi that engages lock
        }
        else { //sets to Gray/Red (Disengages Lock)
            sendButton.setBackgroundColor(0xFF747474);
            sendButton.setTextColor(0xFF991C01);
            sendButton.setText("Engaged");
            sendButton.setTextSize(60f);
            MediaPlayer disengageSound = MediaPlayer.create(this,R.raw.disengage);
            disengageSound.start();
            runPush();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 50 milliseconds
            v.vibrate(50);
            //Todo: Sends code to Raspberry Pi that disengages lock
        }

    }



    /**
     * Bluetooth portion of app feature
     */
    private void runBluetooth(){ //ToDo: ListView items are not clickable
        lv = (ListView)findViewById(R.id.listView);
        BA = BluetoothAdapter.getDefaultAdapter();
        foundDevice = false;
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(turnOn,0);
        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();
        for (BluetoothDevice bt: pairedDevices)
            list.add(bt.getName());
        Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        for (BluetoothDevice bt: pairedDevices)
            if (bt.getName().equals("SCH-I535")){
                for (ParcelUuid x : bt.getUuids()) {


                    try {
                        Scraw.createRfcommSocketToServiceRecord(x.getUuid()).connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                foundDevice = true;
            }

        //Todo: ListView has been given clickable - no onclick implementation
    }



    private void runPush(){

        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, LoginActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        if (clicknum%2==0) {
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Lock Status:")
                    .setContentText("Disengaged")
                    .setSmallIcon(R.drawable.unlock)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(resultPendingIntent)
                    .build();
            mManager.notify(0, notification);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

        }
        else {
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Lock Status:")
                    .setContentText("Engaged")
                    .setSmallIcon(R.drawable.lock)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(resultPendingIntent)
                    .build();
            mManager.notify(0, notification);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

        }


    }
//    public void onPause() {
//        super.onPause();
//        finish();
//    }
  }

