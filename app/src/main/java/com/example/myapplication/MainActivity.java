package com.example.myapplication;

import static android.app.PendingIntent.getActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_CONTACT_REQUEST = 1;
    private static final int Perm_CTC = 1;
    String nomContact="";
    String numTel="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button buttonContactID = findViewById(R.id.buttonContactID);
        Button buttonDetailsContact = findViewById(R.id.buttonDetailsContact);
        Button buttonCall=findViewById(R.id.buttonCall);
        buttonCall.setEnabled(false);
        buttonDetailsContact.setEnabled(false);

        buttonDetailsContact.setOnClickListener(v->{
            TextView textView = findViewById(R.id.textView);
            textView.setText(nomContact);
                });
        buttonContactID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Demandez la permission de lecture de contacts ici
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        Perm_CTC);
            }
        });
       // Button buttonCall = findViewById(R.id.buttonCall);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Placez un appel en utilisant le numéro de téléphone du contact
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + numTel));
                startActivity(intent);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Récupérez l'URI du contact sélectionné et affichez-la dans le TextView
                TextView textView = findViewById(R.id.textView);
                String contactUri1 = data.getDataString();
                //textView.setText(contactUri1);

                // Activez les boutons "Détails contact" et "Call"
                Button buttonDetailsContact = findViewById(R.id.buttonDetailsContact);
                Button buttonCall = findViewById(R.id.buttonCall);
                buttonDetailsContact.setEnabled(true);
                buttonCall.setEnabled(true);

                Uri contactUri = data.getData();
            Log.i("contactUri", String.valueOf(contactUri));

                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    // Récupérer le nom du contact.
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    // Récupérer l'ID du contact.
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    // Fermer le curseur.
                    cursor.close();

// Afficher les détails du contact.
                    Toast.makeText(this, "Contact: " + contactName + " (ID: " + contactId + ")", Toast.LENGTH_SHORT).show();


                    String[] projection = new String[] {
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.TYPE
                    };

// Requête pour récupérer les numéros de téléphone du contact.
                    Cursor cursor2 = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            projection,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[] { contactId },
                            null);

                    if (cursor2 != null && cursor2.moveToFirst()) {
                        // Récupérer le numéro de téléphone.
                        String phoneNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        nomContact="nom du contact : "+contactName+"\n tel :"+phoneNumber;
                        numTel=phoneNumber;




                        Button btnCall=findViewById(R.id.buttonCall);
                buttonCall.setEnabled(true);

            } else if (resultCode == RESULT_CANCELED) {
                //TextView textView = findViewById(R.id.textView);
                textView.setText("Opération annulée");
            }
        }
    }}


}
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Perm_CTC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission a été accordée, ouvrez la liste de contacts
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            } else {
                // La permission a été refusée, affichez un message d'erreur
                TextView textView = findViewById(R.id.textView);
                textView.setText("Permission refusée");
            }
        }
    }}
