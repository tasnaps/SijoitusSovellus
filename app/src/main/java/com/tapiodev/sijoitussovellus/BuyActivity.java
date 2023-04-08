package com.tapiodev.sijoitussovellus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

import com.google.android.material.textfield.TextInputEditText;

public class BuyActivity extends AppCompatActivity {

    private TextInputEditText stockPriceInput, stockNameInput, stockAmountInput;
    private Button btnEnter, btnCancel;
    private InvestmentDao investmentDao;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        stockPriceInput = findViewById(R.id.stockPriceInput);
        stockNameInput = findViewById(R.id.stockNameInput);
        stockAmountInput = findViewById(R.id.stockAmountInput);
        btnCancel = findViewById(R.id.btn_cancel);
        btnEnter = findViewById(R.id.btn_enter);
        investmentDao = AppDatabase.getDatabase(this).investmentDao();

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInvestment();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
                Intent intent = new Intent(BuyActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        calendar = Calendar.getInstance();
    }

    private void saveInvestment() {
        String stockName = stockNameInput.getText().toString();
        double stockPrice = Double.parseDouble(stockPriceInput.getText().toString());
        int stockAmount = Integer.parseInt(stockAmountInput.getText().toString());

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        Date buyDate = new Date(currentYear, currentMonth, currentDay);

        //Create invenstment object
        Investment investmentObj = new Investment();
        investmentObj.investment = stockName;
        investmentObj.buyPrice = stockPrice;
        investmentObj.numStocks = stockAmount;
        investmentObj.buyDate = buyDate;

        new InsertInvestmentTask().execute(investmentObj);

        //notification for success
        Toast.makeText(this, "Investment saved", Toast.LENGTH_SHORT).show();
        clearFields();

        setResult(RESULT_OK);
        finish();

    }
    private void clearFields(){
        stockPriceInput.getText().clear();
        stockNameInput.getText().clear();
        stockAmountInput.getText().clear();
    }
    private class InsertInvestmentTask extends AsyncTask<Investment, Void, Void> {
        @Override
        protected Void doInBackground(Investment... investments) {
            investmentDao.insert(investments[0]);
            return null;
        }
    }

}