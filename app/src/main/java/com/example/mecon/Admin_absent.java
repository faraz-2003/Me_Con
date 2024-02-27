package com.example.mecon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Admin_absent extends AppCompatActivity {

    private TextView countTextView;
    private Button refreshButton;
    private int absentCount = 0 ;
    private CollectionReference votesCollection;
    private FirebaseFirestore db;
    private ListenerRegistration snapshotListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_absent);

        refreshButton = findViewById(R.id.refresh);
        countTextView = findViewById(R.id.absenteeCountTextView);

        db = FirebaseFirestore.getInstance();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetVotes();
            }
        });
    }

    protected void onStart(){
        super.onStart();
        startListening();
    }

    protected void onStop(){
        super.onStop();
        stopListening();
    }

    private void startListening(){
        snapshotListener = db.collection("users").whereEqualTo("hasVoted",true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    return;
                }
                if(querySnapshot != null){
                    absentCount = querySnapshot.size();
                    countTextView.setText(String.valueOf(absentCount));
                }
            }
        });
    }

    private void stopListening(){
       if(snapshotListener != null){
           snapshotListener.remove();
           snapshotListener = null;
       }
    }
     private void resetVotes(){
         db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if(task.isSuccessful()){
                     for(QueryDocumentSnapshot document : task.getResult()){
                         document.getReference().update("hasVoted",false);
                     }
                     Toast.makeText(Admin_absent.this,"Votes reset Successfully",Toast.LENGTH_SHORT).show();
                 }
                 else{
                     Toast.makeText(Admin_absent.this,"Failed to reset votes",Toast.LENGTH_SHORT).show();
                 }
             }
         });
     }

}