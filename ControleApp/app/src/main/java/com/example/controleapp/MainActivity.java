package com.example.controleapp;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    EditText editText1;

    String value ="";
    String value1 ="";
    FirebaseDatabase database  = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         editText = (EditText)findViewById(R.id.editText) ;
         editText1 = (EditText)findViewById(R.id.editText2) ;

        Button button1= findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference myRef1 = database.getReference().child("utilisateur").child("nom_utilisateur");
                myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         value = snapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                DatabaseReference myRef2 = database.getReference().child("utilisateur").child("mdp_utilisateur");
                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        value1 = snapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                String editTextValue = editText.getText().toString();
                String editTextValue1 = editText1.getText().toString();
                if(editTextValue.isEmpty() || editTextValue1.isEmpty()){
                    Toast.makeText(MainActivity.this, "Entrer Nom utilisateur ou Mot de passe ", Toast.LENGTH_LONG).show();
                }else if((editTextValue.equals(value))&&(editTextValue1.equals(value1))){
                Intent intent = new Intent(MainActivity.this,ActivityP.class);
                startActivity(intent);
            }else{
                    Toast.makeText(MainActivity.this, "Nom utilisateur ou Mot de passe incorrect", Toast.LENGTH_LONG).show();
                    editText1.setText("");
                }
            }
        });
    }
}
