package com.taam.collection_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ReportDescAdapter extends RecyclerView.Adapter<ReportDescAdapter.ReportDescViewHolder>  {
    private List<Item> itemList;
    protected FirebaseDatabase db;
    protected DatabaseReference itemsRef;

    public ReportDescAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ReportDescAdapter.ReportDescViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_desc_img_only_layout, parent, false);
        return new ReportDescAdapter.ReportDescViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportDescAdapter.ReportDescViewHolder holder, int position) {
        //changed to adjust to new fields
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://taam-management-system.appspot.com/");


        Item item = itemList.get(position);
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


public static class ReportDescViewHolder extends RecyclerView.ViewHolder {
    TextView textViewDescription;

    ImageView galleryUrl;

    public ReportDescViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewDescription = itemView.findViewById(R.id.textViewDescription);
        galleryUrl = itemView.findViewById(R.id.imageView);
    }
}





}
