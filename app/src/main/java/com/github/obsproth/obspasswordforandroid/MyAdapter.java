package com.github.obsproth.obspasswordforandroid;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.obsproth.obspassword.HashUtil;
import com.github.obsproth.obspassword.ServiceElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ServiceElement> dataList;
    private LayoutInflater inflater;

    private EditText passwordField;

    public MyAdapter(Context context, EditText passwordField) {
        this.dataList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);

        this.passwordField = passwordField;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.recycler_element, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ServiceElement serviceElement = dataList.get(position);
        holder.textViewName.setText(serviceElement.getServiceName());
        holder.textViewLength.setText(String.valueOf(serviceElement.getLength()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void add(ServiceElement element) {
        dataList.add(element);
        notifyItemInserted(dataList.size() - 1);
    }

    public void addAll(List<ServiceElement> list) {
        int start = dataList.size();
        dataList.addAll(list);
        notifyItemRangeInserted(start, list.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewName, textViewLength;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.elem_text_name);
            textViewLength = (TextView) itemView.findViewById(R.id.elem_text_length);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            int position = getLayoutPosition();
            ServiceElement element = dataList.get(position);

            char[] password = new char[passwordField.length()];
            passwordField.getText().getChars(0, passwordField.length(), password, 0);
            if (element.getBaseHash().equals(HashUtil.getBaseHashStr(password, false))) {
                byte[] passByte = HashUtil.calcHash(password, element.getServiceName(), element.getLength());
                final String passwordStr = Base64.encodeToString(passByte, Base64.DEFAULT).substring(0, element.getLength());
                new AlertDialog.Builder(view.getContext())
                        .setMessage("Success!")
                        .setPositiveButton("Copy to the clipboard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setPrimaryClip(ClipData.newPlainText("ObsPassword", passwordStr));
                                Toast.makeText(view.getContext(), "Success!", Toast.LENGTH_SHORT);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNeutralButton("Show", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new AlertDialog.Builder(view.getContext())
                                        .setMessage(passwordStr)
                                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface2, int i) {
                                                dialogInterface2.dismiss();
                                            }
                                        })
                                        .show();
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            } else {
                Arrays.fill(password, (char) 0);
                Toast.makeText(view.getContext(), "Hash mismatch", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
