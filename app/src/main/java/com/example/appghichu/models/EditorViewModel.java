package com.example.appghichu.models;

import android.graphics.Color;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.activities.EditorActivity;
import com.example.appghichu.objects.MyCanvas;
import com.example.appghichu.objects.dtos.NotePageDTO;
import com.example.appghichu.objects.entities.TagEntity;

import java.util.ArrayList;

public class EditorViewModel extends ViewModel
{
    public static final int PEN = 0;
    public static final int PENCIL = 1;
    public static final int ERASE = 2;

    private MutableLiveData<Boolean> isUsingDrawer = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isUsingTool = new MutableLiveData<>(true);
    private MutableLiveData<Integer> mode = new MutableLiveData<>(MyCanvas.DRAW);
    private MutableLiveData<Integer> currentColor = new MutableLiveData<>(Color.RED);
    private MutableLiveData<Integer> canvasWidth = new MutableLiveData<>(0);
    private MutableLiveData<Integer> canvasHeight = new MutableLiveData<>(0);
    private MutableLiveData<Pair<Integer, Integer>> currentTool = new MutableLiveData<>(new Pair<>(PEN, PEN));

    private MutableLiveData<ArrayList<NotePageDTO>> pages = new MutableLiveData<>(null);
    private MutableLiveData<ArrayList<TagEntity>> tags = new MutableLiveData<>(null);

    public void fetchTags(ArrayList<TagEntity> tags)
    {
        this.tags.setValue(tags);
    }

    public MutableLiveData<ArrayList<TagEntity>> observeTags()
    {
        return tags;
    }

    public ArrayList<TagEntity> getTags()
    {
        return tags.getValue();
    }

    public int getLastPosition()
    {
        return pages.getValue().size() - 1;
    }

    public void insertNewPage(NotePageDTO dto)
    {
        pages.getValue().add(dto);
    }

    public NotePageDTO pageAt(int index)
    {
        return pages.getValue().get(index);
    }

    public MutableLiveData<ArrayList<NotePageDTO>> observePages()
    {
        return pages;
    }

    public void initPages(ArrayList<NotePageDTO> dtos)
    {
        pages.setValue(dtos);
    }

    public boolean isUsingDrawer()
    {
        return isUsingDrawer.getValue();
    }

    public boolean isUsingTool()
    {
        return isUsingTool.getValue();
    }

    public void setIsUsingDrawer(boolean b)
    {
        isUsingDrawer.setValue(b);
    }

    public void setIsUsingTool(boolean b)
    {
        isUsingTool.setValue(b);
    }

    public int getMode()
    {
        return mode.getValue();
    }

    public int getCurrentColor()
    {
        return currentColor.getValue();
    }

    public int getCanvasWidth()
    {
        return canvasWidth.getValue();
    }

    public int getCanvasHeight()
    {
        return canvasHeight.getValue();
    }

    public int getCurrentTool()
    {
        return currentTool.getValue().second;
    }

    public void setCanvasWidth(int width)
    {
        canvasWidth.setValue(width);
    }

    public void setCanvasHeight(int height)
    {
        canvasHeight.setValue(height);
    }

    public void setMode(int mode)
    {
        this.mode.setValue(mode);
    }

    public void setCurrentColor(int currentColor)
    {
        this.currentColor.setValue(currentColor);
    }

    public void setCurrentTool(int currentTool)
    {
        int oldTool = this.currentTool.getValue().second;
        this.currentTool.setValue(new Pair<>(oldTool, currentTool));
    }

    public MutableLiveData<Boolean> observeDrawerState()
    {
        return isUsingDrawer;
    }

    public MutableLiveData<Boolean> observeToolState()
    {
        return isUsingTool;
    }

    public MutableLiveData<Integer> observeColor()
    {
        return currentColor;
    }

    public MutableLiveData<Pair<Integer, Integer>> observeCurrentTool()
    {
        return currentTool;
    }
}