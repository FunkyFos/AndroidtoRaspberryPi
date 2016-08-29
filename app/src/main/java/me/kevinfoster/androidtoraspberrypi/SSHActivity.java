package me.kevinfoster.androidtoraspberrypi;

import android.content.Intent;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Created by Kevno on 7/3/2016.
 */
public class SSHActivity extends AppCompatActivity{
    public void main(String[] args) {
        String host="me.kevinfoster.androidtoraspberrypi";
        String user="pi";
        String password="Battlefield19991944";
        String command1="Python";

        try{

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    System.out.print(new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
            Intent q = new Intent(SSHActivity.this, LoginActivity.class);
            Toast.makeText(getApplicationContext(), "Command Sent", Toast.LENGTH_SHORT).show();
            startActivity(q);
          

        }catch(Exception e){
            e.printStackTrace();
        }


    }
}
