package com.example.appghichu.models;

import androidx.lifecycle.ViewModel;

import com.example.appghichu.objects.dtos.NotePageDTO;

import java.util.ArrayList;

public class EditorViewModel extends ViewModel
{
    public ArrayList<NotePageDTO> pages = new ArrayList<>();
}
