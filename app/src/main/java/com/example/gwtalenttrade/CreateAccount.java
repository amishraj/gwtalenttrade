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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    // Check current user status on app start
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Redirect to MainActivity if user is already logged in
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

        // Initializing UI elements
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

        // Listener for login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //navigate to the first page if user wants to login
                Intent intent = new Intent(CreateAccount.this, firstPage.class);
                startActivity(intent);
            }
        });

        // Listener for sign up button
        completeSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if all constraints have been checked and the data is properly formatted
                if(checkDataEntered()){
                    //make the spinner visible
                    progressBar.setVisibility(View.VISIBLE);

                    //collect user input
                    String fullName, GWID, email, password, dob, phone;
                    fullName= String.valueOf(editTextFullName.getText());
                    GWID= String.valueOf(editTextGWID.getText());
                    email= String.valueOf(editTextEmail.getText());
                    password= String.valueOf(editTextPassword.getText());
                    phone = String.valueOf(editTextPhoneNumber.getText());
                    dob= selectedDate;

                    // Create user account with Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        //success case
                                        Toast.makeText(CreateAccount.this, "Account Created!",
                                                Toast.LENGTH_SHORT).show();

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                                        User newUser = new User(fullName, GWID, email, dob, phone); // Replace with your User model
                                        userRef.setValue(newUser);

                                        //user created, now let the user navigate back to the first page in order to login again
                                        FirebaseAuth.getInstance().signOut();

                                        startActivity(new Intent(CreateAccount.this, firstPage.class));
                                        finish();
                                    } else {
                                        //error handling
                                        Toast.makeText(CreateAccount.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

        //method to handle date picker
        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                //fetch year month and day values
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                //show the date picker dialog and set the recorded values correctly on the textview that shows the selected date
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateAccount.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                selectedDateTV.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    // Validate user input
    private boolean checkDataEntered() {
        // Check if input fields are valid
        // Various validations: empty fields, valid GWID, valid email, valid phone number, and date
        if (isEmpty(editTextFullName)) {
            Toast t = Toast.makeText(this, "You must enter full name to register!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        if (isEmpty(editTextGWID) || !isValidGWID(editTextGWID)) {
            Toast t = Toast.makeText(this, "You must enter GWID to register!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }

        if (!isEmail(editTextEmail)) {
            editTextEmail.setError("Enter valid GWU email!");
            Toast t = Toast.makeText(this, "You must enter a valid GW email!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }

        if (isEmpty(editTextPhoneNumber) || !isValidPhoneNumber(editTextPhoneNumber)) {
            Toast t = Toast.makeText(this, "You must enter a phone number to register!", Toast.LENGTH_SHORT);
            t.show();
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

        if (!isValidDate(selectedDate)) {
            Toast.makeText(this, "Enter a valid date of birth!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!editTextPassword.getText().toString().equals(editTextPassword2.getText().toString())){
            Toast t = Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        return true;
    }

    // Check if email is valid
    private boolean isEmail(EditText editText) {
        String email = editText.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@gwu.edu";

        if (!email.matches(emailPattern)) {
            editText.setError("Enter a valid GWU email address");
            return false;
        }
        return true;
    }

    // Check if field is empty
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    // Validate GWID format
    private boolean isValidGWID(EditText editText) {
        String gwid = editText.getText().toString().trim();
        String gwidPattern = "[gG]\\d{8}";

        if (!gwid.matches(gwidPattern)) {
            editText.setError("Enter a valid GWID (G followed by 8 digits)");
            return false;
        }
        return true;
    }

    // Validate selected date
    private boolean isValidDate(String selectedDate) {
        if (TextUtils.isEmpty(selectedDate)) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        try {
            Date dateOfBirth = dateFormat.parse(selectedDate);

            Calendar currentDate = Calendar.getInstance();
            Date today = currentDate.getTime();

            return dateOfBirth != null && dateOfBirth.before(today);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Validate phone number format
    private boolean isValidPhoneNumber(EditText editText) {
        String phoneNumber = editText.getText().toString().trim();
        String phonePattern = "\\d{10}";

        if (!phoneNumber.matches(phonePattern)) {
            editText.setError("Enter a valid 10-digit phone number");
            return false;
        }
        return true;
    }
}