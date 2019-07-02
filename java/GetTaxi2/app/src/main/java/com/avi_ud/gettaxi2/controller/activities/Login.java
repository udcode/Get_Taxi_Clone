package com.avi_ud.gettaxi2.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.model.backend.BackEndFactory;
import com.avi_ud.gettaxi2.model.backend.IAction;
import com.avi_ud.gettaxi2.model.entities.Driver;
import com.avi_ud.gettaxi2.utils.Alerter;
import com.avi_ud.gettaxi2.utils.Globals;
import com.avi_ud.gettaxi2.utils.LoadingProgressBar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

//Login activity
public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    Button loginButton;
    Button registerButton;
    Button forgetButton;
    TextView emailView;
    TextView passwordView;
    LoadingProgressBar loadingProgressBar;
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_layout);
        loadingProgressBar = new LoadingProgressBar(Login.this, layout);

        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);
        forgetButton = (Button) findViewById(R.id.forget_password);
        emailView = (TextView) findViewById(R.id.email);
        passwordView = (TextView) findViewById(R.id.password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });
        signInButton = findViewById(R.id.sign_in_with_google_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //sign in with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauth_key))
                .requestEmail()
                .build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    //login with existing user
    private void login() {
        if (validate()) {
            BackEndFactory.backEnd.signInUser(emailView.getText().toString(),
                    passwordView.getText().toString(), new IAction<FirebaseUser>() {
                        @Override
                        public void onSuccess(final FirebaseUser usr) {
                            loadMainActivity(usr);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Alerter.showErrorDialog(Login.this, exception.getMessage());
                            loadingProgressBar.hide();
                        }

                        @Override
                        public void onPreExecute() {
                            loadingProgressBar.show();
                        }

                        @Override
                        public void onPostExecute() {
                        }
                    });
        }
    }

    private void loadMainActivity(final FirebaseUser usr) {
        if(usr == null) return;

        final String uid = usr.getUid();
        final String email = usr.getEmail();
        //pull the driver data and load the main activity
        BackEndFactory.backEnd.getDriver(uid, new IAction<Driver>() {
            @Override
            public void onSuccess(Driver driver) {
                if (driver == null) { //some misconfiguration
                    Intent profileIntent = new Intent(Login.this, Register.class);
                    profileIntent.putExtra(Globals.Strings.uid, uid);
                    profileIntent.putExtra(Globals.Strings.email, email);
                    startActivity(profileIntent);
                    finish();
                } else {
                    Intent mainIntent = new Intent(getApplicationContext(), Main.class);
                    driver.setUid(uid);
                    Globals.driver = driver;
                    startActivity(mainIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Alerter.showErrorDialog(Login.this, exception.getMessage());
            }

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute() {
                loadingProgressBar.hide();
            }
        });
    }

    //register new user
    private void register() {
        if (validate()) {
            BackEndFactory.backEnd.createUser(emailView.getText().toString(),
                    passwordView.getText().toString(), new IAction<FirebaseUser>() {
                        @Override
                        public void onSuccess(final FirebaseUser usr) {
                            Intent profileIntent = new Intent(Login.this, Register.class);
                            profileIntent.putExtra(Globals.Strings.uid, usr.getUid());
                            profileIntent.putExtra(Globals.Strings.email, emailView.getText().toString());
                            startActivity(profileIntent);
                            finish();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Alerter.showErrorDialog(Login.this, exception.getMessage());
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
    }

    //password recover
    private void forgetPassword() {
        if (!TextUtils.isEmpty(emailView.getText())) {
            String email = emailView.getText().toString();
            BackEndFactory.backEnd.resetPassword(email, new IAction<Void>() {
                @Override
                public void onSuccess(Void obj) {
                    Toast.makeText(getApplicationContext(), "Reset email was send.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    Alerter.showErrorDialog(Login.this, exception.getMessage());
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
    }

    //validate the input fields
    private boolean validate() {
        emailView.setError(null);
        passwordView.setError(null);
        boolean noerror = true;

        if (TextUtils.isEmpty(emailView.getText())) {
            emailView.setError(getString(R.string.empty_filed_error));
            noerror = false;
        }
        if (TextUtils.isEmpty(passwordView.getText())) {
            passwordView.setError(getString(R.string.empty_filed_error));
            noerror = false;
        }
        return noerror;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            BackEndFactory.backEnd.SignInWithGoogle(account, new IAction<FirebaseUser>() {
                @Override
                public void onSuccess(FirebaseUser usr) {
                    loadMainActivity(usr);
                }

                @Override
                public void onFailure(Exception exception) {
                    Alerter.showErrorDialog(getApplicationContext(), exception.getMessage());
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

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Alerter.showErrorDialog(getApplicationContext(),"Failed to login. Error code:" + e.getStatusCode() );
            loadMainActivity(null);
        }
    }
}
