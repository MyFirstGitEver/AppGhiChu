package com.example.appghichu.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appghichu.AppDatabase;
import com.example.appghichu.activities.EditorActivity;
import com.example.appghichu.objects.dtos.NotePageDTO;
import com.example.appghichu.objects.entities.TagEntity;

import java.util.ArrayList;

public class EditorViewModel extends ViewModel
{
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
}
