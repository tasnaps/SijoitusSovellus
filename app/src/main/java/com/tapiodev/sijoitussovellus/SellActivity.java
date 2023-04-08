package com.tapiodev.sijoitussovellus;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Bundle;
import java.util.List;
public class SellActivity extends AppCompatActivity {
    private InvestmentDao investmentDao;
    private RecyclerView investmentRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        investmentRecyclerView = findViewById(R.id.InvestmentRecyclerview);
        investmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        investmentDao = AppDatabase.getDatabase(this).investmentDao();
        new GetInvestmmentsAsyncTask().execute();
    }
    private class GetInvestmmentsAsyncTask extends AsyncTask<Void, Void, List<Investment>> {
        @Override
        protected List<Investment> doInBackground(Void... voids) {
            return investmentDao.getAllInvestments();
        }
        @Override
        protected void onPostExecute(List<Investment> investments){
            super.onPostExecute(investments);
            SecondaryDatabase secondaryDatabase = SecondaryDatabase.getDatabase(SellActivity.this);
            InvestmentAdapter investmentAdapter = new InvestmentAdapter(investments, investmentRecyclerView, investmentDao, secondaryDatabase);
            investmentRecyclerView.setAdapter(investmentAdapter);
        }
    }
}