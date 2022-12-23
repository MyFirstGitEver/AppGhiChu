package com.example.appghichu.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appghichu.R;
import com.example.appghichu.Utils;
import com.example.appghichu.interfaces.NotePageListener;
import com.example.appghichu.objects.dtos.NotePageDTO;
import com.example.appghichu.objects.MyCanvas;
import com.example.appghichu.objects.PaintStep;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

public class NotePageListAdapter extends RecyclerView.Adapter<NotePageListAdapter.NotePageViewHolder>
{
    private static final int BOLD_REQUEST = 0;
    private static final int ITALIC_REQUEST = 1;
    private static final int FONT_SIZE_REQUEST = 2;
    public static final int INDEXING = 3;

    private Context context;

    private ArrayList<NotePageDTO> pages;
    private NotePageListener notePageListener;
    private int currentPageIndex = 0, constraintedWidth;

    public NotePageListAdapter(ArrayList<NotePageDTO> pages,
                               NotePageListener notePageListener,
                               Context context,
                               int constraintedWidth)
    {
        this.pages = pages;
        this.notePageListener = notePageListener;
        this.context = context;
        this.constraintedWidth = constraintedWidth;
    }

    @NonNull
    @Override
    public NotePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new NotePageViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.a_note_page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotePageViewHolder holder, int position, @NonNull List<Object> payloads)
    {
        if(payloads.size() == 0)
            super.onBindViewHolder(holder, position, payloads);

        for(Object payload : payloads)
        {
            if(payload instanceof Pair)
            {
                Pair<Integer, Integer> pair = (Pair<Integer, Integer>) payload;
                holder.richEditor.setEditorFontSize(pair.second);

                return;
            }

            Integer code = (Integer) payload;
            if(code == BOLD_REQUEST)
                holder.richEditor.setBold();
            else if(code == ITALIC_REQUEST)
                holder.richEditor.setItalic();
            else if(code == INDEXING)
                holder.indexTxt.setText((position + 1) + "/" + pages.size());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull NotePageViewHolder holder, int position)
    {
        NotePageDTO page = pages.get(position);

        notePageListener.notifyCanvasSize(holder.canvas.getWidth(), holder.canvas.getHeight());

        MyCanvas canvas = holder.canvas;
        RichEditor richEditor = holder.richEditor;

        richEditor.setEditorFontSize(18);
        richEditor.setHtml(page.getHtml());
        richEditor.setEditorBackgroundColor(Color.TRANSPARENT);
        richEditor.setBackgroundColor(Color.TRANSPARENT);

        canvas.setPreviousBitmap(page.getPreviousBitmap());
        canvas.initSteps(page.getSteps());
        canvas.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        canvas.redraw();

        holder.indexTxt.setText((position + 1) + "/" + pages.size());

        richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener()
        {
            @Override
            public void onTextChange(String text)
            {
                if(notePageListener.checkLineLimit(text))
                {
                    richEditor.setHtml(page.getHtml());
                    return;
                }

                page.setHtml(text);
            }
        });

        richEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                currentPageIndex = holder.getBindingAdapterPosition();
                notePageListener.onPageFocus(currentPageIndex);
            }
        });

        richEditor.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return false;
            }
        });

        canvas.setNotePageListener(notePageListener);
        canvas.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if(!notePageListener.isUsingPen())
                {
                    if(motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                        canvas.draw(motionEvent.getX(), motionEvent.getY());
                    else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        currentPageIndex = holder.getBindingAdapterPosition();
                        canvas.beginPath(motionEvent.getX(), motionEvent.getY());
                        notePageListener.onPageFocus(currentPageIndex);
                    }
                }

                return(!notePageListener.isUsingPen());
            }
        });

        holder.pageContainer.setLayoutParams(new LinearLayout.LayoutParams(
                constraintedWidth, Utils.dpToPx(400, context)));
    }

    @Override
    public int getItemCount()
    {
        return pages.size();
    }

    public void alterCurrentPageBold()
    {
        notifyItemChanged(currentPageIndex, BOLD_REQUEST);
    }

    public void alterCurrentPageItalic()
    {
        notifyItemChanged(currentPageIndex, ITALIC_REQUEST);
    }

    public void revertCurrentPageDraw()
    {
        ArrayList<PaintStep> steps = pages.get(currentPageIndex).getSteps();

        if(steps.size() != 0)
            steps.remove(steps.size() - 1);

        notifyItemChanged(currentPageIndex);
    }

    public class NotePageViewHolder extends RecyclerView.ViewHolder
    {
        private RichEditor richEditor;
        private MyCanvas canvas;
        private TextView indexTxt;
        private ConstraintLayout pageContainer;

        public NotePageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            richEditor = itemView.findViewById(R.id.richEditor);
            canvas = itemView.findViewById(R.id.canvas);
            indexTxt = itemView.findViewById(R.id.indexTxt);
            pageContainer = itemView.findViewById(R.id.pageContainer);
        }
    }
}
