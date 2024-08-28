package com.example.save4fun;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.save4fun.db.DBUsersHelper;
import com.example.save4fun.model.User;
import com.example.save4fun.util.Constant;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 100;

    EditText editTextMainUsername, editTextMainPassword;
    Button buttonLogin;
    String DBNAME = Constant.DBNAME;
    TextView textViewGoToSignUp;
    ImageView imageViewGoogleLogin;
    DBUsersHelper dbUsersHelper;

    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private final static String TAG = "MainActivity";
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To remove db
        // clearDBAndSharedPreferences();
        processCopyDB();

        editTextMainUsername = findViewById(R.id.editTextMainUsername);
        editTextMainPassword = findViewById(R.id.editTextMainPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        imageViewGoogleLogin = findViewById(R.id.imageViewGoogleLogin);

        textViewGoToSignUp = findViewById(R.id.textViewGoToSignUp);

        dbUsersHelper = new DBUsersHelper(MainActivity.this);

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFERENCES_NAME, 0);
        boolean hasLoggedIn = sharedPreferences.getBoolean(Constant.HAS_LOGGED_IN, false);

        if (hasLoggedIn) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        // Google sign in
        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextMainUsername.getText().toString();
                String password = editTextMainPassword.getText().toString();
                User user = new User(username, password);

                if (username.equals("") || password.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter all the fields", Toast.LENGTH_LONG).show();
                }
                boolean res = dbUsersHelper.authenticate(username, password);
                if (res) {
                    Toast.makeText(MainActivity.this, "Signed in successfully", Toast.LENGTH_LONG).show();

                    SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFERENCES_NAME, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constant.HAS_LOGGED_IN, true);
                    editor.putString(Constant.USERNAME, username);
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            Log.d(TAG, "Got ID token.");
                            if (idToken != null) {
                                // Got an ID token from Google. Use it to authenticate
                                // with your backend.
                                String email = credential.getId();
                                String username = email.split("@")[0];

                                boolean userExisted = dbUsersHelper.isUserExisted(username);
                                if (!userExisted) {
                                    User user = new User(username, "");
                                    user.setEmail(email);
                                    boolean res = dbUsersHelper.insertUser(user);
                                    if (res) {
                                        userExisted = true;
                                    } else {
                                        Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                                    }
                                }

                                if (userExisted) {
                                    Toast.makeText(MainActivity.this, "Signed in successfully", Toast.LENGTH_LONG).show();

                                    SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFERENCES_NAME, 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(Constant.HAS_LOGGED_IN, true);
                                    editor.putString(Constant.USERNAME, username);
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Please sign in into a Google account first", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                });

        imageViewGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneTapClient.beginSignIn(signUpRequest)
                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
                                IntentSenderRequest intentSenderRequest =
                                        new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                                activityResultLauncher.launch(intentSenderRequest);
                            }
                        })
                        .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // No Google Accounts found. Just continue presenting the signed-out UI.
                                Toast.makeText(MainActivity.this, "No Google accounts found", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onFailure" + e.getLocalizedMessage());
                            }
                        });
            }
        });

        textViewGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void clearDBAndSharedPreferences() {
        MainActivity.this.deleteDatabase(DBNAME);

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFERENCES_NAME, 0);
        sharedPreferences.edit().remove(Constant.HAS_LOGGED_IN).apply();
        sharedPreferences.edit().remove(Constant.USERNAME).apply();
    }

    private void processCopyDB() {
        File dbFile = getDatabasePath(DBNAME);
        if (!dbFile.exists()) {
            try {
                CopyDatabaseFromAsset();
                // Toast.makeText(this, "Successfully copied from assets folder", Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getDatabasePath() {
        return getApplicationInfo().dataDir + Constant.DB_PATH_SUFFIX + DBNAME;
    }

    private void CopyDatabaseFromAsset() {
        try {
            InputStream input;
            input = getAssets().open(DBNAME);
            String outFileName = getDatabasePath();

            File f = new File(getApplicationInfo().dataDir + Constant.DB_PATH_SUFFIX);
            if (!f.exists()) {
                f.mkdir();
            }

            OutputStream output = new FileOutputStream(outFileName);
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            output.write(buffer);

            output.flush();
            output.close();
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}