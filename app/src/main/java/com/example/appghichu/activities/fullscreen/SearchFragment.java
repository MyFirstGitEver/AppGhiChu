package com.example.appghichu.activities.fullscreen;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.adapters.NoteListAdapter;
import com.example.appghichu.adapters.NotePageListAdapter;
import com.example.appghichu.interfaces.NotePageListener;
import com.example.appghichu.interfaces.OnNoteClickListener;
import com.example.appghichu.interfaces.SimpleCallBack;
import com.example.appghichu.models.MainViewModel;
import com.example.appghichu.objects.entities.NoteEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchFragment extends DialogFragment
{
    private static final int BY_CONTENT = 0;

    private ImageButton backBtn;
    private Spinner categoryPicker;
    private EditText searchEditTxt;
    private RecyclerView noteList;

    private MainViewModel model;

    private NoteListAdapter adapter;
    private List<NoteEntity> notes;
    private List<String> realTitles;
    private int searchMode = BY_CONTENT;

    public SearchFragment()
    {

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed()
            {
                dismiss();
                getParentFragmentManager().setFragmentResult("refresh", null);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        model.editorIntent.observe(getViewLifecycleOwner(), result ->
        {
            if(result == null)
                return;

            NoteEntity note = result.getParcelableExtra("note");

            int index = result.getIntExtra("index", 0);
            notes.set(index, note);
            adapter.notifyItemChanged(index);
            model.editorIntent.setValue(null);
        });

        categoryPicker = view.findViewById(R.id.categoryPicker);
        searchEditTxt = view.findViewById(R.id.searchEditTxt);
        noteList = view.findViewById(R.id.noteList);
        backBtn = view.findViewById(R.id.backBtn);

        notes = new ArrayList<>();
        realTitles = new ArrayList<>();

        noteList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NoteListAdapter(notes,
                (note, index) ->
                {
                    // search fragment catches this first
                    if(index < realTitles.size())
                        note.setTitle(realTitles.get(index));

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("note", note);
                    bundle.putInt("index", index);

                    getParentFragmentManager().setFragmentResult("note clicked", bundle);
                }, getContext(), "Không tìm thấy ghi chú :(");
        noteList.setAdapter(adapter);

        String[] categories = new String[]{"Theo nội dung", "Theo tiêu đề"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryPicker.setAdapter(adapter);
        categoryPicker.setSelection(0);
        categoryPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
            {
                searchMode = position;
                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        searchEditTxt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                search();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        backBtn.setOnClickListener((View v) ->
        {
            dismiss();
            getParentFragmentManager().setFragmentResult("refresh", null);
        });
    }

    private void searchByTitle(String searchTerm)
    {
        notes = AppDatabase.getInstance(getContext()).noteInterface().searchUsingTitle('%' + searchTerm + '%');

        if(notes.size() == 0)
            notes.add(null);

        adapter.replaceList(notes);
    }

    private void searchByContent(String searchTerm)
    {
        realTitles.clear();
        List<NoteEntity> notes = AppDatabase.getInstance(getContext()).noteInterface().listAll();

        for(NoteEntity note : notes)
        {
            File noteFolder = new File(getContext().getFilesDir(), "Note " + note.getId());

            String html = "";
            for(int i=0;i<note.getNumberOfPages();i++)
            {
                File assetFolder = new File(noteFolder, note.getId() + "." + i);
                File htmlFile = new File(assetFolder, "html");

                byte[] htmlBytes = new byte[(int) htmlFile.length()];

                FileInputStream fIn = null;
                try
                {
                    fIn = new FileInputStream(htmlFile);
                    fIn.read(htmlBytes);

                    html = new String(htmlBytes);

                    Document doc = Jsoup.parse(html);

                    String rawContent = doc.body().text();

                    if(Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE).matcher(rawContent).find())
                    {
                        String boldText = boldSearchTerm(rawContent, searchTerm);
                        realTitles.add(note.getTitle());
                        note.setTitle(boldText);
                        this.notes.add(note);

                        break;
                    }

                    fIn.close();
                }
                catch (IOException e)
                {
                    try
                    {
                        fIn.close();
                    }
                    catch (IOException ioException)
                    {
                        Toast.makeText(getContext(), "From search fragment: can't close html file!", Toast.LENGTH_SHORT).show();
                        ioException.printStackTrace();
                    }
                    e.printStackTrace();
                    Toast.makeText(getContext(), "File exception while creating html and canvas!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(this.notes.size() == 0)
            this.notes.add(null);

        adapter.notifyDataSetChanged();
    }

    private String boldSearchTerm(String text, String searchTerm)
    {
        String lowerCaseText = text.toLowerCase();
        String lowerCaseTerm = searchTerm.toLowerCase();

        int index = lowerCaseText.indexOf(lowerCaseTerm);

        StringBuilder builder = new StringBuilder();

        builder.append("..." + text.substring(Math.max(0, index - 20), index));
        builder.append("<b>" + searchTerm + "</b>");
        builder.append(
                text.substring(index + searchTerm.length(), Math.min(text.length(), index + 21 + searchTerm.length())) + "...");

        return builder.toString();
    }

    private void search()
    {
        notes.clear();

        String searchTerm = searchEditTxt.getText().toString();

        if(searchTerm.equals(""))
        {
            notes.add(null);
            adapter.notifyDataSetChanged();
            return;
        }

        if(searchMode == BY_CONTENT)
            searchByContent(searchTerm);
        else
            searchByTitle(searchTerm);
    }
}