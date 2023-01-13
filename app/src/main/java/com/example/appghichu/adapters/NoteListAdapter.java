package com.example.appghichu.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.Utils;
import com.example.appghichu.interfaces.OnNoteClickListener;
import com.example.appghichu.objects.entities.NoteEntity;
import com.example.appghichu.objects.entities.TagEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<NoteEntity> notes;
    private OnNoteClickListener onNoteClickListener;
    private Context context;
    private String emptyMsg;

    public NoteListAdapter(List<NoteEntity> notes, OnNoteClickListener onNoteClickListener, Context context, String emptyMsg)
    {
        this.notes = notes;
        this.onNoteClickListener = onNoteClickListener;
        this.context = context;
        this.emptyMsg = emptyMsg;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == NoteEntity.EMPTY)
            return
                    new EmptyViewHolder(LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.empty_message, parent, false));

        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.a_note, parent, false));
    }

    @Override
    public int getItemViewType(int position)
    {
        if(notes.get(position) == null)
            return NoteEntity.EMPTY;

        return NoteEntity.REAL_NOTE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof EmptyViewHolder)
        {
            ((EmptyViewHolder) holder).msgTxt.setText(emptyMsg);
            return;
        }

        NoteEntity note = notes.get(position);

        NoteViewHolder Holder = (NoteViewHolder) holder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Holder.titleTxt.setText(Html.fromHtml(note.getTitle(), Html.FROM_HTML_MODE_COMPACT));
        else
            Holder.titleTxt.setText(note.getTitle());

        Holder.dateTxt.setText(Utils.convertToReadable(note.getCreationDate()));
        Holder.previewImg.setImageBitmap(BitmapFactory.decodeFile(note.getPreviewPath()));

        List<TagEntity> tags =
                AppDatabase.getInstance(context).tagInterface().getAllTagsUsingNoteID(note.getId());

        Holder.container.setOnClickListener((View v) -> onNoteClickListener.onNoteClick(note, holder.getBindingAdapterPosition()));
        Holder.tagList.setAdapter(new TagListAdapter(tags, context, false, false));
        Holder.tagList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public int getItemCount()
    {
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView dateTxt, titleTxt;
        private ImageView previewImg;
        private ConstraintLayout container;
        private RecyclerView tagList;

        public NoteViewHolder(@NonNull View itemView)
        {
            super(itemView);

            dateTxt = itemView.findViewById(R.id.dateTxt);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            previewImg = itemView.findViewById(R.id.previewImg);
            container = itemView.findViewById(R.id.Container);
            tagList = itemView.findViewById(R.id.tagList);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder
    {
        TextView msgTxt;
        public EmptyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            msgTxt = itemView.findViewById(R.id.msgTxt);
        }
    }

    public void replaceList(List<NoteEntity> notes)
    {
        this.notes = notes;
        notifyDataSetChanged();
    }
}