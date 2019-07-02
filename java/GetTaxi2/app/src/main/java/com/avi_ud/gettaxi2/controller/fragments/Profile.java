package com.avi_ud.gettaxi2.controller.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.controller.activities.Main;
import com.avi_ud.gettaxi2.model.backend.BackEndFactory;
import com.avi_ud.gettaxi2.model.backend.IAction;
import com.avi_ud.gettaxi2.model.entities.Driver;
import com.avi_ud.gettaxi2.utils.Alerter;
import com.avi_ud.gettaxi2.utils.Globals;
import com.avi_ud.gettaxi2.utils.LoadingProgressBar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class Profile extends Fragment {
    private static final int REQUEST_PICTURE = 1;

    private enum State {REGISTER, PROFILE}

    private State mState = State.REGISTER;
    Button saveButton;
    TextView lastNameView;
    TextView firstNameView;
    TextView idView;
    TextView phoneNumberView;
    TextView carTypeView;
    LoadingProgressBar loadingProgressBar;
    String uid;
    String email;
    ImageView image;
    private UpdateDriverListener updateDriverListener;
    View.OnFocusChangeListener idListener, phoneListener, fNameListener, lNameListener, carTypeListener;

    public interface UpdateDriverListener {
        void onUpdateDriver();
    }

    public interface ValidateListener {
        void onValidate(boolean valid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        idListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isValidId(idView.getText().toString());
                }
            }
        };

        phoneListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isValidMobile(phoneNumberView.getText().toString());
                }
            }
        };

        fNameListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (firstNameView.getText().toString().length() < 3) {
                        firstNameView.setError("name length must be longer then 2 letters");
                    }
                }
            }
        };

        lNameListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (lastNameView.getText().toString().length() < 3) {
                        lastNameView.setError("name length must be longer then 2 letters");
                    }
                }
            }
        };
        carTypeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (carTypeView.getText().toString().length() < 3) {
                        carTypeView.setError("Car type must be longer then 2 letters");
                    }
                }
            }
        };

        image = (ImageView) view.findViewById(R.id.image);
        saveButton = (Button) view.findViewById(R.id.save_button);
        lastNameView = (TextView) view.findViewById(R.id.lastName);
        firstNameView = (TextView) view.findViewById(R.id.firstName);
        idView = (TextView) view.findViewById(R.id.id);
        phoneNumberView = (TextView) view.findViewById(R.id.phoneNumber);
        carTypeView = (TextView) view.findViewById(R.id.car_type);
        idView.setOnFocusChangeListener(idListener);
        phoneNumberView.setOnFocusChangeListener(phoneListener);
        lastNameView.setOnFocusChangeListener(lNameListener);
        firstNameView.setOnFocusChangeListener(fNameListener);
        carTypeView.setOnFocusChangeListener(carTypeListener);

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.main_layout);
        loadingProgressBar = new LoadingProgressBar(getActivity(), layout);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        String state = getArguments().getString(Globals.Strings.state);
        if (Objects.equals(state, Globals.Strings.profile)) {
            mState = State.PROFILE;
        }

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            uid = extras.getString(Globals.Strings.uid);
            email = extras.getString(Globals.Strings.email);
        }

        setContentByState(view);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocusFromTouch();
                if (validate()) {
                    if (mState == State.REGISTER)
                        register();
                    else
                        save();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICTURE);
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getActivity() is fully created in onActivityCreated and instanceOf differentiate it between different Activities
        if (getActivity() instanceof UpdateDriverListener)
            updateDriverListener = (UpdateDriverListener) getActivity();
    }

    //set the content by the state of the container
    private void setContentByState(View view) {
        TextView title = (TextView) view.findViewById(R.id.title);

        if (mState == State.REGISTER) {
            title.setText("Register");
            saveButton.setText("Register");
        } else {
            title.setText("Profile");
            saveButton.setText("Save");
            if (Globals.driver != null) {
                firstNameView.setText(Globals.driver.getFirstName());
                lastNameView.setText(Globals.driver.getLastName());
                idView.setText(Globals.driver.getId());
                phoneNumberView.setText(Globals.driver.getPhoneNumber());
                carTypeView.setText(Globals.driver.getCarType());
                if (Globals.driver.getImageLocalUri() != null)
                    image.setImageURI(Globals.driver.getImageLocalUri());
            }
        }
    }

    private void save() {
        final Driver d = getDriverData();
        d.setUid(Globals.driver.getUid());
        d.setEmail(Globals.driver.getEmail());
        BackEndFactory.backEnd.addDriver(d, new IAction<Void>() {
            @Override
            public void onSuccess(Void obj) {
                Globals.driver = d;
                updateDriverListener.onUpdateDriver();
                Toast.makeText(getContext(), "Saved changes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Alerter.showErrorDialog(getContext(), exception.getMessage());
            }

            @Override
            public void onPreExecute() {
                loadingProgressBar.show();
            }

            @Override
            public void onPostExecute() {
                loadingProgressBar.hide();
            }
        });


    }

    private void register() {
        final Driver d = getDriverData();
        d.setUid(uid);
        BackEndFactory.backEnd.addDriver(d, new IAction<Void>() {
            @Override
            public void onSuccess(Void obj) {
                Globals.driver = d;
                Intent mainIntent = new Intent(getActivity(), Main.class);
                startActivity(mainIntent);
                getActivity().finish();
            }

            @Override
            public void onFailure(Exception exception) {
                Alerter.showErrorDialog(getContext(), exception.getMessage());

            }

            @Override
            public void onPreExecute() {
                loadingProgressBar.show();
            }

            @Override
            public void onPostExecute() {
                loadingProgressBar.hide();
            }
        });
    }

    private Driver getDriverData() {
        return new Driver(
                lastNameView.getText().toString(),
                firstNameView.getText().toString(),
                idView.getText().toString(),
                phoneNumberView.getText().toString(),
                email,
                carTypeView.getText().toString(), (Uri) image.getTag());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            validateImage(data.getData(), new ValidateListener() {
                @Override
                public void onValidate(boolean valid) {
                    if (valid) {
                        image.setImageURI(data.getData());
                        image.setTag(data.getData());
                    } else {
                        Alerter.showErrorDialog(getContext(), "It is not a car image!");
                    }
                }
            });

        }
    }

    private boolean validate() {
        return (idView.getError() == null &&
                carTypeView.getError() == null &&
                phoneNumberView.getError() == null &&
                firstNameView.getError() == null &&
                lastNameView.getError() == null);
    }

    //validate that there is a car in the image (using ml)
    private void validateImage(Uri image, final ValidateListener validateListener) {
        if (image != null) {
            FirebaseVisionImage fImage;
            try {
                fImage = FirebaseVisionImage.fromFilePath(getContext(), image);
                FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                        .getOnDeviceImageLabeler(new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                                .setConfidenceThreshold(0.7f)
                                .build());

                labeler.processImage(fImage)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                boolean res = false;
                                for (FirebaseVisionImageLabel label : labels) {
                                    if (label.getText().equals("Car")) {
                                        res = true;
                                        break;
                                    }
                                }
                                validateListener.onValidate(res);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean isValidId(String id) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", id)) {
            if (id.length() < 6 || id.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
                idView.setError("ID must be between 6 to 13 digits");
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
        phoneNumberView.setError("wrong phone format");
        return false;
    }
}
