package com.tapiodev.sijoitussovellus;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Investment {

    @PrimaryKey(autoGenerate = true)
    public int investmentId;

    @ColumnInfo(name = "investment")
    public String investment;

    @ColumnInfo(name = "buyPrice")
    public double buyPrice;

    @ColumnInfo(name = "sellPrice")
    public double sellPrice;

    @ColumnInfo(name = "buyDate")
    public Date buyDate;

    @ColumnInfo(name = "sellDate")
    public Date sellDate;

    @ColumnInfo(name = "numStocks")
    public int numStocks;
}