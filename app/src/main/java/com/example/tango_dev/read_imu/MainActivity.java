package com.example.tango_dev.read_imu;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.hardware.*;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private String TAG = "mylog";
    private SensorManager my_sensor_manager;
    private Sensor sensor_accel;
    private Sensor sensor_gyro;
    private boolean gyro_enabled;
    private File log_file;
    private String log_directory_name;
    private String log_file_name;
    private String log_file_name_absolute;
    private FileOutputStream log_file_out_stream;
    private PrintWriter log_file_out_print;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get_sensor_list();
        sensor_accel = my_sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ;
        sensor_gyro =  my_sensor_manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensor_gyro!=null){
            Log.i(TAG,"Gyro exists.  Will log it as well");
            gyro_enabled = true;
        }
        else{
            Log.e(TAG,"Gyro Does not exist.Won't log");
            gyro_enabled = false;
        }
        DateFormat dateFormatter = new SimpleDateFormat("yyyMMdd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();

        log_directory_name = make_storage_directory("test_1");
        log_file_name = dateFormatter.format(today)+".txt" ;
        log_file_name_absolute = log_directory_name + log_file_name;
        Log.i(TAG,log_file_name_absolute);

        log_file = new File(log_directory_name,log_file_name);
        try {
            log_file_out_stream = new FileOutputStream(log_file);
            log_file_out_print = new PrintWriter(log_file_out_stream);
        }catch(IOException e){
            e.printStackTrace();
        }
        Log.i(TAG,"onCreate");
    }

    private void get_sensor_list() {
        my_sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors_on_board = my_sensor_manager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor curr_sensor:sensors_on_board){
            Log.i(TAG,curr_sensor.getName());
            Log.i(TAG,String.valueOf(curr_sensor.getMinDelay()));
        }
    }

    public String make_storage_directory(String folder_name){
        String directory_name = null;
        File directory;
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),folder_name);
        Log.i(TAG,directory.getAbsolutePath());
        directory.mkdirs();
        directory_name = directory.getAbsolutePath();
        return directory_name;
    }

    public void write_to_file(String data){
            log_file_out_print.println(data);
    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){
        Log.i(TAG,sensor.getName()+"Sensor Accuracy Changed");
    }

    @Override
    public final void onSensorChanged(SensorEvent event){

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            Log.i(TAG,"A: ");
            final String log_msg_accel = "A: "+String.valueOf(event.timestamp) + " " + String.valueOf(event.values[0]) + "," + String.valueOf(event.values[1]) + "," + String.valueOf(event.values[2]);
//            Log.i(TAG,log_msg_accel);
            write_to_file(log_msg_accel);
        }
        if((event.sensor.getType()==Sensor.TYPE_GYROSCOPE)&&(gyro_enabled)){
            Log.i(TAG,"G: ");
            final String log_msg_gyro = "G: "+String.valueOf(event.timestamp) + " " + String.valueOf(event.values[0]) + "," + String.valueOf(event.values[1]) + "," + String.valueOf(event.values[2]);
//            Log.i(TAG,log_msg_gyro);
            write_to_file(log_msg_gyro);
        }

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

    @Override
    public void onResume(){
        super.onResume();
        my_sensor_manager.registerListener(this,sensor_accel,SensorManager.SENSOR_DELAY_FASTEST);
        if(gyro_enabled){
            my_sensor_manager.registerListener(this,sensor_gyro,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        my_sensor_manager.unregisterListener(this);
        if(gyro_enabled){
            my_sensor_manager.registerListener(this,sensor_gyro,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            log_file_out_print.close();
            log_file_out_stream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
