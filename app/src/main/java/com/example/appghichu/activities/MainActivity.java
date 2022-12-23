package com.example.appghichu.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.Utils;
import com.example.appghichu.activities.fullscreen.FolderManagerFragment;
import com.example.appghichu.activities.fullscreen.SearchFragment;
import com.example.appghichu.activities.fullscreen.TagManagerFragment;
import com.example.appghichu.adapters.FolderListAdapter;
import com.example.appghichu.adapters.NoteListAdapter;
import com.example.appghichu.daos.FolderDAO;
import com.example.appghichu.daos.NoteDAO;
import com.example.appghichu.dialogs.AddFolderDialog;
import com.example.appghichu.dialogs.SortNoteDialog;
import com.example.appghichu.interfaces.DrawerListener;
import com.example.appghichu.interfaces.MainOptionListener;
import com.example.appghichu.interfaces.OnNoteClickListener;
import com.example.appghichu.models.MainViewModel;
import com.example.appghichu.objects.dtos.FolderDTO;
import com.example.appghichu.objects.entities.FolderEntity;
import com.example.appghichu.objects.entities.NoteEntity;
import com.google.android.material.navigation.NavigationView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout drawer;
    private NavigationView navView;
    private ImageButton openDrawerBtn, addBtn, mainOptionsBtn, searchBtn;
    private RecyclerView noteList, folderList;
    private TextView folderTxt, navViewFolderTxt;

    private int noteCounter = 1, currentFolderID = 0;

    private MainViewModel model;
    private NoteListAdapter noteListAdapter;
    private FolderListAdapter folderListAdapter;

    private final ActivityResultLauncher<Intent> mainLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            {
                if(result.getData() == null)
                    Toast.makeText(this, "Failed to edit/create your note. We're sorry :(", Toast.LENGTH_SHORT).show();

                int code = result.getResultCode();
                if(code == Utils.DISCARD_NOTE)
                {
                    int action = result.getData().getIntExtra("action", 0);

                    if(action == Utils.OPEN_FOR_ADD_NEW)
                        return;

                    int id = result.getData().getIntExtra("id", 0);
                    int index = result.getData().getIntExtra("index", 0);
                    AppDatabase.getInstance(this).noteInterface().move(id, -1);
                    model.notes.remove(index);
                    noteListAdapter.notifyItemRemoved(index);

                    return;
                }

                if(code == Utils.OPEN_FOR_ADD_NEW || code == Utils.OPEN_FOR_EDIT)
                {
                    NoteEntity note = result.getData().getParcelableExtra("note");

                    if(note == null)
                    {
                        Toast.makeText(this, "Your note can't be saved!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(code == Utils.OPEN_FOR_ADD_NEW)
                    {
                        if(model.notes.size() == 1 && model.notes.get(0) == null)
                        {
                            model.notes.remove(0); // remove empty message!
                            noteListAdapter.notifyItemRemoved(0);
                        }

                        model.notes.add(note);
                        noteListAdapter.notifyItemInserted(model.notes.size() - 1);
                        note.setFolderID(currentFolderID);
                        noteCounter++;
                    }
                    else
                    {
                        int index = result.getData().getIntExtra("index", 0);
                        model.notes.set(index, note);
                        noteListAdapter.notifyItemChanged(index);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            {
                model.editorIntent.setValue(result.getData());
            });

    //Callbacks
    private final OnNoteClickListener searchNoteClickListener = (note, index) ->
    {
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("noteID", note.getId());
        intent.putExtra("action", Utils.OPEN_FOR_EDIT);
        intent.putExtra("index", index);
        intent.putExtra("folderID", note.getFolderID());

        searchLauncher.launch(intent);
    };

    private final OnNoteClickListener mainNoteClickListener = (note, index) ->
    {
        if(currentFolderID == -1)
        {
            Utils.showConfirmDialog("Bạn có muốn vĩnh viễn xóa ghi chú này không ?", (() ->
            {
                File f = new File(getFilesDir(), "Note " + note.getId());

                try
                {
                    FileUtils.deleteDirectory(f);
                    AppDatabase.getInstance(this).noteInterface().deleteNote(note);
                }
                catch (IOException e)
                {
                    Toast.makeText(this,
                            "We can't delete this not for some reason!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                model.notes.remove(index);
                noteListAdapter.notifyItemRemoved(index);
            }),  this);
            return;
        }

        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("noteID", note.getId());
        intent.putExtra("action", Utils.OPEN_FOR_EDIT);
        intent.putExtra("index", index);
        intent.putExtra("folderID", currentFolderID);

        mainLauncher.launch(intent);
    };

    private DrawerListener drawerListener = new DrawerListener()
    {
        @Override
        public void onMoveToFolder(int folderID)
        {
            if(currentFolderID == folderID)
                return;

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("current", folderID);

            startActivity(intent);
            finish();
        }

        @Override
        public void onMoveToMain()
        {
            if(currentFolderID == 0)
                return;

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("current", 0);

            startActivity(intent);
            finish();
        }

        @Override
        public void onMoveToTrashBin()
        {
            if(currentFolderID == -1)
                return;

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("current", -1);

            startActivity(intent);
            finish();
        }

        @Override
        public void onMoveToTagManager()
        {
            new TagManagerFragment(searchNoteClickListener, () -> initFolderList())
                    .show(getSupportFragmentManager(), "tag manager");
        }

        @Override
        public void onMoveToFolderManager()
        {
            new FolderManagerFragment(() -> initFolderList()).show(getSupportFragmentManager(), "folder manager");
        }
    };

    private final MainOptionListener mainOptionListener = new MainOptionListener()
    {
        @Override
        public void onCreateNewFolder(String folderName)
        {
            FolderDAO folderInterface = AppDatabase.getInstance(MainActivity.this).folderInterface();

            if(folderInterface.checkIfFolderAlreadyExists(folderName) == 1)
                Toast.makeText(MainActivity.this, "This folder name already exists", Toast.LENGTH_SHORT).show();
            else
                folderInterface.insertToParentFolder(currentFolderID, folderName);

            List<FolderEntity> folders = folderInterface.listFoldersUsingParentID(0);

            List<FolderDTO> newStructure = new ArrayList<>();

            for(FolderEntity folder : folders)
                newStructure.add(new FolderDTO(folder));

            folderListAdapter.setNewFolderStructure(newStructure);// re-display the folder structure
        }

        @Override
        public void onSort(boolean byDate, boolean increasing)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                model.notes.sort(new Comparator<NoteEntity>()
                {
                    @Override
                    public int compare(NoteEntity n1, NoteEntity n2)
                    {
                        int result;
                        if(byDate)
                            result = n1.getCreationDate().compareTo(n2.getCreationDate());
                        else
                            result = n1.getTitle().compareTo(n2.getTitle());

                        if(!increasing)
                            return -result;

                        return result;
                    }
                });

                noteListAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentFolderID = getIntent().getIntExtra("current", 0);

        model = new ViewModelProvider(this).get(MainViewModel.class);

        folderTxt = findViewById(R.id.folderTxt);
        drawer = findViewById(R.id.drawer);
        noteList = findViewById(R.id.noteList);
        openDrawerBtn = findViewById(R.id.openDrawerBtn);
        addBtn = findViewById(R.id.addBtn);
        searchBtn = findViewById(R.id.searchBtn);
        mainOptionsBtn = findViewById(R.id.mainOptionsBtn);
        openDrawerBtn.setOnClickListener((View v) -> drawer.openDrawer(Gravity.LEFT));

        navView = findViewById(R.id.nav_view);
        navViewFolderTxt = navView.findViewById(R.id.folderTxt);
        folderList = navView.findViewById(R.id.folderList);

        getCurrentFolder();
        initDrawerOptionsHandler();
        initNoteList();
        initFolderList();

        addBtn.setOnClickListener((View v) ->
        {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            intent.putExtra("noteID", noteCounter);
            intent.putExtra("action", Utils.OPEN_FOR_ADD_NEW);
            intent.putExtra("folderID", currentFolderID);

            mainLauncher.launch(intent);
        });

        mainOptionsBtn.setOnClickListener((View v) ->
        {
            PopupMenu pm = new PopupMenu(this, v);
            pm.getMenuInflater().inflate(R.menu.main_menu, pm.getMenu());
            pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem)
                {
                    if(menuItem.getTitle().equals("Tạo thư mục"))
                        new AddFolderDialog(mainOptionListener).show(getSupportFragmentManager(), "add");
                    else if(menuItem.getTitle().equals("Sắp xếp"))
                        new SortNoteDialog(mainOptionListener).show(getSupportFragmentManager(), "sort");

                    return false;
                }
            });
            pm.show();
        });

        searchBtn.setOnClickListener((View v) ->
            new SearchFragment(searchNoteClickListener, () -> initNoteList()).show(getSupportFragmentManager(), "search"));
    }

    private void initFolderList()
    {
        List<FolderEntity> folders = AppDatabase.getInstance(this).folderInterface().listFoldersUsingParentID(0);

        ArrayList<FolderDTO> dtos = new ArrayList<>();

        for(FolderEntity entity : folders)
            dtos.add(new FolderDTO(entity));

        folderList.setLayoutManager(new LinearLayoutManager(this));
        folderListAdapter = new FolderListAdapter(dtos, this, drawerListener);
        folderList.setAdapter(folderListAdapter);
    }

    private void getCurrentFolder()
    {
        FolderDAO folderInterface = AppDatabase.getInstance(this).folderInterface();
        NoteDAO noteInterface = AppDatabase.getInstance(this).noteInterface();

        if(currentFolderID == 0 && folderInterface.checkIfFolderExistsUsingID(0) == 0)
            folderInterface.initMainFolder();

        if(folderInterface.checkIfFolderExistsUsingID(-1) == 0)
            folderInterface.initTrashFolder();

        FolderEntity folder = folderInterface.findFolderByID(currentFolderID);
        folderTxt.setText(folder.getFolderName());

        int folderCount = folderInterface.countFoldersInsideFolder(currentFolderID);

        if(currentFolderID == 0)
            folderCount--;

        navViewFolderTxt.setText(folder.getFolderName() + "\n" + "Tổng cộng " +
                noteInterface.countNotesInsideFolder(currentFolderID) + " ghi chú và " +
                folderCount + " thư mục");
    }

    private void initNoteList()
    {
        model.notes = (ArrayList<NoteEntity>) AppDatabase.getInstance(this).noteInterface().getAllNotesInFolder(currentFolderID);

        if(model.notes.size() == 0)
            model.notes.add(null);

        noteCounter = AppDatabase.getInstance(this).noteInterface().getCurrentNoteID() + 1;

        noteListAdapter = new NoteListAdapter(model.notes, mainNoteClickListener, this, "Hiện chưa có ghi chú nào");

        noteList.setAdapter(noteListAdapter);
        noteList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initDrawerOptionsHandler()
    {
        AppCompatButton allNoteBtn = navView.findViewById(R.id.allNoteBtn);
        AppCompatButton trashBtn = navView.findViewById(R.id.trashBtn);
        AppCompatButton tagBtn = navView.findViewById(R.id.tagBtn);
        AppCompatButton folderManagerBtn = navView.findViewById(R.id.folderManagerBtn);

        allNoteBtn.setOnClickListener((View v) -> drawerListener.onMoveToMain());

        trashBtn.setOnClickListener((View v) -> drawerListener.onMoveToTrashBin());

        tagBtn.setOnClickListener((View v) -> drawerListener.onMoveToTagManager());

        folderManagerBtn.setOnClickListener((View v) -> drawerListener.onMoveToFolderManager());
    }
}