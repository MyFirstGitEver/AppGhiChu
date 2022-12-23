package com.example.appghichu.objects.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "restore",
        indices = {
                @Index(name = "restoreNoteIndex", value = "noteId"),
        },
        foreignKeys = {
                @ForeignKey(entity = NoteEntity.class, parentColumns = "id", childColumns = "noteId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = FolderEntity.class, parentColumns = "id", childColumns = "lastFolderID", onDelete = ForeignKey.CASCADE)
        }
)
public class RestoreEntity
{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int noteId;
    private int lastFolderID;

    public int getId() {
        return id;
    }

    public int getNoteId() {
        return noteId;
    }

    public int getLastFolderID() {
        return lastFolderID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setLastFolderID(int lastFolderID) {
        this.lastFolderID = lastFolderID;
    }
}