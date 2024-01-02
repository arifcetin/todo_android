package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    float maxTask= 0.0F,tTask=0.0F;
    private Button btn;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onRestart() {
        super.onRestart();
        db.collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("userId").equals(user.getUid())){
                                    maxTask++;
                                    if (document.get("bittim gözün aydın").equals(true)){
                                        tTask++;
                                    }
                                }
                            }
                            pieChart(tTask,maxTask);
                        }
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        onRestart();
        getDaily();

        btn = findViewById(R.id.button6);
        listView = findViewById(R.id.daily);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyActivity();
            }
        });

    }
    public void pieChart(float tTask, float maxTask){
        PieChart pieChart = findViewById(R.id.pieChart);
        Log.d(TAG,"maxTask=> " + maxTask);
        Log.d(TAG,"tTaskk=> " + tTask);
        Log.d(TAG,"Tamamlanan Hedef=> " + (tTask/maxTask)*100);
        Log.d(TAG,"Tamamlanmayan Hedef=> " + ((maxTask-tTask)/maxTask)*100);
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((tTask/maxTask)*100, "Tamamlanan Hedef"));
        entries.add(new PieEntry(((maxTask-tTask)/maxTask)*100, "Tamamlanmayan Hedef"));

        PieDataSet dataSet = new PieDataSet(entries, "Pasta Grafiği");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    public void dailyActivity(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.add_daily_activity_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        EditText text = mView.findViewById(R.id.dailyA);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setTitle("Alt Hedef Ekle")
                .setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        String txt = text.getText().toString();
                        arrayList.add(txt);
                        adapter.notifyDataSetChanged();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DAY_OF_MONTH,1);

                        Map<String, Object> daily = new HashMap<>();
                        daily.put("Günlük Aktivite", txt);
                        daily.put("Başlangıç tarihi", new Date());
                        daily.put("Bitiş Tarihi", calendar.getTime());
                        daily.put("userId", user.getUid());

                        db.collection("daily")
                                .add(daily)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(DailyActivity.this, "Hedef Kaydedildi", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DailyActivity.this, "Hedef Kaydedilemedi", Toast.LENGTH_LONG).show();
                                    }
                                });
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

    public void getDaily(){
        db.collection("daily")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.get("userId").equals(user.getUid()) && !document.getDate("Bitiş Tarihi").after(new Date())) {
                                    arrayList.add((String) document.get("Günlük Aktivite"));
                                    adapter.notifyDataSetChanged();
                                }
                                else {
                                    db.collection("daily").document(document.getId()).delete();
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}