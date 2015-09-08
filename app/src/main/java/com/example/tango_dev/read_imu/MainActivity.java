package com.example.tango_dev.read_imu;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.hardware.*;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private String TAG = "mylog";
    private SensorManager my_sensor_manager;
    private Sensor sensor_accel;
    private Sensor sensor_gyro;
    private boolean gyro_enabled;

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

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){
        Log.i(TAG,sensor.getName()+"Sensor Accuracy Changed");
    }

    @Override
    public final void onSensorChanged(SensorEvent event){

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            Log.i(TAG,"A: ");
            final String log_msg_accel = String.valueOf(event.timestamp) + " " + String.valueOf(event.values[0]) + "," + String.valueOf(event.values[1]) + "," + String.valueOf(event.values[2]);
            Log.i(TAG,log_msg_accel);
        }
        if((event.sensor.getType()==Sensor.TYPE_GYROSCOPE)&&(gyro_enabled)){
            Log.i(TAG,"G: ");
            final String log_msg_gyro = String.valueOf(event.timestamp) + " " + String.valueOf(event.values[0]) + "," + String.valueOf(event.values[1]) + "," + String.valueOf(event.values[2]);
            Log.i(TAG,log_msg_gyro);
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
}
