package com.exceldroid.expensemanager;

import static android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList = new ArrayList<>();
    private List<Expense> fullExpenseList = new ArrayList<>(); // for search
    private TextView tvTotal; // add total amount
    private EditText etSearch;
    private PieChart pieChart; //chart

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        tvTotal = findViewById(R.id.tvTotal);
        etSearch = findViewById(R.id.etSearch);
        pieChart = findViewById(R.id.pieChart);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExpenseAdapter(expenseList, this::deleteExpense, this::showEditDialog);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnAdd).setOnClickListener(v -> showAddDialog());

        loadExpenses();

        /// //////////////////////////////////system bar color
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.activityColor));
        window.setNavigationBarColor(getResources().getColor(R.color.activityColor));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            Objects.requireNonNull(window.getInsetsController()).setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |APPEARANCE_LIGHT_NAVIGATION_BARS);
        }
        else
        {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

/// ///////////////////////////////////////////////////////search code
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExpenses(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });
    }

    /// /////////////////////

    private void loadExpenses() {
        db.collection("expenses")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    fullExpenseList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Expense expense = doc.toObject(Expense.class);
                        expense.setId(doc.getId());
                        fullExpenseList.add(expense);
                    }
                    filterExpenses(etSearch.getText().toString());
                });
    }

    private void filterExpenses(String query) {
        expenseList.clear();
        double total = 0;

        for (Expense expense : fullExpenseList) {
            boolean matches = expense.getTitle().toLowerCase().contains(query.toLowerCase())
                    || expense.getCategory().toLowerCase().contains(query.toLowerCase());

            if (matches) {
                expenseList.add(expense);
                total += expense.getAmount();
            }
        }

        adapter.notifyDataSetChanged();
        tvTotal.setText("Total: ₹" + total);
        updateChart(); ////chart
    }

    private void showAddDialog() {
        showExpenseDialog(null);
    }

    private void showEditDialog(Expense expense) {
        showExpenseDialog(expense);
    }

    private void showExpenseDialog(Expense existingExpense) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_expense, null);

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etCategory = dialogView.findViewById(R.id.etCategory);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etDate = dialogView.findViewById(R.id.etDate);

        boolean isEdit = existingExpense != null;

        if (isEdit) {
            etTitle.setText(existingExpense.getTitle());
            etCategory.setText(existingExpense.getCategory());
            etAmount.setText(String.valueOf(existingExpense.getAmount()));
            etDate.setText(existingExpense.getDate());
        }

        new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit Expense" : "Add Expense")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();
                    String amountStr = etAmount.getText().toString().trim();
                    String date = etDate.getText().toString().trim();

                    if (title.isEmpty() || amountStr.isEmpty()) {
                        Toast.makeText(this, "Title and amount required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double amount = Double.parseDouble(amountStr);
                    Expense expense = new Expense(title, category, amount, date);

                    if (isEdit) {
                        expense.setId(existingExpense.getId());
                        updateExpense(expense);
                    } else {
                        addExpense(expense);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private void addExpense(Expense expense) {
        db.collection("expenses")
                .add(expense)
                .addOnSuccessListener(docRef ->
                        Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /// //////////////////////////////////////////////////////////////////////////////////
    private void updateExpense(Expense expense) {
        db.collection("expenses")
                .document(expense.getId())
                .set(expense)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
/// /////////////////////////////////////////////////////////////////////////////////////
    private void deleteExpense(Expense expense) {
        db.collection("expenses")
                .document(expense.getId())
                .delete()
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /// ////////chart

    private void updateChart() {
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenseList) {
            String category = expense.getCategory().isEmpty() ? "Other" : expense.getCategory();
            double current = categoryTotals.getOrDefault(category, 0.0);
            categoryTotals.put(category, current + expense.getAmount());
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // redraws the chart with new data
    }

}