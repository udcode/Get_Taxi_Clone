package com.avi_ud.gettaxi1.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avi_ud.gettaxi1.R;
import com.avi_ud.gettaxi1.model.backend.BackEndFactory;
import com.avi_ud.gettaxi1.model.backend.IAction;
import com.avi_ud.gettaxi1.model.entities.Passenger;
import com.google.gson.Gson;

import java.util.regex.Pattern;

public class PersonActivity extends AppCompatActivity {
    Button nextButton;
    TextInputEditText idTxt;
    TextInputEditText emailTxt, phoneTxt, fNameTxt, lNameTxt;
    View.OnFocusChangeListener idListener, mailListener, phoneListener, fNameListener, lNameListener;
    SharedPreferences prefs = null;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_activity);

        idListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isValidId(idTxt.getText().toString());
                }
            }
        };

        mailListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isValidEmail(emailTxt.getText().toString());
                }
            }
        };

        phoneListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isValidMobile(phoneTxt.getText().toString());
                }
            }
        };

        fNameListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (fNameTxt.getText().toString().length() < 3) {
                        fNameTxt.setError("name length must be longer then 2 letters");
                    }
                }
            }
        };

        lNameListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (lNameTxt.getText().toString().length() < 3) {
                        lNameTxt.setError("name length must be longer then 2 letters");
                    }
                }
            }
        };

        idTxt = findViewById(R.id.id);
        idTxt.setOnFocusChangeListener(idListener);
        emailTxt = findViewById(R.id.email);
        emailTxt.setOnFocusChangeListener(mailListener);
        phoneTxt = findViewById(R.id.phoneNumber);
        phoneTxt.setOnFocusChangeListener(phoneListener);
        fNameTxt = findViewById(R.id.firstName);
        fNameTxt.setOnFocusChangeListener(fNameListener);
        lNameTxt = findViewById(R.id.lastName);
        lNameTxt.setOnFocusChangeListener(lNameListener);
        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocusFromTouch();
                if (validate()) {
                    openOrderActivity();
                }
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = prefs.getString(Passenger.class.getName(), "");
        if (!json.equals("")) {
            Passenger person = gson.fromJson(json, Passenger.class);
            if (person != null) {
                idTxt.setText(person.getId());
                fNameTxt.setText(person.getFirstName());
                lNameTxt.setText(person.getFirstName());
                phoneTxt.setText(person.getPhoneNumber());
                emailTxt.setText(person.getEmail());
            }
        }
    }

    private void openOrderActivity() {
        final Intent orderIntent = new Intent(PersonActivity.this, MainActivity.class);
        final Passenger p = getPassengerData();
        SharedPreferences.Editor prefsEditor = prefs.edit();
        String json = gson.toJson(p);
        prefsEditor.putString(Passenger.class.getName(), json);
        prefsEditor.apply();

        BackEndFactory.backEnd.addPassenger(p, new IAction<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                orderIntent.putExtra(Passenger.class.getName(), getPassengerData());
                startActivity(orderIntent);
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Passenger getPassengerData() {

        return new Passenger(lNameTxt.getText().toString(),
                fNameTxt.getText().toString(),
                idTxt.getText().toString(),
                phoneTxt.getText().toString(),
                emailTxt.getText().toString());
    }

    private boolean validate() {
        boolean valid = true;
        if (!isValidId(idTxt.getText().toString()))
            valid = false;
        if (!isValidEmail(emailTxt.getText().toString()))
            valid = false;
        if (!isValidMobile(phoneTxt.getText().toString()))
            valid = false;
        if (fNameTxt.getText().toString().length() < 3)
            valid = false;
        if (lNameTxt.getText().toString().length() < 3)
            valid = false;

        return (valid && idTxt.getError() == null &&
                emailTxt.getError() == null &&
                phoneTxt.getError() == null &&
                fNameTxt.getError() == null &&
                lNameTxt.getError() == null);
    }

    public boolean isValidEmail(String target) {
        if ((!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())) {
            return true;
        }
        emailTxt.setError("wrong mail address");
        return false;
    }

    private boolean isValidId(String id) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", id)) {
            if (id.length() < 6 || id.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
                idTxt.setError("ID must be between 6 to 13 digits");
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    private boolean isValidMobile(String phone) {
        if (android.util.Patterns.PHONE.matcher(phone).matches()) {
            return true;
        }
        phoneTxt.setError("wrong phone format");
        return false;
    }

}
