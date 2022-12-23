package com.example.appghichu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.R;
import com.example.appghichu.interfaces.OnTagClickListener;
import com.example.appghichu.interfaces.SimpleCallBack;
import com.example.appghichu.objects.entities.TagEntity;
import java.util.List;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.TagViewHolder>
{
    private List<TagEntity> tags;
    private Context context;
    private boolean showDeleteBtn, simplyRemove;
    private SimpleCallBack callback;

    public TagListAdapter(List<TagEntity> tags, Context context, boolean showDeleteBtn, boolean simplyRemove)
    {
        this.tags = tags;
        this.context = context;
        this.showDeleteBtn = showDeleteBtn;
        this.simplyRemove = simplyRemove;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new TagViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.a_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position)
    {
        holder.tagNameTxt.setText('#' + tags.get(position).getTagName());

        if(!showDeleteBtn)
        {
            holder.removeBtn.setVisibility(View.GONE);
            return;
        }

        holder.removeBtn.setOnClickListener((View v) ->
        {
            int pos = holder.getBindingAdapterPosition();
            int id = tags.get(pos).getId();

            tags.remove(pos);
            notifyItemRemoved(pos);

            if(callback != null)
                callback.run();

            if(simplyRemove)
                return;

            AppDatabase.getInstance(context).tagInterface().deleteTagById(id);
        });
    }

    @Override
    public int getItemCount()
    {
        return tags.size();
    }

    public void notifyWhenTagsRemoved(SimpleCallBack callBack)
    {
        this.callback = callBack;
    }

    public class TagViewHolder extends RecyclerView.ViewHolder
    {
        TextView tagNameTxt;
        ImageButton removeBtn;

        public TagViewHolder(@NonNull View itemView)
        {
            super(itemView);

            tagNameTxt = itemView.findViewById(R.id.tagNameTxt);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }
}
