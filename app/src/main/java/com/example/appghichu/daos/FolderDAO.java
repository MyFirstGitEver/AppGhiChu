package com.example.appghichu.daos;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.appghichu.objects.entities.FolderEntity;

import java.util.List;

@Dao
public interface FolderDAO
{
    @Query("Select COUNT(*) from folder Where id = :id;")
    int checkIfFolderExistsUsingID(int id);

    @Query("INSERT INTO folder VALUES(0, 0, \"Thu muc chinh\")")
    void initMainFolder();

    @Query("INSERT INTO folder VALUES(-1, -1, \"Thu muc rac\")")
    void initTrashFolder();

    @Query("Select * from folder Where id = :folderID")
    FolderEntity findFolderByID(int folderID);

    @Query("Select COUNT(*) from folder Where parentID = :id")
    int countFoldersInsideFolder(int id);

    @Query("Select * from folder Where parentID = :id AND id != 0")
    List<FolderEntity> listFoldersUsingParentID(int id);

    @Query("INSERT INTO folder(parentID, folderName) VALUES(:parentID, :name)")
    void insertToParentFolder(int parentID, String name);

    @Query("Select COUNT(*) from folder Where folderName = :name")
    int checkIfFolderAlreadyExists(String name);

    @Query("Update folder SET folderName = :name Where id = :id")
    void renameFolder(String name, int id);

    @Query("Delete from folder Where id = :id")
    void deleteFolderWithID(int id);

    @Query("Delete from folder Where id IN (:ids)")
    void deleteFolders(List<Integer> ids);

    @Query("Update folder SET parentID = :parent Where id IN (:ids)")
    void moveFolders(List<Integer> ids, int parent);

    @Query("Update folder SET parentID = :parent Where id = :id")
    void moveFolder(int parent, int id);

    @Query("Select * from folder Where id != -1 AND id != 0")
    List<FolderEntity> listAllFoldersUnderManagement();
}