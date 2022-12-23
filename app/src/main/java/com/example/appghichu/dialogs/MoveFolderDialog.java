package com.example.appghichu.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.interfaces.OnMoveFoldersListener;
import com.example.appghichu.interfaces.SimpleCallBack;
import com.example.appghichu.objects.dtos.AnimatingFolderDTO;
import com.example.appghichu.objects.entities.FolderEntity;

import java.util.ArrayList;
import java.util.List;

public class MoveFolderDialog extends DialogFragment
{
    private Button okBtn;
    private Spinner dropdownList;

    private List<AnimatingFolderDTO> folders;

    private int folderIDMoveTo;
    private OnMoveFoldersListener callback;

    public MoveFolderDialog(List<AnimatingFolderDTO> folders, OnMoveFoldersListener callback)
    {
        this.folders = folders;
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.move_folder_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        okBtn = view.findViewById(R.id.okBtn);
        dropdownList = view.findViewById(R.id.dropdownList);

        List<String> folderNames = new ArrayList<>();

        folderNames.add("Thu muc chinh");
        for(AnimatingFolderDTO folder : folders)
            folderNames.add(folder.folder.getFolderName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, folderNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownList.setAdapter(adapter);
        dropdownList.setSelection(0);
        dropdownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
            {
                if(position == 0)
                {
                    folderIDMoveTo = 0;
                    return;
                }

                folderIDMoveTo = folders.get(position - 1).folder.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        okBtn.setOnClickListener((View v) ->
        {
            callback.onMoveFolders(folderIDMoveTo);
            dismiss();
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }
}
