package com.example.ekene.managemeds.DB.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.ekene.managemeds.data.model.Medicine;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MedicineDao {

    //Get all Medicine From DB
    @Query("SELECT * FROM Medicine")
    LiveData<List<Medicine>> getAllMedicine();

    @Insert(onConflict = REPLACE)
    void addMedicine(Medicine medicine);

    @Delete
    void deleteMedicine(Medicine medicine);
}
