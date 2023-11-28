package com.example.gwtalenttrade;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class CreateAccount extends AppCompatActivity {

    private Button pickDateBtn;
    private Button completeSignup;
    private TextView selectedDateTV;
    private TextView loginBtn;

    private EditText editTextFullName;
    private EditText editTextGWID;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPassword2;

    private EditText editTextPhoneNumber;

    String selectedDate;

    FirebaseAuth mAuth;

    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        System.out.println(currentUser);
        if(currentUser != null){
            startActivity(new Intent(CreateAccount.this, MainActivity.class));
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth= FirebaseAuth.getInstance();

        pickDateBtn = findViewById(R.id.idBtnPickDate);
        completeSignup = findViewById(R.id.completeSignup);
        loginBtn = findViewById(R.id.goToLogin);
        selectedDateTV = findViewById(R.id.idTVSelectedDate);
        progressBar= findViewById(R.id.progressBar);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextGWID = findViewById(R.id.editTextGWID);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword2 = findViewById(R.id.editTextPassword2);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccount.this, firstPage.class);

                // Start the new activity
                startActivity(intent);
            }
        });

        completeSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                // Retrieve values from EditText fields
                if(checkDataEntered()){
                    String fullName, GWID, email, password, dob, phone;
                    fullName= String.valueOf(editTextFullName.getText());
                    GWID= String.valueOf(editTextGWID.getText());
                    email= String.valueOf(editTextEmail.getText());
                    password= String.valueOf(editTextPassword.getText());
                    phone = String.valueOf(editTextPhoneNumber.getText());
                    dob= selectedDate;

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CreateAccount.this, "Account Created!",
                                                Toast.LENGTH_SHORT).show();

                                        // Get the authenticated user
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        // Save additional details to the database
                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                                        User newUser = new User(fullName, GWID, email, dob, phone); // Replace with your User model
                                        userRef.setValue(newUser);

                                        FirebaseAuth.getInstance().signOut();

                                        startActivity(new Intent(CreateAccount.this, firstPage.class));
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(CreateAccount.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

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

    private boolean checkDataEntered() {
        if (isEmpty(editTextFullName)) {
            Toast t = Toast.makeText(this, "You must enter full name to register!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        if (isEmpty(editTextGWID)) {
            Toast t = Toast.makeText(this, "You must enter GWID to register!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }

        if (!isEmail(editTextEmail)) {
            editTextEmail.setError("Enter valid email!");
            return false;
        }

        if (isEmpty(editTextPassword)) {
            Toast t = Toast.makeText(this, "You must enter password to register!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        if (isEmpty(editTextPassword2)) {
            Toast t = Toast.makeText(this, "You must re-enter password to register!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }

        if(!editTextPassword.getText().toString().equals(editTextPassword2.getText().toString())){
            Toast t = Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        return true;
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