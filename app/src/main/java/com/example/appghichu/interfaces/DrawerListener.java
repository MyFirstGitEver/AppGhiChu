package com.example.appghichu.interfaces;

public interface DrawerListener
{
    void onMoveToFolder(int folderID);
    void onMoveToMain();
    void onMoveToTrashBin();
    void onMoveToTagManager();
    void onMoveToFolderManager();
}