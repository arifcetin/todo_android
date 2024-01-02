package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {
    private TextView hAdı,hAciklama,hKategori,hBaslangicT,hBitisT;
    private ListView hAlt;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private Button btn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        hAdı = findViewById(R.id.hedefAdi);
        hAciklama = findViewById(R.id.hedefAciklama);
        hKategori = findViewById(R.id.kategori);
        hBaslangicT = findViewById(R.id.baslangicTarihi);
        hBitisT = findViewById(R.id.bitisTarihi);
        btn = findViewById(R.id.altHedefEkle);
        hAlt = findViewById(R.id.altHedefler);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        hAlt.setAdapter(adapter);

        String hedefAdi = getIntent().getStringExtra("hedefAdi");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        db.collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.get("userId").equals(user.getUid()) && document.get("Hedef Adı").equals(hedefAdi)) {
                                    db.collection("tasks").document(document.getId()).update("bittim gözün aydın", true);
                                    hAdı.setText((String)document.get("Hedef Adı"));
                                    hAciklama.setText((String)document.get("Hedef Açıklama"));
                                    hKategori.setText((String)document.get("Kategori"));
                                    hBaslangicT.setText(sdf.format(document.getTimestamp("Başlangıç Tarihi").toDate()));
                                    hBitisT.setText((String)document.get("Bitiş Tarihi"));
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_altHedef_dialog();
            }
        });
    }

    private void add_altHedef_dialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.add_althedef_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText althedef = mView.findViewById(R.id.alt);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setTitle("Alt Hedef Ekle")
                .setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        String alt = althedef.getText().toString();

                        arrayList.add(alt);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }
}
