package com.example.appghichu.models;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appghichu.objects.entities.NoteEntity;

import java.util.ArrayList;

public class MainViewModel extends ViewModel
{
    public ArrayList<NoteEntity> notes = new ArrayList<>();
    public MutableLiveData<Intent> editorIntent = new MutableLiveData<>();
}