package me.kevinfoster.androidtoraspberrypi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.samsung.android.sdk.pass.SpassFingerprint;

/**
 * Created by kevinfoster on 5/21/16.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText password;
    private Button loginButton;
    private NotificationManager mManager;
    private SpassFingerprint test;
    private int clicknum;
    private boolean running;


    protected void onCreate(Bundle savedInstanceState) { //Builds app
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        password = (EditText) findViewById(R.id.passwordText);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals("12345")) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    password.setText("");
                    startActivity(i);
                    if (running) {
                        test.cancelIdentify();
                    }
                }

            }
        });
        clicknum = 0;
        running = false;
        setVolumeControlStream(3);
        test = new SpassFingerprint(this);
        runThumbPrint();

    }


    /**
     * Thumbprint portion of app feature
     */
    private void runThumbPrint() {


        SpassFingerprint.IdentifyListener initialize = new SpassFingerprint.IdentifyListener() {
            @Override
            public void onFinished(int i) {
                if (test.getIdentifiedFingerprintIndex() > 0) {
                    clicknum++;
                    runPush();
                }
                //else if(i=SpassFingerprint.STATUS_TIMEOUT_FAILED)
//                else {
//                    Toast.makeText(getApplicationContext(), "Denied", Toast.LENGTH_SHORT).show();
//                }
                running = false;

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        runThumbPrint();
                    }
                };
                t.run();
            }

            @Override
            public void onReady() {
                if (running) {
                    try {
                        test.cancelIdentify();
                    }
                    catch (Exception e) {
                    e.printStackTrace();
                    }
                }
//                if (clicknum < 1) {
//                    Toast.makeText(getApplicationContext(), "Scanner Functional", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onStarted() {

                running = true;
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 50 milliseconds
                v.vibrate(50);
            }

            @Override
            public void onCompleted() {
                Toast.makeText(getApplicationContext(), "Executed", Toast.LENGTH_SHORT).show();


            }
        };

        test.startIdentify(initialize);


    }

    /**
     * Push notifications identify lock status
     */
    private void runPush() {
        Intent resultIntent = new Intent(this, LoginActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);//Lock Status Disengaged
        if (clicknum % 2 != 0 || clicknum == 1) {


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
            MediaPlayer engageSound = MediaPlayer.create(this, R.raw.engage);//Queue Audio engage
            engageSound.start();

        } else {//Lock Status Engaged

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
            MediaPlayer disengageSound = MediaPlayer.create(this, R.raw.disengage);//Queue Audio disengage
            disengageSound.start();
        }

    }


//    public void onPause() {
//        super.onPause();
//        finish();
//    }


}

