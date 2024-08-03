package com.taam.collection_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends ReportAdapter {
    private List<CheckBox> checkBoxList;

    public ItemAdapter(List<Item> itemList, List<CheckBox> checkBoxList) {
        super(itemList);
        this.checkBoxList = checkBoxList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);
        checkBoxList.add(holder.selectCheckBox);
        return holder;
    }


    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        //changed to adjust to new fields
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends ReportViewHolder {
        CheckBox selectCheckBox;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
        }
    }
}
