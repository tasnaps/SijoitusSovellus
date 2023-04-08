package com.tapiodev.sijoitussovellus;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

public class InvestmentViewHolder extends RecyclerView.ViewHolder{
    public TextView investmentTextView;
    public TextView initPrice;
    public Button sellBtn;
    public TextInputEditText sellPriceInput;

    public InvestmentViewHolder(View itemView,View.OnClickListener sellBtnClickListener) {
        super(itemView);
        investmentTextView = itemView.findViewById(R.id.investmentTextView);
        initPrice = itemView.findViewById(R.id.tv_buyPrice);
        sellBtn = itemView.findViewById(R.id.sell_btn);
        sellBtn.setOnClickListener(sellBtnClickListener);
        sellPriceInput = itemView.findViewById(R.id.sellPriceInput);
    }
}
