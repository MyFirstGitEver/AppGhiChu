package com.example.appghichu.activities.fullscreen;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.Utils;
import com.example.appghichu.activities.EditorActivity;
import com.example.appghichu.activities.MainActivity;
import com.example.appghichu.adapters.NoteListAdapter;
import com.example.appghichu.adapters.TagListAdapter;
import com.example.appghichu.interfaces.OnNoteClickListener;
import com.example.appghichu.objects.entities.NoteEntity;
import com.example.appghichu.objects.entities.TagEntity;

import java.util.ArrayList;
import java.util.List;

public class TagManagerFragment extends DialogFragment
{
    private RecyclerView tagList, resultList;
    private ImageButton backBtn;
    private EditText searchEditTxt;
    private ArrayList<TagEntity> tags = new ArrayList<>();
    private TagListAdapter adapter;

    private boolean justModified;
    private int currentFolderID;

    private OnNoteClickListener listener;

    public TagManagerFragment()
    {

    }

    public TagManagerFragment(OnNoteClickListener listener, int currentFolderID)
    {
        this.listener = listener;
        this.currentFolderID = currentFolderID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tag_manager_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tagList = view.findViewById(R.id.tagList);
        resultList = view.findViewById(R.id.resultList);
        searchEditTxt = view.findViewById(R.id.searchEditTxt);
        backBtn = view.findViewById(R.id.backBtn);

        backBtn.setOnClickListener((View v) -> dismiss());

        tagList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapter = new TagListAdapter(tags, null, true, true);
        adapter.notifyWhenTagsRemoved(() -> updateResultList());
        tagList.setAdapter(adapter);

        resultList.setLayoutManager(new LinearLayoutManager(getContext()));

        searchEditTxt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(justModified)
                {
                    justModified = false;
                    return;
                }

                int len = charSequence.length();

                if(len <  2)
                    return;

                if(charSequence.charAt(len - 1) == ' ' && charSequence.charAt(len - 2) == ' ')
                {
                    String searchTerm = charSequence.toString();
                    searchTerm = searchTerm.substring(0, searchTerm.length() - 2);

                    if(!tags.contains(searchTerm))
                    {
                        tags.add(new TagEntity(searchTerm));
                        adapter.notifyItemInserted(tags.size() - 1);

                        updateResultList();
                    }

                    searchEditTxt.setText("");
                    justModified = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
    }

    public void updateResultList()
    {
        if(tags.size() == 0)
        {
            resultList.setAdapter(new NoteListAdapter(new ArrayList<>(), listener, getContext(), "Không tìm thấy ghi chú nào!"));
            return;
        }

        StringBuilder condition = new StringBuilder();

        if(tags.size() == 1)
            condition.append("_group LIKE ?");
        else
        {
            for(int j=0;j<tags.size();j++)
            {
                if(j == tags.size() - 1)
                    condition.append("_group LIKE ?");
                else
                    condition.append("_group LIKE ? AND ");
            }
        }

        String[] args = new String[tags.size()];

        for(int j=0;j<args.length;j++)
            args[j] = '%' + tags.get(j).getTagName() + '%';

        String query = "Select * from note n1 INNER JOIN(Select id from (Select n.id, group_concat(t.tagName) as _group from note n" +
                " INNER JOIN tag t ON n.id = t.noteId group by n.id) " +
                "Where (" + condition + ")) n2 ON n1.id = n2.id;";


        List<NoteEntity> notes =
                AppDatabase.getInstance(getContext()).noteInterface().searchUsingTags(
                        new SimpleSQLiteQuery(query, args));

        if(notes.size() == 0)
            notes.add(null);

        resultList.setAdapter(new NoteListAdapter(notes, listener, getContext(), "Không tìm thấy ghi chú nào"));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
        {
            dialog.getWindow().getAttributes().windowAnimations = R.style.tagAnimStyle;
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}