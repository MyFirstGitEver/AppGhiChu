package com.example.appghichu.activities.fullscreen;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.Utils;
import com.example.appghichu.adapters.AnimatingFolderListAdapter;
import com.example.appghichu.dialogs.MoveFolderDialog;
import com.example.appghichu.dialogs.RenameFolderDialog;
import com.example.appghichu.interfaces.OnFolderManagerListener;
import com.example.appghichu.interfaces.SimpleCallBack;
import com.example.appghichu.objects.dtos.AnimatingFolderDTO;
import com.example.appghichu.objects.entities.FolderEntity;

import java.util.ArrayList;
import java.util.List;

public class FolderManagerFragment extends DialogFragment
{
    private RecyclerView folderList;
    private MotionLayout moreOptionsArea;
    private AppCompatButton fixBtn, moveBtn, deleteBtn, selectAllBtn;
    private ImageButton backBtn;

    private AnimatingFolderListAdapter adapter;

    private ArrayList<AnimatingFolderDTO> folders;

    private boolean isSelecting;

    private SimpleCallBack callback;

    public FolderManagerFragment()
    {
    }

    public FolderManagerFragment(SimpleCallBack callback)
    {
        this.callback = callback;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.folder_manager_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        List<FolderEntity> foundFolders =
                AppDatabase.getInstance(getContext()).folderInterface().listAllFoldersUnderManagement();

        folders = new ArrayList<>();
        for(FolderEntity folder : foundFolders)
            folders.add(new AnimatingFolderDTO(folder));

        folderList = view.findViewById(R.id.folderList);

        folderList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AnimatingFolderListAdapter(folders, new OnFolderManagerListener()
        {
            @Override
            public boolean isSelecting()
            {
                return isSelecting;
            }

            @Override
            public void onRenameFolder(int id, int index)
            {
                new RenameFolderDialog(id,
                        (folderName) ->
                        {
                            folders.get(index).folder.setFolderName(folderName);
                            adapter.notifyItemChanged(index);
                            callback.run();
                        })
                        .show(getActivity().getSupportFragmentManager(), "rename");
            }

            @Override
            public void onDeleteFolder(int id, int index)
            {
                Utils.showConfirmDialog("Bạn có muốn xóa thư mục này và tất cả ghi chú trong nó?", () ->
                {
                    AppDatabase.getInstance(getContext()).folderInterface().deleteFolderWithID(id);
                    folders.remove(index);
                    adapter.notifyItemRemoved(index);
                    callback.run();

                    isSelecting = false;
                    adapter.notifyDataSetChanged();
                }, getContext());
            }

            @Override
            public void onMoveFolder(int id, int index)
            {
                new MoveFolderDialog(folders,
                        (parentID) ->
                        {
                            AppDatabase.getInstance(getContext()).folderInterface().moveFolder(parentID, id);
                            Toast.makeText(getContext(), "Moved successfully", Toast.LENGTH_SHORT).show();

                            callback.run();

                            isSelecting = false;
                            adapter.notifyDataSetChanged();
                        }).show(getActivity().getSupportFragmentManager(), "move");
            }
        });
        folderList.setAdapter(adapter);

        fixBtn = view.findViewById(R.id.fixBtn);
        backBtn = view.findViewById(R.id.backBtn);
        moreOptionsArea = view.findViewById(R.id.moreOptionsArea);

        fixBtn.setOnClickListener((View v) ->
        {
            if(isSelecting)
                return;

            isSelecting = true;
            moreOptionsArea.transitionToEnd();
            adapter.notifyDataSetChanged();
        });

        backBtn.setOnClickListener((View v) -> dismiss());

        selectAllBtn = view.findViewById(R.id.selectAllBtn);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        moveBtn = view.findViewById(R.id.moveBtn);

        deleteBtn.setOnClickListener((View v) ->
        {
            ArrayList<Integer> ids = getAllCheckedIds();

            if(ids.size() != 0)
            {
                Utils.showConfirmDialog("Bạn có thật sự muốn xóa tất cả thư mục đã chọn?", () ->
                {
                    for(int i=folders.size() - 1;i>=0;i--)
                    {
                        AnimatingFolderDTO folder = folders.get(i);
                        if(folder.isChecked)
                        {
                            folders.remove(i);
                            adapter.notifyItemRemoved(i);
                        }
                    }

                    AppDatabase.getInstance(getContext()).folderInterface().deleteFolders(ids);

                    moreOptionsArea.transitionToStart();
                    isSelecting = false;
                    adapter.notifyDataSetChanged();
                    callback.run();
                }, getContext());
            }
        });

        selectAllBtn.setOnClickListener((View v) ->
        {
            for(int i=folders.size() - 1;i>=0;i--)
                folders.get(i).isChecked = true;

            adapter.notifyDataSetChanged();
        });

        moveBtn.setOnClickListener((View v) ->
        {
            ArrayList<Integer> ids = getAllCheckedIds();

            if(ids.size() != 0)
            {
                new MoveFolderDialog(folders, (parentID) ->
                {
                    for(int i=0;i<ids.size();i++)
                    {
                        if(ids.get(i) == parentID)
                        {
                            ids.remove(i);
                            break;
                        }
                    }

                    AppDatabase.getInstance(getContext()).folderInterface().moveFolders(ids, parentID);

                    moreOptionsArea.transitionToStart();
                    isSelecting = false;
                    adapter.notifyDataSetChanged();
                    callback.run();
                }).show(getActivity().getSupportFragmentManager(), "move folders");
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed()
            {
                if(isSelecting)
                {
                    isSelecting = false;
                    moreOptionsArea.transitionToStart();
                    adapter.notifyDataSetChanged();
                    return;
                }

                dismiss();
            }
        };
    }

    private ArrayList<Integer> getAllCheckedIds()
    {
        ArrayList<Integer> ids = new ArrayList<>();

        for(int i=folders.size() - 1;i>=0;i--)
        {
            AnimatingFolderDTO folder = folders.get(i);
            if(folder.isChecked)
                ids.add(folder.folder.getId());
        }

        return ids;
    }
}