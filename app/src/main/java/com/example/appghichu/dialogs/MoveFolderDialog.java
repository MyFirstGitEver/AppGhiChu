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

    private List<FolderEntity> folders;

    public MoveFolderDialog()
    {

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

        folders = AppDatabase.getInstance(getContext()).folderInterface().listAllFoldersUnderManagement();

        for(FolderEntity folder : folders)
            folderNames.add(folder.getFolderName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, folderNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownList.setAdapter(adapter);
        dropdownList.setSelection(0);

        okBtn.setOnClickListener((View v) ->
        {
            int folderIDMoveTo;

            if(dropdownList.getSelectedItemPosition() == 0)
                folderIDMoveTo = 0;
            else
                folderIDMoveTo = folders.get(dropdownList.getSelectedItemPosition() - 1).getId();

            if(!getArguments().getBoolean("multiple"))
            {
                Bundle bundle = new Bundle();
                bundle.putInt("id", folderIDMoveTo);
                bundle.putInt("folderId", getArguments().getInt("folderId"));

                getParentFragmentManager().setFragmentResult("move", bundle);
            }
            else
            {
                Bundle bundle = new Bundle();
                bundle.putInt("id", folderIDMoveTo);

                getParentFragmentManager().setFragmentResult("move multiple", bundle);
            }
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
