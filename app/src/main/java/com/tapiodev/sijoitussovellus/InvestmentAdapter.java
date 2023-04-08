package com.tapiodev.sijoitussovellus;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.tapiodev.sijoitussovellus.DeleteInvestmentAsyncTask;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvestmentAdapter extends RecyclerView.Adapter<InvestmentViewHolder>{
    private List<Investment> investments;
    private RecyclerView recyclerView;
    private InvestmentDao investmentDao;
    private SecondaryDatabase secondaryDatabase;
    public InvestmentAdapter(List<Investment> investments, RecyclerView recyclerView, InvestmentDao investmentDao, SecondaryDatabase secondaryDatabase) {
        this.investments = investments;
        this.recyclerView = recyclerView;
        this.investmentDao = investmentDao;
        this.secondaryDatabase = secondaryDatabase;
    }
    @Override
    public InvestmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.investment_item, parent, false);
        View.OnClickListener sellBtnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View itemView = (View) v.getParent();
                RecyclerView recyclerView = null;
                while (itemView.getParent() != null && recyclerView == null) {
                    if (itemView.getParent() instanceof RecyclerView) {
                        recyclerView = (RecyclerView) itemView.getParent();
                    } else {
                        itemView = (View) itemView.getParent();
                    }
                }
                if (recyclerView != null) {
                    InvestmentViewHolder holder = (InvestmentViewHolder) recyclerView.getChildViewHolder(itemView);
                    int position = holder.getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        Investment investment = investments.get(position);
                        double sellPrice = holder.sellPriceInput.getText().toString().isEmpty() ? 0 : Double.parseDouble(holder.sellPriceInput.getText().toString());
                        double endPrice = sellPrice * investment.numStocks;
                        double profitLoss = endPrice - (investment.buyPrice * investment.numStocks);
                        CashFlow cashFlow = new CashFlow();
                        cashFlow.balance = profitLoss;
                        cashFlow.balanceDate = new Date();
                        new InsertCashFlowAsyncTask(secondaryDatabase.cashFlowDao()).execute(cashFlow);
                        System.out.println("Sell button clicked for investment: " + investment.investment);
                        new DeleteInvestmentAsyncTask(investmentDao).execute(investment);
                        investments.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            }
        };
        return new InvestmentViewHolder(itemView, sellBtnClickListener);
    }
    @Override
    public void onBindViewHolder(InvestmentViewHolder holder, int position) {
        Investment investment = investments.get(position);
        holder.investmentTextView.setText(investment.investment);
        double initialPrice = investment.buyPrice;
        holder.initPrice.setText(String.format(Locale.getDefault(), "Bought at: $%.2f", initialPrice));
    }
    @Override
    public int getItemCount() {
        return investments.size();
    }

    private static class InsertCashFlowAsyncTask extends AsyncTask<CashFlow, Void, Void> {
        private CashFlowDao cashFlowDao;

        private InsertCashFlowAsyncTask(CashFlowDao cashFlowDao) {
            this.cashFlowDao = cashFlowDao;
        }

        @Override
        protected Void doInBackground(CashFlow... cashFlows) {
            cashFlowDao.insert(cashFlows[0]);
            return null;
        }
    }

}