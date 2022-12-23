package com.example.appghichu.dialogs;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.appghichu.R;
import com.example.appghichu.interfaces.MainOptionListener;

public class SortNoteDialog extends DialogFragment
{
    private AppCompatButton byDateBtn, byTitleBtn;
    private AppCompatButton increasingBtn, decreasingBtn;
    private MainOptionListener listener;

    private Button okBtn;

    private boolean byDate = true, increasing = true;

    public SortNoteDialog(MainOptionListener listener)
    {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.sort_note_dialog, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        byDateBtn = view.findViewById(R.id.byDateBtn);
        byTitleBtn = view.findViewById(R.id.byTitleBtn);
        increasingBtn = view.findViewById(R.id.increasingBtn);
        decreasingBtn = view.findViewById(R.id.decreasingBtn);
        okBtn = view.findViewById(R.id.okBtn);

        byDateBtn.setOnClickListener((View v) ->
        {
            byDateBtn.setBackgroundResource(R.drawable.round_bg);
            byTitleBtn.setBackgroundResource(R.drawable.transparent_round_bg);
            byDate = true;
        });

        byTitleBtn.setOnClickListener((View v) ->
        {
            byDateBtn.setBackgroundResource(R.drawable.transparent_round_bg);
            byTitleBtn.setBackgroundResource(R.drawable.round_bg);
            byDate = false;
        });

        increasingBtn.setOnClickListener((View v) ->
        {
            increasingBtn.setBackgroundResource(R.drawable.round_bg);
            decreasingBtn.setBackgroundResource(R.drawable.transparent_round_bg);

            increasing = true;
        });

        decreasingBtn.setOnClickListener((View v) ->
        {
            decreasingBtn.setBackgroundResource(R.drawable.round_bg);
            increasingBtn.setBackgroundResource(R.drawable.transparent_round_bg);

            increasing = false;
        });

        okBtn.setOnClickListener((View v) -> listener.onSort(byDate, increasing));
    }
}
