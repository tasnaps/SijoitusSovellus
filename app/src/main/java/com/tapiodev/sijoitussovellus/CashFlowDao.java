package com.tapiodev.sijoitussovellus;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;


@Dao
public interface CashFlowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CashFlow cashFlow);

    @Query("SELECT * FROM cashflow_table")
    List<CashFlow> getAllCashFlows();

}
