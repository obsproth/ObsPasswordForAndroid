package com.github.obsproth.obspasswordforandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.obsproth.obspassword.HashUtil;
import com.github.obsproth.obspassword.ServiceElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 1;

    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText passwordField = (EditText) findViewById(R.id.pass_field);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this, passwordField);
        recyclerView.setAdapter(myAdapter);

        Button button = (Button) findViewById(R.id.button_new_service);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View dialog = getLayoutInflater().inflate(R.layout.new_service, null);
                new AlertDialog.Builder(MainActivity.this).setView(dialog)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String name = ((EditText) dialog.findViewById(R.id.dialog_name)).getText().toString();
                                int length = Integer.parseInt(((EditText) dialog.findViewById(R.id.dialog_length)).getText().toString());
                                String passwordStr = ((EditText) dialog.findViewById(R.id.dialog_password)).getText().toString();
                                myAdapter.add(new ServiceElement(name, length, HashUtil.getBaseHashStr(passwordStr.toCharArray())));
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = resultData.getData();
            List<ServiceElement> elementList = loadCSV(uri);
            myAdapter.addAll(elementList);
        }
    }

    private List<ServiceElement> loadCSV(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            List<ServiceElement> list = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                list.add(ServiceElement.buildFromCSV(line));
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
