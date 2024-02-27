package com.example.mecon;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import android.annotation.SuppressLint;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button loginBtn;
    TextView gotoRegister;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        gotoRegister = findViewById(R.id.signupRedirectText);

       loginBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               checkField(email);
               checkField(password);
               Log.d("TAG","onClick:" + email.getText().toString());

               if (valid) {
                   fAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                           Toast.makeText(LoginActivity.this, "Loggedin Successfully", Toast.LENGTH_SHORT).show();
                           FirebaseUser a = FirebaseAuth.getInstance().getCurrentUser();
                           //Toast.makeText(LoginActivity.this, " " + a.getUid(), Toast.LENGTH_SHORT).show();
                           checkUserAccessLevel(a.getUid());
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                           //checkUserAccessLevel(authResult.getUser().getUid());
                       }
                   });

               }
           }
       });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(),SignupActivity.class));
            }
        });
    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("users").document(uid);
        // Extract the data from the document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "DocumentSnapshot: " + documentSnapshot);

                if (documentSnapshot.exists()) {
                    Log.d("TAG", "isStudent: " + documentSnapshot.getString("isStudent"));
                    Log.d("TAG", "isTeacher: " + documentSnapshot.getString("isTeacher"));

                    if (documentSnapshot.getString("isStudent") != null) {
                       // Toast.makeText(LoginActivity.this, "working3", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }

                    if (documentSnapshot.getString("isTeacher") != null) {
                       // Toast.makeText(LoginActivity.this, "working2", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Admin.class));
                        finish();
                    }
                } else {
                    Log.d("TAG", "Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "Error getting document", e);
            }
        });
    }



    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }

     protected void onStart(){
        super.onStart();
         if(FirebaseAuth.getInstance().getCurrentUser()!=null){
           DocumentReference df = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                   df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.getString("isTeacher")!=null){
                                startActivity(new Intent(getApplicationContext(),Admin.class));
                                finish();
                            }
                           if(documentSnapshot.getString("isStudent")!=null){
                               startActivity(new Intent(getApplicationContext(),MainActivity.class));
                               finish();
                           }
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                          FirebaseAuth.getInstance().signOut();
                          startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                          finish();
                       }
                   });
         }
    }
}

