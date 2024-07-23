package com.taam.collection_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private List<CheckBox> checkBoxList;

    public ItemAdapter(List<Item> itemList, List<CheckBox> checkBoxList) {
        this.itemList = itemList;
        this.checkBoxList = checkBoxList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        //changed to adjust to new fields
        Item item = itemList.get(position);
        holder.textViewLot.setText(item.getLot());
        holder.textViewName.setText(item.getName());
        holder.textViewCategory.setText(item.getCategory());
        holder.textViewPeriod.setText(item.getPeriod());
        holder.textViewDescription.setText(item.getDescription());
        checkBoxList.add(holder.selectCheckBox);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLot, textViewName, textViewCategory, textViewPeriod,
                textViewDescription;
        CheckBox selectCheckBox;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLot = itemView.findViewById(R.id.textViewLot);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCategory = itemView.findViewById(R.id.textViewCategory); //change to field
            textViewPeriod = itemView.findViewById(R.id.textViewPeriod);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
        }
    }
}
