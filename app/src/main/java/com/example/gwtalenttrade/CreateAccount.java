package com.example.gwtalenttrade;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class CreateAccount extends AppCompatActivity {

    private Button pickDateBtn;
    private Button completeSignup;
    private TextView selectedDateTV;

    private EditText fullName;
    private EditText GWID;
    private EditText email;
    private EditText password;
    private EditText password2;

    String selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        pickDateBtn = findViewById(R.id.idBtnPickDate);
        completeSignup = findViewById(R.id.completeSignup);

        selectedDateTV = findViewById(R.id.idTVSelectedDate);

        fullName = findViewById(R.id.editTextFullName);
        GWID = findViewById(R.id.editTextGWID);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        password2 = findViewById(R.id.editTextPassword2);

        completeSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve values from EditText fields
                checkDataEntered();
            }
        });


        // on below line we are adding click listener for our pick date button
        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        CreateAccount.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                selectedDateTV.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });
    }

    private void checkDataEntered() {
        if (isEmpty(fullName)) {
            Toast t = Toast.makeText(this, "You must enter full name to register!", Toast.LENGTH_SHORT);
            t.show();
        }
        if (isEmpty(GWID)) {
            Toast t = Toast.makeText(this, "You must enter GWID to register!", Toast.LENGTH_SHORT);
            t.show();
        }

        if (!isEmail(email)) {
            email.setError("Enter valid email!");
        }

        if (isEmpty(password)) {
            Toast t = Toast.makeText(this, "You must enter password to register!", Toast.LENGTH_SHORT);
            t.show();
        }
        if (isEmpty(password2)) {
            Toast t = Toast.makeText(this, "You must re-enter password to register!", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}