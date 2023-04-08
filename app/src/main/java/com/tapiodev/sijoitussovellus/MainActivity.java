package com.tapiodev.sijoitussovellus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import android.content.res.Configuration;
import java.util.Locale;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private InvestmentDao investmentDao;
    private LineChart chart;
    private double initialSum;
    private CashFlowDao cashFlowDao;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadLocale();//calling this causes problems
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button sellButton = findViewById(R.id.sell_button);
        Button buyButton = findViewById(R.id.buy_button);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        investmentDao = AppDatabase.getDatabase(this).investmentDao();
        TextView totalInvestment = findViewById(R.id.tv_totalInvestment);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here
                int id = item.getItemId();

                if (id == R.id.action_finnish) {
                    setLocale("fi");
                } else if (id == R.id.action_english) {
                    setLocale("en");
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });



        toggle.syncState();
        cashFlowDao = SecondaryDatabase.getDatabase(this).cashFlowDao();
        setupLineChart();
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SellActivity.class);
                startActivity(intent);
            }
        });
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BuyActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    new initializeSums().execute();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateTotalInvestment();
        new FetchCashFlowAsyncTask().execute();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            updateTotalInvestment();
            new FetchCashFlowAsyncTask().execute();
        }
    }

    public class initializeSums extends AsyncTask<Void, Void, Double> {
        @Override
        protected Double doInBackground(Void... voids) {
            List<Investment> Linvestments = investmentDao.getAllInvestments();
            initialSum = 0.0;
            for(Investment investment: Linvestments){
                initialSum+= investment.buyPrice;
            }
            return initialSum;
        }
        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);
            String amount = String.valueOf(result);
            TextView totalInvestment = findViewById(R.id.tv_totalInvestment);
            totalInvestment.setText(amount);

            //saving sum
            SharedPreferences sharedPreferences = getSharedPreferences("sums", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("initialSum", amount);
            editor.apply();
        }
    }
    private void updateTotalInvestment() {
        SharedPreferences sharedPreferences = getSharedPreferences("sums", MODE_PRIVATE);
        String initialSum = sharedPreferences.getString("initialSum", "0");
        TextView totalInvestment = findViewById(R.id.tv_totalInvestment);
        totalInvestment.setText(initialSum);
    }
    private void setupLineChart() {
        chart = findViewById(R.id.line_chart);
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private class FetchCashFlowAsyncTask extends AsyncTask<Void, Void, List<CashFlow>> {
        @Override
        protected List<CashFlow> doInBackground(Void... voids) {
            List<CashFlow> cashFlows = cashFlowDao.getAllCashFlows();
            Date currentDate = new Date();
            long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000;

            List<CashFlow> filteredCashFlows = new ArrayList<>();
            for (CashFlow cashFlow : cashFlows) {
                long difference = currentDate.getTime() - cashFlow.balanceDate.getTime();
                if (difference <= thirtyDaysInMillis) {
                    filteredCashFlows.add(cashFlow);
                }
            }
            return filteredCashFlows;
        }

        @Override
        protected void onPostExecute(List<CashFlow> cashFlows) {
            super.onPostExecute(cashFlows);
            populateLineChart(cashFlows);
        }
    }
    private void populateLineChart(List<CashFlow> cashFlows) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        LinkedHashMap<String, Float> dateToBalanceMap = new LinkedHashMap<>();

        Calendar calendar = Calendar.getInstance();
        for(int i = 0; i<10; i++){
            String dateKey = sdf.format(calendar.getTime());
            dateToBalanceMap.put(dateKey, 0f);
            calendar.add(Calendar.DATE, -1);
        }

        for (CashFlow cashFlow : cashFlows) {
            String dateKey = sdf.format(cashFlow.balanceDate);
            float currentBalance = dateToBalanceMap.getOrDefault(dateKey, 0f);
            dateToBalanceMap.put(dateKey, currentBalance + (float) cashFlow.balance);
        }
        List<Entry> entries = new ArrayList<>();
        List<String> xAxisDates = new ArrayList<>();
        int index = 0;
        List<Map.Entry<String, Float>> reversedEntries = new ArrayList<>(dateToBalanceMap.entrySet());
        Collections.reverse(reversedEntries);

        for (Map.Entry<String, Float> entry : reversedEntries) {
            float balance = entry.getValue();
            entries.add(new Entry(index, balance));
            xAxisDates.add(entry.getKey());
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Profit/Loss");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < xAxisDates.size()) {
                    return xAxisDates.get(index);
                } else {
                    return "";
                }
            }
        });
        chart.getDescription().setEnabled(false);
        chart.invalidate(); // Refresh the chart
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Save the selected language code in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();

        // Restart the activity to apply the changes
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    private void loadLocale() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String languageCode = preferences.getString("language", "en");
        setLocale(languageCode);
    }



}
