package com.tapiodev.sijoitussovellus;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InvestmentDao {

    @Query("SELECT * FROM investment WHERE investment LIKE :name LIMIT 1")
    Investment findByName(String name);

    @Query("SELECT * FROM investment")
    List<Investment> getAllInvestments();

    @Query("DELETE FROM investment")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Investment investment);

    @Delete
    void delete(Investment investment);


}
