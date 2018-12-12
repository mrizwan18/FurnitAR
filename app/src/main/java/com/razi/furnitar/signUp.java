package com.razi.furnitar;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class signUp extends AppCompatActivity {
    FirebaseAuth gAuth;
    Button signUp, signin;
    EditText user1, pass1;
    internetConnectivity it;

    protected void onDestroy() {
        unregisterReceiver(it);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        gAuth = FirebaseAuth.getInstance();
        user1 = findViewById(R.id.username_2);
        pass1 = findViewById(R.id.password_2);
        signin = findViewById(R.id.signin_2);
        signUp = findViewById(R.id.signup);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signUp.this, Login.class));
            }
        });
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        it = new internetConnectivity();
        registerReceiver(it, in);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regExpn =
                        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

                CharSequence inputStr = user1.getText().toString();

                Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(inputStr);

                if (!matcher.matches()) {
                    user1.setError("Invalid Email");
                    return;
                }


                String pass = pass1.getText().toString();
                if (TextUtils.isEmpty(pass) || pass.length() < 6) {
                    pass1.setError("You must have at least 6 characters in your password");
                    return;
                }

                createAccount(user1.getText().toString(), pass1.getText().toString());

            }
        });
    }

    public void createAccount(String email, String password) {
        gAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Succ", "createUserWithEmail:success");
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(signUp.this, Login.class));
                            //FirebaseUser user = gAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("warning", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }

}
