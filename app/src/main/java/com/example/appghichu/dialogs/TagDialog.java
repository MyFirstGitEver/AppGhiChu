package com.example.appghichu.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appghichu.R;
import com.example.appghichu.adapters.TagListAdapter;
import com.example.appghichu.objects.entities.TagEntity;
import java.util.List;

public class TagDialog extends DialogFragment
{
    private int noteID;

    private Button saveBtn, addBtn;
    private RecyclerView tagList;
    private EditText tagNameEditTxt;

    private List<TagEntity> tags;

    public TagDialog()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tag_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        noteID = getArguments().getInt("noteId");
        tags = getArguments().getParcelableArrayList("tags");

        saveBtn = view.findViewById(R.id.saveBtn);
        addBtn = view.findViewById(R.id.addBtn);

        tagList = view.findViewById(R.id.tagList);
        tagNameEditTxt = view.findViewById(R.id.tagNameEditTxt);

        tagList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        tagList.setAdapter(new TagListAdapter(tags, getContext(), true, false));

        addBtn.setOnClickListener((View v) ->
        {
            String tagName = tagNameEditTxt.getText().toString();

            if(tagName.equals(""))
                return;

            tagNameEditTxt.setText("");

            for(TagEntity tag : tags)
            {
                if(tag.getTagName().equals(tagName))
                    return;
            }

            tags.add(new TagEntity(noteID, tagName));
            tagList.getAdapter().notifyItemInserted(tags.size() - 1);
        });

        saveBtn.setOnClickListener((View v) -> dismiss());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }
}
