package com.taam.collection_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    protected List<Item> itemList;
    protected FirebaseDatabase db;
    protected DatabaseReference itemsRef;

    public ReportAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item_layout, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        //changed to adjust to new fields
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://taam-management-system.appspot.com/");


        Item item = itemList.get(position);
        holder.textViewLot.setText(item.getLot());
        holder.textViewName.setText(item.getName());
        holder.textViewCategory.setText(item.getCategory());
        holder.textViewPeriod.setText(item.getPeriod());
        holder.textViewDescription.setText(item.getDescription());

        StorageReference storageRef = storage.getReference().child("gallery/" + item.getGalleryUrl());

        //adjust picture size to 1 text row, 5sp?
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .apply(new RequestOptions().override(300, 300)).centerCrop()
                    .into(holder.galleryUrl);
        }).addOnFailureListener(e -> {
            // Handle errors here
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLot, textViewName, textViewCategory, textViewPeriod,
                textViewDescription;

        ImageView galleryUrl;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLot = itemView.findViewById(R.id.textViewLot);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewPeriod = itemView.findViewById(R.id.textViewPeriod);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            galleryUrl = itemView.findViewById(R.id.imageView);
        }
    }
}
