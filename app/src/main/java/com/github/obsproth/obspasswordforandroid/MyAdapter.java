package com.github.obsproth.obspasswordforandroid;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.obsproth.obspassword.ServiceElement;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ServiceElement> dataList;
    private LayoutInflater inflater;

    public MyAdapter(Context context) {
        this.dataList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewLength;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.elem_text_name);
            textViewLength = (TextView) itemView.findViewById(R.id.elem_text_length);
        }
    }
}
