package com.exceldroid.expensemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    // A simple interface so MainActivity gets notified when delete is tapped
    public interface OnDeleteClickListener {
        void onDelete(Expense expense);

        ////// interface to update or edit the list
        public interface OnItemClickListener {
            void onItemClick(Expense expense);
        }
    }

    private List<Expense> expenseList;
    private OnDeleteClickListener deleteListener;
    private OnDeleteClickListener.OnItemClickListener itemClickListener;   ///for update item

    public ExpenseAdapter(List<Expense> expenseList, OnDeleteClickListener deleteListener, OnDeleteClickListener.OnItemClickListener itemClickListener) {
        this.expenseList = expenseList;
        this.deleteListener = deleteListener;
        this.itemClickListener = itemClickListener;

    }

    // Called once per row type — inflates (creates) the row layout
    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    // Called for every row — fills the row with real data
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.tvTitle.setText(expense.getTitle());
        holder.tvCategoryDate.setText(expense.getCategory() + " • " + expense.getDate());
        holder.tvAmount.setText("₹" + expense.getAmount());

        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(expense));
        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(expense));   ///for edit
    }

    // Tells RecyclerView how many rows to draw
    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // Holds references to the views inside one row (so we don't re-find them every time)
    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategoryDate, tvAmount;
        ImageButton btnDelete;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategoryDate = itemView.findViewById(R.id.tvCategoryDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}