package com.example.appghichu.models;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appghichu.objects.entities.NoteEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainViewModel extends ViewModel
{
    private MutableLiveData<Integer> noteCounter = new MutableLiveData<>(1);
    private MutableLiveData<Integer> currentFolderID = new MutableLiveData<>(0);

    private MutableLiveData<ArrayList<NoteEntity>> notes = new MutableLiveData<>(null);
    public MutableLiveData<Intent> editorIntent = new MutableLiveData<>();

    public void removeNoteAt(int index)
    {
        notes.getValue().remove(index);
    }

    public void insertNewNote(NoteEntity note)
    {
        notes.getValue().add(note);
    }

    public void changeNoteAt(int index, NoteEntity newNote)
    {
        notes.getValue().set(index, newNote);
    }

    public int getLastPosition()
    {
        return notes.getValue().size() - 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sort(boolean byDate, boolean increasing)
    {
        List<NoteEntity> notes = this.notes.getValue();

        notes.sort(new Comparator<NoteEntity>()
        {
            @Override
            public int compare(NoteEntity n1, NoteEntity n2)
            {
                int result;
                if(byDate)
                    result = n1.getCreationDate().compareTo(n2.getCreationDate());
                else
                    result = n1.getTitle().compareTo(n2.getTitle());

                if(!increasing)
                    return -result;

                return result;
            }
        });
    }

    public boolean showingEmptyMessage()
    {
        List<NoteEntity> notes = this.notes.getValue();

        return (notes.size() == 1 && notes.get(0) == null);
    }

    public void initNoteList(ArrayList<NoteEntity> notes)
    {
        this.notes.setValue(notes);
    }

    public MutableLiveData<ArrayList<NoteEntity>> observeNoteList()
    {
        return notes;
    }

    public void setCurrentFolder(int id)
    {
        currentFolderID.setValue(id);
    }

    public int getCurrentFolder()
    {
        return currentFolderID.getValue();
    }

    public void setNoteCounter(int counter)
    {
        noteCounter.setValue(counter);
    }

    public int getNoteCounter()
    {
        return noteCounter.getValue();
    }
}