package com.duxca.app;

import java.io.*;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.usb.*;
import com.hoho.android.usbserial.driver.*;
import android.content.*;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    UsbManager manager;
    UsbSerialDriver usb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usb = UsbSerialProber.acquire(manager);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, usb != null ? "usb detected." :"usb not detected.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (usb != null) {
                    try{
                        usb.open();
                        usb.setBaudRate(9600);
                        start_read_thread(); // シリアル通信を読むスレッドを起動
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public void start_read_thread(){
        new Thread(new Runnable(){
            public void run(){
                Log.v("usb serial thread", "thread running.");
                try{
                    while(true){
                        byte buf[] = new byte[256];
                        int num = usb.read(buf, buf.length);
                        if(num > 0) Log.v("usb arduino", new String(buf, 0, num)); // Arduinoから受信した値をlogcat出力
                        Thread.sleep(10);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
