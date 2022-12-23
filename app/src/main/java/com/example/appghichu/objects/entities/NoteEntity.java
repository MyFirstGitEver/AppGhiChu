package com.example.appghichu.objects.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "note",
        foreignKeys = {
                @ForeignKey(entity = FolderEntity.class, parentColumns = "id", childColumns = "folderID", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(name = "folderIndex", value = "folderID")}
)
public class NoteEntity implements Parcelable
{
    public static final int EMPTY = 0;
    public static final int REAL_NOTE = 1;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int numberOfPages, folderID;
    private String title, previewPath;
    private Date creationDate;

    public NoteEntity(int id, String previewPath, String title, Date creationDate, int numberOfPages, int folderID)
    {
        this.id = id;
        this.previewPath = previewPath;
        this.title = title;
        this.creationDate = creationDate;
        this.numberOfPages = numberOfPages;
        this.folderID = folderID;
    }

    public NoteEntity(Parcel in)
    {
        id = in.readInt();
        previewPath = in.readString();
        title = in.readString();
        numberOfPages = in.readInt();

        long dateLong = in.readLong();
        creationDate = new Date(dateLong);
        folderID = in.readInt();
    }

    public static final Creator<NoteEntity> CREATOR = new Creator<NoteEntity>() {
        @Override
        public NoteEntity createFromParcel(Parcel in) {
            return new NoteEntity(in);
        }

        @Override
        public NoteEntity[] newArray(int size) {
            return new NoteEntity[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getFolderID() {
        return folderID;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void setFolderID(int folderID) {
        this.folderID = folderID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(previewPath);
        parcel.writeString(title);
        parcel.writeInt(numberOfPages);
        parcel.writeLong(creationDate.getTime());
        parcel.writeInt(folderID);
    }
}
