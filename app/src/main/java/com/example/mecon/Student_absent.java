package com.example.mecon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class Student_absent extends AppCompatActivity {

    private CheckBox confirmCheckbox;
    private Button submitButton;
    private FirebaseFirestore db;

    // CollectionReference votesCollection = db.collection("votes");
    //DocumentReference newVoteRef = votesCollection.document();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_absent);

        confirmCheckbox = findViewById(R.id.agree);
        submitButton = findViewById(R.id.submit);

        db = FirebaseFirestore.getInstance();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitVote();
            }
        });
    }
    private void submitVote() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();

            if (confirmCheckbox.isChecked()) {
                DocumentReference userRef = db.collection("users").document(userUid);

                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Check if the hasVoted field exists in the document
                                if (document.contains("hasVoted")) {
                                    boolean hasVoted = document.getBoolean("hasVoted");
                                    if(hasVoted){
                                        Toast.makeText(Student_absent.this,"You have already Voted",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    // Update the hasVoted field
                                    userRef.update("hasVoted", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(Student_absent.this, "Vote submitted successfully", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Student_absent.this, "Failed to submit vote", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // Add the hasVoted field and set it to true
                                    userRef.update("hasVoted", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(Student_absent.this, "Vote submitted successfully", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Student_absent.this, "Failed to submit vote", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(Student_absent.this, "User document not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Student_absent.this, "Failed to retrieve user document", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please confirm your absence", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

