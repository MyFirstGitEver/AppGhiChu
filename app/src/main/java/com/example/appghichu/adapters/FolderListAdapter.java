package com.example.appghichu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.interfaces.DrawerListener;
import com.example.appghichu.objects.dtos.FolderDTO;
import com.example.appghichu.objects.entities.FolderEntity;

import java.util.ArrayList;
import java.util.List;

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderViewHolder>
{
    private ArrayList<FolderDTO> folders;
    private Context context;
    private DrawerListener listener;

    public FolderListAdapter(ArrayList<FolderDTO> folders,
                             Context context,
                             DrawerListener listener)
    {
        this.folders = folders;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.a_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position)
    {
        FolderDTO folder = folders.get(position);
        holder.nameTxt.setText(folder.folder.getFolderName());
        holder.container.setPadding(folder.marginLeft, 0, 0, 0);
        holder.container.setOnClickListener((View v) ->
        {
            folder.isExpanded = !folder.isExpanded;
            int pos = holder.getBindingAdapterPosition();
            int marginLeft = folders.get(pos).marginLeft;

            if(!folder.isExpanded)
            {
                int lastChildLevelPos = pos;
                for(int i=pos+1;i<folders.size();i++)
                {
                    FolderDTO belowFolder = folders.get(i);
                    if(belowFolder.marginLeft == marginLeft)
                        break;

                    lastChildLevelPos = i;
                }

                for(int i=lastChildLevelPos;i>pos;i--)
                {
                    folders.remove(pos + 1);
                    notifyItemRemoved(pos + 1);
                }

                return;
            }

            List<FolderEntity> subFolders =
                    AppDatabase.getInstance(context).folderInterface().listFoldersUsingParentID(folder.folder.getId());

            for(int i=subFolders.size() - 1;i>=0;i--)
            {
                FolderEntity subFolder = subFolders.get(i);
                FolderDTO dto = new FolderDTO(subFolder);

                dto.marginLeft = folder.marginLeft + 30;

                folders.add(pos + 1, dto);
                notifyItemInserted(pos + 1);
            }
        });
        holder.container.setOnLongClickListener((v) ->
        {
            listener.onMoveToFolder(folder.folder.getId());
            return true;
        });
    }

    @Override
    public int getItemCount()
    {
        return folders.size();
    }

    public void setNewFolderStructure(List<FolderDTO> folders)
    {
        this.folders = (ArrayList<FolderDTO>) folders;
        notifyDataSetChanged();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameTxt;
        ConstraintLayout container;

        public FolderViewHolder(@NonNull View itemView)
        {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.nameTxt);
            container = itemView.findViewById(R.id.container);
        }
    }
}
