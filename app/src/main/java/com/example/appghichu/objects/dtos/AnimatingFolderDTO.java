package com.example.appghichu.objects.dtos;

import com.example.appghichu.objects.entities.FolderEntity;

public class AnimatingFolderDTO
{
    public FolderEntity folder;
    public boolean isChecked;

    public AnimatingFolderDTO(FolderEntity folder)
    {
        this.folder = folder;
    }
}