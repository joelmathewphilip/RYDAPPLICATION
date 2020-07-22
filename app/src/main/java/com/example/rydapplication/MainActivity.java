package com.example.rydapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText username,password,mobile;
    Button mobile_btn;
    String otp_code;
    TextView country_code;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton gmail;
    private static int RC_SIGN_IN=9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mobile=findViewById(R.id.login_mobile);

        gmail=findViewById(R.id.gmail_login_btn);
        mobile_btn=findViewById(R.id.mobile_login_btn);
        country_code=findViewById(R.id.country_code);

        getSupportActionBar().setTitle("Authentication");

        create_request();
        mAuth=FirebaseAuth.getInstance();

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                progressDialog=new ProgressDialog(getApplicationContext());
                progressDialog.setMessage("Loading...Please Wait...");
            }
        });

        mobile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth_using_mobile();
            }
        });

    }

    public void create_request()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }



    private void firebaseAuthWithGoogle(final String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Successful Authentication", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getApplicationContext(),SetAccountDetails.class);
                            intent.putExtra("Type","Mail");
                            intent.putExtra("ID",mAuth.getUid());
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"Authentication Failure",Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {
            Toast.makeText(this, "Successful Authentication", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,block_post.class);
            intent.putExtra("ID",mAuth.getUid());
            startActivity(intent);
            finish();

        }
    }

    public void auth_using_mobile()
    {
        if(mobile.getText().toString().trim().length()==0)
        {
            mobile.setError("Required!!");
            mobile.requestFocus();
            return;
        }
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading..Please Wait");
        progressDialog.setCancelable(false);
        String number=country_code.getText().toString().trim()+mobile.getText().toString().trim();
        Toast.makeText(this,number,Toast.LENGTH_SHORT).show();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            otp_code=s;
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Otp Send", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), VerifyOtp.class);
            intent.putExtra("code", otp_code);
            intent.putExtra("Type","Mobile");
            startActivity(intent);
            finish();
        }
    };


}