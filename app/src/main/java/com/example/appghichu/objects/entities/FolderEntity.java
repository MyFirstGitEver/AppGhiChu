package com.example.appghichu.objects.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "folder",

        indices = {
        @Index(name = "parentIndex", value = "parentID")
        },

        foreignKeys = {
        @ForeignKey(entity = FolderEntity.class, parentColumns = "id", childColumns = "parentID", onDelete = ForeignKey.CASCADE)
        }
)
public class FolderEntity
{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int parentID;
    private String folderName;

    public int getId() {
        return id;
    }

    public int getParentID() {
        return parentID;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}