package com.example.appghichu.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.daos.FolderDAO;
import com.example.appghichu.interfaces.MainOptionListener;
import com.example.appghichu.interfaces.RenameFolderListener;
import com.example.appghichu.interfaces.SimpleCallBack;

public class RenameFolderDialog extends DialogFragment
{
    private EditText folderNameEditTxt;
    private Button okBtn;
    private TextView titleTxt;

    public RenameFolderDialog()
    {

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
        titleTxt = view.findViewById(R.id.titleTxt);

        okBtn.setOnClickListener((View v) ->
        {
            FolderDAO folderInterface = AppDatabase.getInstance(getContext()).folderInterface();

            String folderName = folderNameEditTxt.getText().toString();

            if(folderName.equals(""))
                return;

            if(folderInterface.checkIfFolderAlreadyExists(folderName) == 0)
            {
                folderInterface.renameFolder(folderName, getArguments().getInt("id"));

                Bundle bundle = new Bundle();
                bundle.putString("folderName", folderName);
                bundle.putInt("index", getArguments().getInt("index"));

                getParentFragmentManager().setFragmentResult("rename", bundle);
                dismiss();
            }
            else
                Toast.makeText(getContext(), "This folder name already exists", Toast.LENGTH_SHORT).show();
        });
        titleTxt.setText("Đổi tên thư mục");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }
}
