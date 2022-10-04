package com.example.controleapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityP extends AppCompatActivity {
    TextView textView ;
    ImageView imageView;
    FirebaseDatabase database  = FirebaseDatabase.getInstance();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        Bundle extra = this.getIntent().getExtras();
        if(extra != null){
            String data = extra.getString("maclé");
            Toast.makeText(ActivityP.this, data, Toast.LENGTH_LONG).show();
        }

        Button buttonH = findViewById(R.id.button3);
        buttonH.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           DatabaseReference myRef1 = database.getReference().getRef().child("histoire");
                                           myRef1.addValueEventListener(new ValueEventListener() {

                                               public void onDataChange(DataSnapshot dataSnapshot) {
                                                   String value = dataSnapshot.getValue().toString();



                                                   // setup the alert builder
                                                   AlertDialog.Builder builder = new AlertDialog.Builder(ActivityP.this);
                                                   builder.setTitle("");
                                                   builder.setMessage(""+value);

                                                   // add a button
                                                   builder.setPositiveButton("OK", null);

                                                   // create and show the alert dialog
                                                   AlertDialog dialog = builder.create();
                                                   dialog.show();
                                               }

                                               @Override
                                               public void onCancelled(DatabaseError error) {

                                               }
                                           });

                                       }
                                   });

        textView = (TextView) findViewById(R.id.textView7);
        DatabaseReference myRef1 = database.getReference().child("Etat_Porte");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                textView.setText("État du port :" + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        final TextView textView2 =(TextView)findViewById(R.id.textView3);
        DatabaseReference myRef2 = database.getReference().child("CapteurTemperatureEtHumidity").child("Temperature");
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue().toString();
                textView2.setText("Température "+value+" °C");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final TextView textView3 =(TextView)findViewById(R.id.textView5);
        DatabaseReference myRef3 = database.getReference().child("CapteurTemperatureEtHumidity").child("Humidity");
        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue().toString();
                textView3.setText("Humidité "+value+" %");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final TextView textView4 =(TextView)findViewById(R.id.textView6);
        DatabaseReference myRef4 = database.getReference().child("CapteurGaz");
        myRef4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue().toString();
                textView4.setText("Niveau de gaz "+value +" %");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.editText3);
                String editTextValue = editText.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                 DatabaseReference myRef = database.getReference().child("mdp");
                 if(!editTextValue.isEmpty()){
                        if(editTextValue.length()>4){

                            Toast.makeText(ActivityP.this, "Code d'accès 4 chiffres", Toast.LENGTH_LONG).show();

                        }else
                            {    myRef.setValue(editTextValue);
                                Toast.makeText(ActivityP.this, "Code d'accès changer", Toast.LENGTH_LONG).show();
                                editText.setText("");
                                editText.setHint("Entrer nouveau code d'accès");
                        }
                 }else{
                     Toast.makeText(ActivityP.this, "Entrer Code d'accès", Toast.LENGTH_LONG).show();
                 }

            }
        });

        DatabaseReference myRefI = database.getInstance().getReference().child("esp32-cam");
        imageView = (ImageView)findViewById(R.id.imageView);
        myRefI.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String base64String = snapshot.getValue(String.class);
                String base64Image = base64String.split(",")[1];
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
              //  imageView.setImageBitmap(decodedByte);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button button2 = findViewById(R.id.button5);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityP.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button button3 = findViewById(R.id.button4);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityP.this,ActivityF.class);
                startActivity(intent);
            }
        });
 }
}
