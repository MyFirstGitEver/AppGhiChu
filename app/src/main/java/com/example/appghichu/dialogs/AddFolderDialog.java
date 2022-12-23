package com.example.appghichu.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appghichu.R;
import com.example.appghichu.interfaces.MainOptionListener;

public class AddFolderDialog extends DialogFragment
{
    private EditText folderNameEditTxt;
    private Button okBtn;
    private MainOptionListener listener;

    public AddFolderDialog(MainOptionListener listener)
    {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.folder_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        folderNameEditTxt = view.findViewById(R.id.folderNameEditTxt);
        okBtn = view.findViewById(R.id.okBtn);

        okBtn.setOnClickListener((View v) ->
        {
            String folderName = folderNameEditTxt.getText().toString();

            if(folderName.equals(""))
                return;

            listener.onCreateNewFolder(folderName);
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
