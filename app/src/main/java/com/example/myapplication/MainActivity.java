package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button3);
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserDialog();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showUpdateDialog(position);
            }
        });
    }

    public void showAddUserDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_task_dialog, null);

        final EditText editTextTaskName = dialogView.findViewById(R.id.editTextTaskName);
        final EditText editTextTaskDescription = dialogView.findViewById(R.id.editText);
        final EditText editTextCategory = dialogView.findViewById(R.id.editTextCategory);
        final EditText editTextDate = dialogView.findViewById(R.id.editTextDate);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Yeni Kullanıcı Ekle")
                .setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Kullanıcı ekleme işlemini gerçekleştir
                        String taskName = editTextTaskName.getText().toString();
                        String taskDescription = editTextTaskDescription.getText().toString();
                        String category = editTextCategory.getText().toString();
                        String date = editTextDate.getText().toString();

                        arrayList.add(taskName);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // İptal edildi
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void showUpdateDialog(final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.update_item_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final TextView userInputDialogEditText = mView.findViewById(R.id.userInputDialog);
        userInputDialogEditText.setText(arrayList.get(position));

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        showAddUserDialog();
                    }
                })
                .setNeutralButton("Detaylar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                        startActivity(intent);
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