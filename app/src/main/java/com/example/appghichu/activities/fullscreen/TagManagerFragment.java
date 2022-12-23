package com.example.appghichu.activities.fullscreen;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.adapters.NoteListAdapter;
import com.example.appghichu.adapters.TagListAdapter;
import com.example.appghichu.interfaces.OnNoteClickListener;
import com.example.appghichu.interfaces.SimpleCallBack;
import com.example.appghichu.models.MainViewModel;
import com.example.appghichu.objects.entities.NoteEntity;
import com.example.appghichu.objects.entities.TagEntity;

import java.util.ArrayList;
import java.util.List;

public class TagManagerFragment extends DialogFragment
{
    private RecyclerView tagList, resultList;
    private ImageButton backBtn;
    private EditText searchEditTxt;

    private boolean justModified;

    private ArrayList<TagEntity> tags = new ArrayList<>();
    private List<NoteEntity> notes;
    private TagListAdapter tagAdapter;
    private NoteListAdapter noteAdapter;
    private MainViewModel model;

    private OnNoteClickListener listener;
    private SimpleCallBack refreshListener;

    public TagManagerFragment()
    {

    }

    public TagManagerFragment(OnNoteClickListener listener, SimpleCallBack refreshListener)
    {
        this.listener = listener;
        this.refreshListener = refreshListener;
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

        model = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        notes = new ArrayList<>();
        notes.add(null);

        model.editorIntent.observe(getViewLifecycleOwner(), result ->
        {
            if(result == null)
                return;

            NoteEntity note = result.getParcelableExtra("note");

            int index = result.getIntExtra("index", 0);
            notes.set(index, note);
            tagAdapter.notifyItemChanged(index);
            model.editorIntent.setValue(null);

            noteAdapter.notifyItemChanged(index);
            refreshListener.run();
        });

        tagList = view.findViewById(R.id.tagList);
        resultList = view.findViewById(R.id.resultList);
        searchEditTxt = view.findViewById(R.id.searchEditTxt);
        backBtn = view.findViewById(R.id.backBtn);

        backBtn.setOnClickListener((View v) -> dismiss());

        tagList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        tagAdapter = new TagListAdapter(tags, null, true, true);
        tagAdapter.notifyWhenTagsRemoved(() -> updateResultList());
        tagList.setAdapter(tagAdapter);

        resultList.setLayoutManager(new LinearLayoutManager(getContext()));
        noteAdapter = new NoteListAdapter(notes, listener, getContext(), "Không tìm thấy ghi chú nào!");
        resultList.setAdapter(noteAdapter);
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
                        tagAdapter.notifyItemInserted(tags.size() - 1);

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
        notes.clear();

        if(tags.size() == 0)
        {
            notes.add(null);
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

        notes = AppDatabase.getInstance(getContext()).noteInterface().searchUsingTags(
                        new SimpleSQLiteQuery(query, args));


        if(notes.size() == 0)
            notes.add(null);

        noteAdapter.replaceList(notes);
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