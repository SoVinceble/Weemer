package com.weemer.weemer.weemer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    Firebase mRef = new Firebase("https://testing01.firebaseio.com/items");



    // Data
    ArrayList<String> mItems = new ArrayList<>();
    ArrayAdapter mAdapter;

    // UI
    RecyclerView mRecyclerView;

    //Sensor variables
    TextView ambientValue;
    TextView[] valueFields = new TextView[1];
    int AMBIENT = 0;
    SensorManager senseManage;
    Sensor envSense;
    String insert;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ambientValue = (TextView) findViewById(R.id.addTextView01);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Create FAB button to display DialogFragment
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        senseManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }


    public void onClick(View v) {


        if (v.getId()==R.id.fab) {
            //ambient temperature

            envSense = senseManage.getDefaultSensor(Sensor.TYPE_PRESSURE);
            if(envSense==null)
                Toast.makeText(this.getApplicationContext(), "Sorry - your device doesn't have an " +
                        "ambient temperature sensor!", Toast.LENGTH_SHORT).show();
            else
               // Toast.makeText(this.getApplicationContext(), "Sorry - your device doesn't have an " +
               //         "ambient temperature sensor!", Toast.LENGTH_SHORT).show();
            senseManage.registerListener(this, envSense, SensorManager.SENSOR_DELAY_NORMAL);



          //  float asddd = (float) 3.121123;
          //  DecimalFormat df = new DecimalFormat();
          //  df.setMinimumFractionDigits(2);
         //   df.setMaximumFractionDigits(2);
            // System.out.println(df.format(asddd));

            ambientValue = (TextView) findViewById(R.id.addTextView01);
            valueFields[AMBIENT] = ambientValue;

            insert = ambientValue.getText().toString();

            mRef.push().setValue(insert);

        }
    }

    public void onItemAdded(String item) {
        mRef.push().setValue(insert);
        mItems.add(insert);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        //retrieve sensor information
        String envInfo ="";
        float sensorValue = event.values[0];
        TextView currValue = ambientValue;

        //check type
        int currType=event.sensor.getType();
        switch(currType){
            case Sensor.TYPE_PRESSURE:
                envInfo = sensorValue + " (mha)";
                currValue=valueFields[AMBIENT];
            default: break;
        }
        //output and reset
        currValue.setText(envInfo);
        envSense=null;
        senseManage.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        senseManage.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        //find out the accuracy
        String accuracyMsg = "";
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                accuracyMsg = "Sensor has high accuracy";
                break;

        }

        //output it
        Toast accuracyToast = Toast.makeText(this.getApplicationContext(), accuracyMsg, Toast.LENGTH_SHORT);
        accuracyToast.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<String, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<String, MessageViewHolder>(
                String.class,
                android.R.layout.two_line_list_item,
                MessageViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder messageViewHolder, String s, int i) {

                messageViewHolder.mText.setText(s);
            }
        };
        mRecyclerView.setAdapter(adapter);

    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView mText;

        public MessageViewHolder(View v) {
            super(v);

            mText = (TextView) v.findViewById(android.R.id.text1);
        }
    }

}

