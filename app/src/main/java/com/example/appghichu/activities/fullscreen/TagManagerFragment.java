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
import androidx.lifecycle.Observer;
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
import com.example.appghichu.models.TagManagerViewModel;
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

    private MainViewModel model;
    private TagManagerViewModel tagManagerViewModel;

    public TagManagerFragment()
    {

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
        tagManagerViewModel = new ViewModelProvider(this).get(TagManagerViewModel.class);

        model.editorIntent.observe(getViewLifecycleOwner(), result ->
        {
            if(result == null)
                return;

            NoteEntity note = result.getParcelableExtra("note");

            int index = result.getIntExtra("index", 0);

            tagManagerViewModel.changeNoteAt(index, note);
            resultList.getAdapter().notifyItemChanged(index);

            getParentFragmentManager().setFragmentResult("refresh", null);
            model.editorIntent.setValue(null);
        });

        tagManagerViewModel.observeTags().observe(getViewLifecycleOwner(), new Observer<List<TagEntity>>()
        {
            @Override
            public void onChanged(List<TagEntity> tags)
            {
                TagListAdapter adapter = new TagListAdapter(tags, getContext(), true, true);
                adapter.notifyWhenTagsRemoved(() -> updateResultList());
                tagList.setAdapter(adapter);
            }
        });

        tagManagerViewModel.observeNotes().observe(getViewLifecycleOwner(), new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(List<NoteEntity> notes)
            {
                resultList.setAdapter(new NoteListAdapter(notes, (note, index) ->
                {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("note", note);
                    bundle.putInt("index", index);

                    getParentFragmentManager().setFragmentResult("note clicked", bundle);
                }, getContext(), "Không tìm được ghi chú nào cả :("));
            }
        });

        tagList = view.findViewById(R.id.tagList);
        resultList = view.findViewById(R.id.resultList);
        searchEditTxt = view.findViewById(R.id.searchEditTxt);
        backBtn = view.findViewById(R.id.backBtn);

        backBtn.setOnClickListener((View v) -> dismiss());

        tagList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
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

                    if(!tagManagerViewModel.contains(searchTerm))
                    {
                        tagManagerViewModel.insertNewTag(new TagEntity(searchTerm));
                        tagList.getAdapter().notifyItemInserted(tagManagerViewModel.getLastPosition());

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
        if(tagManagerViewModel.getLastPosition() == -1)
        {
            List<NoteEntity> result = new ArrayList<>();
            result.add(null);
            tagManagerViewModel.updateResult(result);

            return;
        }

        StringBuilder condition = new StringBuilder();

        if(tagManagerViewModel.getLastPosition() == 0)
            condition.append("_group LIKE ?");
        else
        {
            for(int j=0;j<=tagManagerViewModel.getLastPosition();j++)
            {
                if(j == tagManagerViewModel.getLastPosition())
                    condition.append("_group LIKE ?");
                else
                    condition.append("_group LIKE ? AND ");
            }
        }

        String[] args = new String[tagManagerViewModel.getLastPosition() + 1];

        for(int j=0;j<args.length;j++)
            args[j] = '%' + tagManagerViewModel.tagAt(j).getTagName() + '%';

        String query = "Select * from note n1 INNER JOIN(Select id from (Select n.id, group_concat(t.tagName) as _group from note n" +
                " INNER JOIN tag t ON n.id = t.noteId group by n.id) " +
                "Where (" + condition + ")) n2 ON n1.id = n2.id;";

        List<NoteEntity> notes = AppDatabase.getInstance(getContext()).noteInterface().searchUsingTags(
                        new SimpleSQLiteQuery(query, args));

        if(notes.size() == 0)
            notes.add(null);

        tagManagerViewModel.updateResult(notes);
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