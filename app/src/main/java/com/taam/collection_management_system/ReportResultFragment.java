package com.taam.collection_management_system;

import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReportResultFragment extends ReportFragment {
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private TextView searchResultTextView;
    private int full = 1;
    private RecyclerView recyclerView;

    public static ReportResultFragment newInstance(String lotNumber, String name, String category, String period, int signal) {
        ReportResultFragment fragment = new ReportResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOT_NUMBER, lotNumber);
        args.putString(ARG_NAME, name);
        args.putString(ARG_CATEGORY, category);
        args.putString(ARG_PERIOD, period);
        args.putInt(ARG_SIGNAL, signal);
        fragment.setArguments(args);
        return fragment;
    }

    public static ReportResultFragment newInstance() {
        ReportResultFragment fragment = new ReportResultFragment();
        fragment.setArguments(null);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            queryItem = new Item();
            queryItem.setLot(getArguments().getString(ARG_LOT_NUMBER, ""));
            queryItem.setName(getArguments().getString(ARG_NAME, ""));
            queryItem.setCategory(getArguments().getString(ARG_CATEGORY, ""));
            queryItem.setPeriod(getArguments().getString(ARG_PERIOD, ""));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        int signal = getArguments().getInt(ARG_SIGNAL);
        if (signal == 1) {
            view = inflater.inflate(R.layout.fragment_report_result, container, false);
            full = 1;
        } else {
            view = inflater.inflate(R.layout.report_desc_img_only, container, false);
            full = 0;
        }

        searchResultTextView = view.findViewById(R.id.searchResultTextView);
        ImageButton buttonBack = view.findViewById(R.id.buttonBack);
        ImageButton buttonPrintPDF = view.findViewById(R.id.buttonPrintPDF);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        buttonPrintPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (full == 1) {
                    convertXMLtoPDF_full();
                } else {
                    convertXMLtoPDF();
                }
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        fillRecycler(recyclerView, signal);

        return view;
    }

    private void convertXMLtoPDF_full() {
        View view = getView(); // Get the current view of the fragment
        if (view == null) {
            Toast.makeText(getContext(), "Error: View not found", Toast.LENGTH_SHORT).show();
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().getDisplay().getRealMetrics(displayMetrics);
        } else {
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }

        // Hide the buttons before capturing the view
        ImageButton buttonBack = view.findViewById(R.id.buttonBack);
        ImageButton buttonPrintPDF = view.findViewById(R.id.buttonPrintPDF);
        buttonBack.setVisibility(View.GONE);
        buttonPrintPDF.setVisibility(View.GONE);

        view.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(300 + recyclerView.computeVerticalScrollRange(), View.MeasureSpec.EXACTLY));
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);

        PdfDocument document = new PdfDocument();
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        document.finishPage(page);

        // Show the buttons again after capturing the view
        buttonBack.setVisibility(View.VISIBLE);
        buttonPrintPDF.setVisibility(View.VISIBLE);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "Full Report.pdf";
        File file = new File(downloadsDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(getContext(), "Successfully downloaded PDF", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d("myLog", "Error while writing " + e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void convertXMLtoPDF() {
        View view = getView(); // Get the current view of the fragment
        if (view == null) {
            Toast.makeText(getContext(), "Error: View not found", Toast.LENGTH_SHORT).show();
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().getDisplay().getRealMetrics(displayMetrics);
        } else {
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }

        // Hide the buttons before capturing the view
        ImageButton buttonBack = view.findViewById(R.id.buttonBack);
        ImageButton buttonPrintPDF = view.findViewById(R.id.buttonPrintPDF);
        buttonBack.setVisibility(View.GONE);
        buttonPrintPDF.setVisibility(View.GONE);

        view.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(300 + recyclerView.computeVerticalScrollRange(), View.MeasureSpec.EXACTLY));
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);

        PdfDocument document = new PdfDocument();
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        document.finishPage(page);

        // Show the buttons again after capturing the view
        buttonBack.setVisibility(View.VISIBLE);
        buttonPrintPDF.setVisibility(View.VISIBLE);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "Description and Picture Report.pdf";
        File file = new File(downloadsDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(getContext(), "Successfully downloaded PDF", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d("myLog", "Error while writing " + e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}