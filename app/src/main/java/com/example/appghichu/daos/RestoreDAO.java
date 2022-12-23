package com.example.appghichu.daos;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface RestoreDAO
{
    @Query("INSERT INTO restore(noteId, lastFolderID) VALUES(:note, :folder)")
    void addNewRestore(int note, int folder);

    @Query("Select lastFolderID from restore Where noteId = :noteId")
    int getLastFolderID(int noteId);

    @Query("Delete from restore Where noteId = :id")
    void deleteRestore(int id);
}