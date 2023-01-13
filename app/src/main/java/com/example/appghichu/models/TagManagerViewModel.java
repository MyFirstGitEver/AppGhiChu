package com.example.appghichu.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appghichu.objects.entities.NoteEntity;
import com.example.appghichu.objects.entities.TagEntity;

import java.util.ArrayList;
import java.util.List;

public class TagManagerViewModel extends ViewModel
{
    private MutableLiveData<List<TagEntity>> tags = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<NoteEntity>> notes = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<List<TagEntity>> observeTags()
    {
        return tags;
    }

    public MutableLiveData<List<NoteEntity>> observeNotes()
    {
        return notes;
    }

    public void changeNoteAt(int index, NoteEntity note)
    {
        notes.getValue().set(index, note);
    }

    public void insertNewTag(TagEntity tag)
    {
        tags.getValue().add(tag);
    }

    public boolean contains(String term)
    {
        return tags.getValue().contains(term);
    }

    public int getLastPosition()
    {
        return tags.getValue().size() - 1;
    }

    public TagEntity tagAt(int index)
    {
        return tags.getValue().get(index);
    }

    public void updateResult(List<NoteEntity> notes)
    {
        this.notes.setValue(notes);
    }
}
