package com.tapiodev.sijoitussovellus;

import android.os.AsyncTask;

public class DeleteInvestmentAsyncTask extends AsyncTask<Investment, Void, Void> {
    private InvestmentDao investmentDao;

    public DeleteInvestmentAsyncTask(InvestmentDao investmentDao) {
        this.investmentDao = investmentDao;
    }

    @Override
    protected Void doInBackground(Investment... investments) {
        investmentDao.delete(investments[0]);
        return null;
    }
}
