package com.example.appghichu.objects.dtos;

import com.example.appghichu.objects.entities.FolderEntity;

import java.util.ArrayList;

public class FolderDTO
{
    public FolderEntity folder;
    public int marginLeft;
    public boolean isExpanded, isChecked;

    public FolderDTO(FolderEntity folder)
    {
        this.folder = folder;
    }
}