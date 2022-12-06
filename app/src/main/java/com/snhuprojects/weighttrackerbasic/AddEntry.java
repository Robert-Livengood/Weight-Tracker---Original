package com.snhuprojects.weighttrackerbasic;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class AddEntry extends AppCompatActivity {

    // initializations
    EditText et_weight;
    Button btn_enter;
    Button btn_cancel;
    String username = "";
    Button btn_datePicker;
    private DatePickerDialog datePickerDialog;

    //TODO
    // enforce unique dates for each user.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        // initialize date picker
        initDatePicker();
        btn_datePicker = findViewById(R.id.btn_datePicker);
        btn_datePicker.setText(getTodaysDate());
        // assign buttons to corresponding view variables
        et_weight = findViewById(R.id.et_weight);
        btn_enter = findViewById(R.id.btn_enter);
        btn_cancel = findViewById(R.id.btn_cancel);

        //get username from intent
        Bundle extras = getIntent().getExtras();
        //The key argument here must match that used in the other activity
        username = extras.getString("key");

        // logic for enter button
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // logic to get data from edit texts and add to weight.db
                WeightModel weightModel;

                // Confirm that a date was entered
                String weightEntered = et_weight.getText().toString();
                // if no weight entered -> toast error message
                if (weightEntered.matches("")) {
                    Toast.makeText(AddEntry.this, "Error: No weight entered.", Toast.LENGTH_SHORT).show();
                }
                else {
                    // create WeightModel object with the given information
                    try {
                        weightModel = new WeightModel(-1,username, btn_datePicker.getText().toString(), Integer.parseInt(et_weight.getText().toString()));
                    }
                    catch (Exception e) {
                        Toast.makeText(AddEntry.this, "Error creating entry.", Toast.LENGTH_SHORT).show();
                        weightModel = new WeightModel(-1, "error", "error", 0);
                    }
                    // update weightModel with delta if goal weight is set
                    DatabaseHelper databaseHelper = new DatabaseHelper(AddEntry.this);
                    int goalWeight = databaseHelper.getUserGoalWeight(username);
                    if (goalWeight !=0) {
                        int delta = goalWeight - Integer.parseInt(et_weight.getText().toString());
                        weightModel.setDelta(delta);
                    }
                    // get database instance
                    DatabaseHelperUserWeight databaseHelperUserWeight = new DatabaseHelperUserWeight(AddEntry.this);
                    //return true if there is already a weight entry for the given date
                    boolean dateUsed = databaseHelperUserWeight.findDate(username, btn_datePicker.getText().toString());
                    // if date previously used for the user then do not add, else add
                    if (dateUsed) {
                        Toast.makeText(AddEntry.this, "User already has a weight saved for this date.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // date not used
                        //return true if the above weightModel is added successfully to weight.db
                        Boolean success = databaseHelperUserWeight.addOne(weightModel, username);
                        // Toast success status
                        Toast.makeText(AddEntry.this, "Success = " + success, Toast.LENGTH_SHORT).show();

                        //return to previous screen with same button press
                        Intent intent = new Intent(AddEntry.this, WeightData.class);
                        intent.putExtra("key", username);
                        startActivity(intent);
                    }
                }
            }
        });

        // cancel button -> return home
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEntry.this, WeightData.class);
                startActivity(intent);
            }
        });

    }

    // date picker -> full comments under deleteEntry.java
    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month +1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                String date = makeDateString(day, month, year);
                btn_datePicker.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year,month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    //Return string for the month based on month number
    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}