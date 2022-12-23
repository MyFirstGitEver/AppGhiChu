package com.example.appghichu.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appghichu.R;
import com.example.appghichu.Utils;
import com.example.appghichu.daos.FolderDAO;
import com.example.appghichu.interfaces.OnFolderManagerListener;
import com.example.appghichu.objects.dtos.AnimatingFolderDTO;
import com.example.appghichu.objects.entities.FolderEntity;

import java.util.List;

public class AnimatingFolderListAdapter extends RecyclerView.Adapter<AnimatingFolderListAdapter.AnimatingFolderViewHolder>
{
    private List<AnimatingFolderDTO> folders;
    private OnFolderManagerListener listener;

    public AnimatingFolderListAdapter(List<AnimatingFolderDTO> folders, OnFolderManagerListener listener)
    {
        this.folders = folders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnimatingFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new AnimatingFolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.an_animating_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AnimatingFolderViewHolder holder, int position)
    {
        AnimatingFolderDTO animatingFolder = folders.get(position);

        if(listener.isSelecting())
            holder.takeActionListener.transitionToEnd();
        else
            holder.takeActionListener.transitionToStart();

        holder.deleteCBox.setChecked(animatingFolder.isChecked);
        holder.folderTxt.setText(animatingFolder.folder.getFolderName());
        holder.takeActionListener.setOnClickListener((View v) ->
        {
            if(listener.isSelecting())
            {
                holder.deleteCBox.setChecked(!holder.deleteCBox.isChecked());
                return;
            }

            PopupMenu pm = new PopupMenu(v.getContext(), holder.menuAnchor);
            pm.getMenuInflater().inflate(R.menu.folder_menu, pm.getMenu());
            pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem)
                {
                    int pos = holder.getBindingAdapterPosition();
                    AnimatingFolderDTO folder = folders.get(pos);

                    if(menuItem.getTitle().equals("Đổi tên thư mục"))
                    {
                        listener.onRenameFolder(
                                folder.folder.getId(),
                                pos);
                    }
                    else if(menuItem.getTitle().equals("Xóa thư mục"))
                        listener.onDeleteFolder(folder.folder.getId(), pos);
                    else
                        listener.onMoveFolder(folder.folder.getId(), pos);

                    return true;
                }
            });
            pm.show();
        });

        holder.deleteCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                int pos = holder.getBindingAdapterPosition();
                folders.get(pos).isChecked = checked;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return folders.size();
    }

    public void reDisplayFolderList(List<FolderEntity> newList)
    {
        folders.clear();
        for(FolderEntity folder : newList)
            folders.add(new AnimatingFolderDTO(folder));

        notifyDataSetChanged();
    }

    public class AnimatingFolderViewHolder extends RecyclerView.ViewHolder
    {
        TextView folderTxt;
        CheckBox deleteCBox;
        MotionLayout takeActionListener;
        ImageView menuAnchor;

        public AnimatingFolderViewHolder(@NonNull View itemView)
        {
            super(itemView);

            folderTxt = itemView.findViewById(R.id.nameTxt);
            deleteCBox = itemView.findViewById(R.id.deleteCBox);
            takeActionListener = itemView.findViewById(R.id.takeActionListener);
            menuAnchor = itemView.findViewById(R.id.menuAnchor);
        }
    }
}
