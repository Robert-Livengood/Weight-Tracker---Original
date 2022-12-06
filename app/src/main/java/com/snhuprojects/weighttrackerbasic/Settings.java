package com.snhuprojects.weighttrackerbasic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    EditText et_goalWeight, et_phoneNumber, et_changePassword;
    Switch sw_textPerms;
    Button btn_apply, btn_home;
    DatabaseHelper databaseHelper;
    String username = "Error";
    private int SMS_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        et_goalWeight = findViewById(R.id.et_goalWeight);
        et_phoneNumber = findViewById(R.id.et_phoneNumber);
        et_changePassword = findViewById(R.id.et_changePassword);
        sw_textPerms = findViewById(R.id.sw_textPerms);
        btn_apply = findViewById(R.id.btn_apply);
        btn_home = findViewById(R.id.btn_home);

        //get username included with intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("key");
            // Key must match from the screen that passed the intent to here.
        }

        // Get the current smsPerms
        DatabaseHelper databaseHelper =  new DatabaseHelper(Settings.this);
        int smsPerms = databaseHelper.getSmsPerms(username);
        if (smsPerms == 1) {
            sw_textPerms.setChecked(true);
        }

        // Cancel and return home button logic
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, WeightData.class);
                intent.putExtra("key", username);
                startActivity(intent);
            }
        });

        // Apply changes button logic
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String goalWeight = et_goalWeight.getText().toString();
                String phoneNumber = et_phoneNumber.getText().toString();
                String newPassword = et_changePassword.getText().toString();
                boolean smsPerms = sw_textPerms.isChecked();

                // update goal weight
                if (!goalWeight.matches("")) {
                    // user.db instance
                    DatabaseHelper databaseHelper = new DatabaseHelper(Settings.this);
                    boolean success = databaseHelper.updateGoalWeight(username, Integer.parseInt(et_goalWeight.getText().toString()));
                    if (success){
                        Toast.makeText(Settings.this, "Goal updated successfully.", Toast.LENGTH_SHORT).show();
                    }
                }
                // update weight.db delta values upon changing the goal weight
                if (!goalWeight.matches("")) {
                    // weight.db instance
                    DatabaseHelperUserWeight databaseHelperUserWeight = new DatabaseHelperUserWeight(Settings.this);
                    boolean success = databaseHelperUserWeight.updateDelta(username, Integer.parseInt(et_goalWeight.getText().toString()));
                    if (success){
                        Toast.makeText(Settings.this, "Delta updated successfully.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Settings.this, "Delta update failed.", Toast.LENGTH_SHORT).show();
                    }
                }
                // update user.db with new phone number
                if (!phoneNumber.matches("")) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(Settings.this);
                    boolean success = databaseHelper.updatePhoneNumber(username, et_phoneNumber.getText().toString());
                    if (success){
                        Toast.makeText(Settings.this, "Phone number updated successfully.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Settings.this, "Phone number update failed.", Toast.LENGTH_SHORT).show();
                    }
                }
                // update user.db with new password
                if (!newPassword.matches("")) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(Settings.this);
                    boolean success = databaseHelper.updatePassword(username, newPassword);
                    if (success){
                        Toast.makeText(Settings.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Settings.this, "Password number update failed.", Toast.LENGTH_SHORT).show();
                    }
                }

                // update user.db with sms perms value ->
                //TODO
                // still have to implement perms w/ android settings
                if (smsPerms) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(Settings.this);
                    boolean success = databaseHelper.updateSmsPerms(username, 1);
                    if (ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(Settings.this, "Permission already granted.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        requestSMSPermission();
                    }
                    if (success){
                        Toast.makeText(Settings.this, "SMS permissions updated successfully.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Settings.this, "SMS permissions update failed.", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!smsPerms) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(Settings.this);
                    boolean success = databaseHelper.updateSmsPerms(username, 0);
                    if (success){
                        Toast.makeText(Settings.this, "SMS permissions updated successfully.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Settings.this, "SMS permissions update failed.", Toast.LENGTH_SHORT).show();
                    }
                }


                // Return home AFTER updating user model
                Intent intent = new Intent(Settings.this, WeightData.class);
                intent.putExtra("key", username);
                startActivity(intent);
            }
        });
    }

    // sms permission method
    private void requestSMSPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            new AlertDialog.Builder(this).setTitle("Permission needed").setMessage("This permission is needed to send workout and diet websites.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(Settings.this, new String[] {Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Permission DENIED",Toast.LENGTH_SHORT).show();

            }
        }
    }
}