package com.taam.collection_management_system;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import com.google.android.material.button.MaterialButton;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;



import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class AddItemFragment extends Fragment {
    private EditText editTextName, editTextLot, editTextDescription, editNewCat, editNewPer;
    private Spinner spinnerCategory;
    private Spinner spinnerPeriod;
    protected ArrayAdapter<String> sC;
    protected ArrayAdapter<String> sP;

    private Button buttonAdd, buttonAddCatPer;
    private ImageView imageView;
    private Uri video;
    private LinearProgressIndicator progressIndicator;
    private MaterialButton selectVideo;

    private DatabaseReference catReference, perReference;
    private FirebaseDatabase db;
    private StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextLot = view.findViewById(R.id.editTextLot);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        imageView = view.findViewById(R.id.imageView);
        progressIndicator = view.findViewById(R.id.process);
        selectVideo = view.findViewById(R.id.selectVideo);
        editNewCat = view.findViewById(R.id.editNewCat);
        editNewPer = view.findViewById(R.id.editNewPer);
        buttonAddCatPer = view.findViewById(R.id.buttonAddCatPer);

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        catReference = db.getReference("categories/");
        perReference = db.getReference("periods/");
        FirebaseApp.initializeApp(requireContext());
        storageReference = FirebaseStorage.getInstance().getReference();


        sC = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        sP = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(sC);
        spinnerPeriod.setAdapter(sP);

        AppCompatActivity activity = (AppCompatActivity) getActivity();


        catReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sC.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    String category = snapshot1.child("category").getValue(String.class);
                    sC.add(category);
                }
                sC.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    //error
            }
        });

        perReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sP.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    String period = snapshot1.child("period").getValue(String.class);
                    sP.add(period);
                }
                sP.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {video = result.getData().getData();
                            if (video != null) {
                                Glide.with(getContext()).load(video).into(imageView);
                            } else {
                                Toast.makeText(getContext(), "No video selected.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Please select image/video.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("*/*");
                activityResultLauncher.launch(intent);
            }
        });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });


        buttonAddCatPer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCatPer();
            }
        });

        return view;
    }


    private void addItem() {
        String name = editTextName.getText().toString().trim();
        String lot = editTextLot.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString().toLowerCase();
        String period = spinnerPeriod.getSelectedItem().toString().toLowerCase();

        if (name.isEmpty() || lot.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!lot.matches("-?\\d+")) {
            Toast.makeText(getContext(), "Lot must be a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (video == null) {
            Toast.makeText(getContext(), "Must upload photo/video!~", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference myRef = db.getReference("/collection_data");

        myRef.orderByChild("lot").equalTo(lot).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(getContext(), "Lot number already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }

                StorageReference reference = storageReference.child("gallery/" +
                        UUID.randomUUID().toString());
                reference.putFile(video).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String[] completeUrl = reference.toString().split("/");
                        String galleryUrl = completeUrl[4];

                        Item item = new Item(lot, name, category, period, description, galleryUrl);

                        myRef.child(lot).setValue(item).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to upload~", Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressIndicator.setVisibility(View.VISIBLE);
                        progressIndicator.setMax(Math.toIntExact(snapshot.getTotalByteCount()));
                        progressIndicator.setProgress(Math.toIntExact(snapshot.getBytesTransferred()));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error
            }
        });
    }

    private void addCatPer() {
        String newCat = editNewCat.getText().toString().trim().toLowerCase();
        String newPer = editNewPer.getText().toString().trim().toLowerCase();

        if (newCat.isEmpty() && newPer.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please input a Category or Period to add. It can be one, or both!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newCat.isEmpty()) {
            catReference.orderByChild("category").equalTo(newCat).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(getContext(), "Category already exists.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference catRef = catReference.push();
                    catRef.child("category").setValue(newCat).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Category added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (!newPer.isEmpty()) {
            perReference.orderByChild("period").equalTo(newPer).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(getContext(), "Period already exists.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference perRef = perReference.push();
                    perRef.child("period").setValue(newPer).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Period added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to add period", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //error
                }
            });
        }

    }
}