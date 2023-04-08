package com.tapiodev.sijoitussovellus;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "cashflow_table")
public class CashFlow {

    @PrimaryKey(autoGenerate = true)
    public int cashFlowId;

    @ColumnInfo(name = "balance")
    public double balance;


    public Date balanceDate;
}
