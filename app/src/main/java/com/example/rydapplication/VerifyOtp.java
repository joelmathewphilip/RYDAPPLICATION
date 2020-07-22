package com.example.rydapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyOtp extends AppCompatActivity {
    String otp,mobile_number;
    EditText otp_entered;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        Button btn=findViewById(R.id.verify_otp_btn);
        otp=getIntent().getStringExtra("code");
        getSupportActionBar().setTitle("Verify OTP");
        mobile_number=getIntent().getStringExtra("Mobile Number");
        otp_entered=findViewById(R.id.edit_text_otp);
        progressBar=findViewById(R.id.verify_otp_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otp_entered.getText().toString().trim().isEmpty())
                {
                    otp_entered.setError("Required");
                    otp_entered.requestFocus();
                    return;
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    verify_otp(otp,otp_entered.getText().toString().trim());
                }
            }
        });
    }

    public void verify_otp(String code_send,String code_entered)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code_send, code_entered);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        try {

            mAuth=FirebaseAuth.getInstance();
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Verification Successful", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent=new Intent(getApplicationContext(),SetAccountDetails.class);
                                intent.putExtra("Mobile Number",mobile_number);
                                intent.putExtra("Type","Mobile");
                                intent.putExtra("ID",FirebaseAuth.getInstance().getUid());
                                startActivity(intent);
                                finish();


                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),"Wrong OTP",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);


                            }

                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }



}