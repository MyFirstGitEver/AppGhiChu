package com.example.appghichu.interfaces;

public interface OnFolderManagerListener
{
    boolean isSelecting();
    void onRenameFolder(int id, int index);
    void onDeleteFolder(int id, int index);
    void onMoveFolder(int id, int index);
}
