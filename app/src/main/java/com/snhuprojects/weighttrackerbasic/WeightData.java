package com.snhuprojects.weighttrackerbasic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WeightData extends AppCompatActivity {

    Button btn_logout, btn_addEntry, btn_settings, btn_editEntry, btn_deleteEntry;
    String username = "";
    WeightModel weightModel;
    RecyclerView rv_weight;
    DatabaseHelperUserWeight databaseHelperUserWeight;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //TODO
    // Add in method for deleting and sorting
    // Delta calculation -> will be done in DatabaseHelperUserWeight to allow weight.db update with setting/changing of the goal weight.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_data);

        btn_logout = findViewById(R.id.btn_logout);
        btn_settings = findViewById(R.id.btn_settings);
        btn_addEntry = findViewById(R.id.btn_addEntry);
        btn_editEntry = findViewById(R.id.btn_editEntry);
        btn_deleteEntry = findViewById(R.id.btn_deleteEntry);
        databaseHelperUserWeight = new DatabaseHelperUserWeight(WeightData.this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("key");
            //The key argument here must match that used in the other activity
        }

        btn_addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeightData.this, AddEntry.class);
                intent.putExtra("key", username);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS)  == PackageManager.PERMISSION_GRANTED) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(WeightData.this);
                        String phonenumber = databaseHelper.getPhoneNumber(username);
                        String SMS = "Thank you for using Weight Tracker basic! Check out our website for workouts and diet planning tips.";

                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phonenumber, null, SMS, null, null);
                            Toast.makeText(WeightData.this, "Message sent", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(WeightData.this, "Error sending sms", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        requestPermissions(new String[] {Manifest.permission.SEND_SMS}, 1);
                    }
                }
                Intent intent = new Intent(WeightData.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // start setting activity passing the username with the intent
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeightData.this, Settings.class);
                intent.putExtra("key", username);
                startActivity(intent);
            }
        });

        //start the edit entry activity with the username in the intent
        btn_editEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeightData.this, EditEntry.class);
                intent.putExtra("key", username);
                startActivity(intent);
            }
        });

        //start the delete activity with the username in the intent
        btn_deleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeightData.this, DeleteEntry.class);
                intent.putExtra("key", username);
                startActivity(intent);
            }
        });

        //identify the recycler view
        recyclerView = findViewById(R.id.rv_weights);

        // this setting improves performance
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new RecycleViewAdapter(databaseHelperUserWeight.getUserWeights(username), WeightData.this);
        recyclerView.setAdapter(mAdapter);
    }
    //Send sms function
    DatabaseHelper databaseHelper = new DatabaseHelper(WeightData.this);
}