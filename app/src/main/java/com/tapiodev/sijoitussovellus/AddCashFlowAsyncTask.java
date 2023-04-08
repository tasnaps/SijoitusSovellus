package com.tapiodev.sijoitussovellus;

import android.os.AsyncTask;

public class AddCashFlowAsyncTask extends AsyncTask<CashFlow, Void, Void> {
    private CashFlowDao cashFlowDao;


    public AddCashFlowAsyncTask(CashFlowDao cashFlowDao){
        this.cashFlowDao = cashFlowDao;
    }

    @Override
    protected Void doInBackground(CashFlow... cashFlows){
        //TODO take money from sellPriceInput field. If positive add, if negative deduct.
        return null;
    }
}
