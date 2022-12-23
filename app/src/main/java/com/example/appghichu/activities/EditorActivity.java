package com.example.appghichu.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.Utils;
import com.example.appghichu.adapters.NotePageListAdapter;
import com.example.appghichu.dialogs.TagDialog;
import com.example.appghichu.interfaces.NotePageListener;
import com.example.appghichu.interfaces.SimpleCallBack;
import com.example.appghichu.models.EditorViewModel;
import com.example.appghichu.objects.PaintStep;
import com.example.appghichu.objects.entities.NoteEntity;
import com.example.appghichu.objects.dtos.NotePageDTO;
import com.example.appghichu.objects.MyCanvas;
import com.example.appghichu.R;
import com.example.appghichu.objects.entities.TagEntity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditorActivity extends AppCompatActivity
{
    private ImageButton
            boldBtn, italicBtn, penBtn, eraseBtn, drawerBtn, colorBtn, moreBtn, revertBtn, backBtn, lastBtn;
    private LinearLayout drawerOptions;
    private MotionLayout toolArea;
    private EditText titleEditTxt, limitEditTxt;
    private RecyclerView notePageList;

    private PopupWindow window;
    private NotePageListAdapter notePageListAdapter;
    private EditorViewModel model;

    private boolean isUsingDrawer, isUsingTool = true;
    private int mode = MyCanvas.DRAW, currentColor = Color.RED, canvasWidth, canvasHeight, noteID;
    private File noteFolder;

    private List<TagEntity> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        noteID = getIntent().getIntExtra("noteID", 0);

        model = new ViewModelProvider(this).get(EditorViewModel.class);
        noteFolder = new File(getFilesDir(), "Note " + noteID);
        noteFolder.mkdir();

        boldBtn = findViewById(R.id.boldBtn);
        italicBtn = findViewById(R.id.italicBtn);
        penBtn = findViewById(R.id.penBtn);
        eraseBtn = findViewById(R.id.eraseBtn);
        drawerBtn = findViewById(R.id.drawerBtn);
        colorBtn = findViewById(R.id.colorBtn);
        drawerOptions = findViewById(R.id.drawerOptions);
        toolArea = findViewById(R.id.toolArea);
        moreBtn = findViewById(R.id.moreBtn);
        revertBtn = findViewById(R.id.revertBtn);
        backBtn = findViewById(R.id.backBtn);
        limitEditTxt = findViewById(R.id.limitEditTxt);

        titleEditTxt = findViewById(R.id.titleEditTxt);

        notePageList = findViewById(R.id.notePageList);
        
        prepare();
        initNotPageList();

        boldBtn.setOnClickListener((View v) -> notePageListAdapter.alterCurrentPageBold());
        italicBtn.setOnClickListener((View v) -> notePageListAdapter.alterCurrentPageItalic());
        
        colorBtn.setOnClickListener((View v) ->
        {
            View fragment = popUpFragmentInit();
            window = new PopupWindow(fragment,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setOutsideTouchable(true);
            window.showAtLocation(drawerOptions, Gravity.BOTTOM | Gravity.LEFT, 0, 260);
        });
        
        penBtn.setOnClickListener((View v) ->
        {
            toggle(penBtn);

            if(isUsingDrawer)
            {
                toolArea.transitionToStart();
                isUsingDrawer = false;
            }
        });
        
        eraseBtn.setOnClickListener((View v) ->
        {
            mode = MyCanvas.ERASE;
            toggle(eraseBtn);
            if(!isUsingDrawer)
            {
                toolArea.transitionToEnd();
                isUsingDrawer = true;
            }
        });
        
        drawerBtn.setOnClickListener((View v) ->
        {
            mode = MyCanvas.DRAW;
            toggle(drawerBtn);
            if(!isUsingDrawer)
            {
                toolArea.transitionToEnd();
                isUsingDrawer = true;
            }
        });
        
        moreBtn.setOnClickListener((View v) ->
        {
            PopupMenu menu = new PopupMenu(this, moreBtn);
            menu.getMenuInflater().inflate(R.menu.note_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem)
                {
                    if(menuItem.getTitle().equals("Xóa"))
                        showDiscardDialog(getIntent().getIntExtra("noteID", 0));
                    else if(menuItem.getTitle().equals("Tạo tag"))
                    {
                        if(tags == null)
                            tags = AppDatabase.getInstance(EditorActivity.this).tagInterface().getTagsFromNoteID(noteID);

                        new TagDialog(noteID, tags).show(getSupportFragmentManager(), "tag");
                    }

                    return true;
                }
            });

            menu.show();
        });
        
        revertBtn.setOnClickListener((View v) -> notePageListAdapter.revertCurrentPageDraw());
        
        backBtn.setOnClickListener((View v) ->
            Utils.showConfirmDialog("Bạn có muốn lưu ghi chú này", () ->
            {
                int noteID = getIntent().getIntExtra("noteID", 0);
                NoteEntity note = null;
                Bitmap bm = Bitmap.createBitmap(
                        notePageList.getWidth(), notePageList.getHeight(), Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bm);
                notePageList.draw(canvas);

                FileOutputStream fOut = null;
                try
                {
                    File f = new File(noteFolder,  noteID + "");
                    f.createNewFile();

                    fOut = new FileOutputStream(f);

                    bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);

                    note = new NoteEntity(
                            noteID,
                            f.getAbsolutePath(),
                            titleEditTxt.getText().toString(),
                            new Date(),
                            model.pages.size() - 1,
                            getIntent().getIntExtra("folderID", 0));

                    fOut.close();
                }
                catch (IOException e)
                {
                    if(fOut != null)
                    {
                        try
                        {
                            fOut.close();
                        }
                        catch (IOException ioException)
                        {
                            ioException.printStackTrace();
                        }
                    }

                    Toast.makeText(this, "File exception while saving note preview", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                try
                {
                    saveNotePagesContent(note);
                }
                catch (IOException e)
                {
                    Toast.makeText(this, "File exception while saving note content", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                intent.putExtra("note", note);
                int action = getIntent().getIntExtra("action", 0);
                if(action == Utils.OPEN_FOR_EDIT)
                {
                    intent.putExtra("index", getIntent().getIntExtra("index", 0));
                    AppDatabase.getInstance(this).noteInterface().updateNoteContent(note);
                }
                else
                    AppDatabase.getInstance(this).noteInterface().insertNewNote(note);

                saveTags();

                setResult(action, intent);
                finish();
            }, this));
        
        penBtn.setBackground(getResources().getDrawable(R.drawable.round_bg));
        lastBtn = penBtn;
    }

    private void saveTags()
    {
        if(tags == null)
            return;

        for(TagEntity tag : tags)
        {
            int count =
                    AppDatabase.getInstance(this).tagInterface().checkIfThisTagExist(noteID, tag.getTagName());

            if(count == 1)
                continue;

            AppDatabase.getInstance(this).tagInterface().insertNewTag(noteID, tag.getTagName());
        }
    }

    @Override
    public void onBackPressed()
    {
        if(isUsingTool)
        {
            isUsingTool = false;
            toolArea.setVisibility(View.GONE);
            return;
        }

        showDiscardDialog(getIntent().getIntExtra("noteID", 0));
    }

    private void showDiscardDialog(int noteID)
    {
        Utils.showConfirmDialog( "Bạn có muốn hủy ghi chú này?",() ->
        {
            Intent intent = new Intent();
            intent.putExtra("index", getIntent().getIntExtra("index", 0));
            intent.putExtra("action", getIntent().getIntExtra("action", 0));
            intent.putExtra("id", noteID);
            setResult(Utils.DISCARD_NOTE, intent);
            finish();
        }, this);
    }

    private void saveNotePagesContent(NoteEntity newNote) throws IOException
    {
        for(int i=0;i<model.pages.size() - 1;i++)
        {
            NotePageDTO page = model.pages.get(i);

            File assetFolder = new File(noteFolder, newNote.getId() + "." + i);
            assetFolder.mkdir();

            File htmlFile = new File(assetFolder, "html");

            FileOutputStream fOut = null;
            try
            {
                htmlFile.createNewFile();

                fOut = new FileOutputStream(htmlFile);
                fOut.write(page.getHtml().getBytes(StandardCharsets.UTF_8));
                fOut.close();
            }
            catch (IOException e)
            {
                fOut.close();
                e.printStackTrace();
                Toast.makeText(this, "File exception while creating html and canvas!", Toast.LENGTH_SHORT).show();
            }

            Paint paint  = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);

            Paint clearPaint = new Paint();
            clearPaint.setStyle(Paint.Style.STROKE);
            clearPaint.setStrokeWidth(30);
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            clearPaint.setAntiAlias(true);

            Rect rect = new Rect();
            rect.set(0, 0, canvasWidth, canvasHeight);
            Bitmap bm = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bm);

            if(page.getPreviousBitmap() != null)
                canvas.drawBitmap(page.getPreviousBitmap(), null, rect, null);

            Utils.drawSteps(page.getSteps(), paint, clearPaint, canvas);

            fOut = new FileOutputStream(new File(assetFolder, "canvas"));
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.close();
        }
    }

    private int getVerticalWidth()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    private void initNotPageList()
    {
        notePageListAdapter = new NotePageListAdapter(model.pages,
                new NotePageListener()
                {
                    @Override
                    public void notifyCanvasSize(int width, int height)
                    {
                        canvasWidth = width;
                        canvasHeight = height;
                    }

                    @Override
                    public void onPageFocus(int currentPageIndex)
                    {
                        toolArea.setVisibility(View.VISIBLE);
                        isUsingTool = true;

                        if(currentPageIndex == model.pages.size() - 1)
                        {
                            model.pages.add(new NotePageDTO(null, "", 20));
                            notePageListAdapter.notifyItemInserted(model.pages.size() - 1);

                            for(int i=0;i<model.pages.size();i++)
                                notePageListAdapter.notifyItemChanged(i, NotePageListAdapter.INDEXING); // re-index!
                        }
                    }

                    @Override
                    public boolean isUsingPen()
                    {
                        return lastBtn == penBtn;
                    }

                    @Override
                    public int getMode()
                    {
                        return mode;
                    }

                    @Override
                    public int getColor()
                    {
                        return currentColor;
                    }

                    @Override
                    public boolean checkLineLimit(String html)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        {
                            limitEditTxt.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
                            int count = limitEditTxt.getLineCount();

                            int lines = (int)((double)
                                    Utils.dpToPx(400, EditorActivity.this) / limitEditTxt.getLineHeight());

                            Log.d("max", lines + "");
                            Log.d("lineCount", count + "");
                            return (lines <= count);
                        }

                        return false;
                    }


                }, this, getVerticalWidth());

        LinearLayoutManager manager = new LinearLayoutManager(this)
        {
            @Override
            public boolean canScrollVertically()
            {
                return lastBtn == penBtn;
            }
        };

        notePageList.setAdapter(notePageListAdapter);
        notePageList.setLayoutManager(manager);
    }

    private void prepare()
    {
        NoteEntity note = getIntent().getParcelableExtra("note");

        if(note == null)
        {
            if(model.pages.size() == 0)
                model.pages.add(new NotePageDTO(null, "", 20));

            return;
        }

        File noteFolder = new File(getFilesDir(), "Note " + note.getId());
        titleEditTxt.setText(note.getTitle());

        for(int i=0;i<note.getNumberOfPages();i++)
        {
            File folder = new File(noteFolder, note.getId() + "." + i);

            FileInputStream fIn  = null;
            try
            {
                File htmlFile = new File(folder, "html");
                fIn = new FileInputStream(htmlFile);

                byte[] htmlBytes = new byte[(int) htmlFile.length()];
                fIn.read(htmlBytes);

                String html = new String(htmlBytes);
                Bitmap bm = BitmapFactory.decodeFile(new File(folder, "canvas").getAbsolutePath());

                int fontSize = 20;
                for(File f : folder.listFiles())
                {
                    if(f.getName().equals("html") && f.getName().equals("canvas"))
                    {
                        fontSize = Integer.parseInt(f.getName());
                        break;
                    }
                }

                model.pages.add(new NotePageDTO(bm, html, fontSize));
                fIn.close();
            }
            catch (IOException e)
            {
                try
                {
                    if(fIn != null)
                        fIn.close();
                }
                catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
                Toast.makeText(this, "editor prepare: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        model.pages.add(new NotePageDTO(null, "", 20)); // dummy page
    }

    private void toggle(ImageButton btn)
    {
        if(lastBtn != btn)
            lastBtn.setBackground(new ColorDrawable(Color.TRANSPARENT));

        lastBtn = btn;
        btn.setBackground(getResources().getDrawable(R.drawable.round_bg));
    }

    private View popUpFragmentInit()
    {
        View fragment = getLayoutInflater().inflate(R.layout.color_picker, drawerOptions, false);

        ImageButton redBtn = fragment.findViewById(R.id.redBtn);
        ImageButton yellowBtn = fragment.findViewById(R.id.yellowBtn);
        ImageButton brownBtn = fragment.findViewById(R.id.brownBtn);
        ImageButton blueBtn = fragment.findViewById(R.id.blueBtn);
        ImageButton grayBtn = fragment.findViewById(R.id.grayBtn);
        ImageButton whiteBtn = fragment.findViewById(R.id.whiteBtn);
        ImageButton purpleBtn = fragment.findViewById(R.id.purpleBtn);
        ImageButton greenBtn = fragment.findViewById(R.id.greenBtn);
        ImageButton lightGrayBtn = fragment.findViewById(R.id.lightGrayBtn);

        Button okBtn = fragment.findViewById(R.id.okBtn);

        okBtn.setOnClickListener((View v) -> window.dismiss());
        redBtn.setOnClickListener((View v) -> changeColor(fetchColor(android.R.color.holo_red_dark)));
        yellowBtn.setOnClickListener((View v) -> changeColor(fetchColor(R.color.yellow)));
        brownBtn.setOnClickListener((View v) -> changeColor(fetchColor(R.color.brown)));
        blueBtn.setOnClickListener((View v) -> changeColor(fetchColor(android.R.color.holo_blue_dark)));
        grayBtn.setOnClickListener((View v) -> changeColor(fetchColor(android.R.color.darker_gray)));
        whiteBtn.setOnClickListener((View v) -> changeColor(fetchColor(android.R.color.white)));
        purpleBtn.setOnClickListener((View v) -> changeColor(fetchColor(android.R.color.holo_purple)));
        greenBtn.setOnClickListener((View v) -> changeColor(fetchColor(android.R.color.holo_green_dark)));
        lightGrayBtn.setOnClickListener((View v) -> changeColor(fetchColor(R.color.lightgray)));

        return fragment;
    }

    private int fetchColor(int colorResourceID)
    {
        return getResources().getColor(colorResourceID);
    }

    private void changeColor(int color)
    {
        colorBtn.setBackgroundColor(color);
        currentColor = color;
    }
}