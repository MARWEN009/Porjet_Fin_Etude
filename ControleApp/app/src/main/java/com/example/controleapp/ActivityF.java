package com.example.controleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityF  extends AppCompatActivity {
    DatabaseReference myRef;
    TextView textView,textView1,textView2;
    String mdpC;
    protected void onCreate(Bundle savedInstanceState) {

        Button button;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout2);
        textView=(TextView)findViewById(R.id.editText4);
        textView1=(TextView)findViewById(R.id.editText5);
        textView2=(TextView)findViewById(R.id.editText6);
        button =(Button)findViewById(R.id.button6);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             final String mdpA=textView.getText().toString();
             String mdpN=textView1.getText().toString();
              mdpC=textView2.getText().toString();
                if(mdpA.isEmpty()&& mdpN.isEmpty()&& mdpC.isEmpty()){
                    Toast.makeText(ActivityF.this, "Remplir tous les champs", Toast.LENGTH_LONG).show();
                }else
                    if(mdpC.equals(mdpN)){
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        myRef = database.getReference().child("utilisateur").child("mdp_utilisateur");
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String value = snapshot.getValue().toString();
                                if(value.equals(mdpA)){
                                    myRef.setValue(mdpC);
                                    Intent intent = new Intent(ActivityF.this,ActivityP.class);
                                    intent.putExtra("maclé", "Mot de passe changer");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ActivityF.this, "Mot de passe Actuel incorrect", Toast.LENGTH_LONG).show();
                                }

                            }
                             @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                } else {
                        Toast.makeText(ActivityF.this, "Nouveau mot de passe incorrect", Toast.LENGTH_LONG).show();
                    }

        }

    });
}
}