package com.example.accountbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountbook.adapter.RecordAdapter;
import com.example.accountbook.database.DatabaseHelper;
import com.example.accountbook.database.Record;
import com.example.accountbook.utils.DateUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Record> records;

    private TextView tvTotalIncome;
    private TextView tvTotalExpense;
    private TextView tvBalance;
    private TextView tvDate;
    private ImageView btnStatistics;

    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupRecyclerView();
        loadData();
        setupFAB();
        setupStatisticsButton();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        tvTotalIncome = findViewById(R.id.tv_total_income);
        tvTotalExpense = findViewById(R.id.tv_total_expense);
        tvBalance = findViewById(R.id.tv_balance);
        tvDate = findViewById(R.id.tv_date);
        btnStatistics = findViewById(R.id.btn_statistics);

        tvDate.setText(DateUtils.getCurrentDate());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecordAdapter(null, new RecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Record record) {
                Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
                intent.putExtra("record_id", record.getId());
                intent.putExtra("type", record.getType());
                intent.putExtra("category", record.getCategory());
                intent.putExtra("amount", record.getAmount());
                intent.putExtra("note", record.getNote());
                intent.putExtra("date", record.getDate());
                intent.putExtra("time", record.getTime());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Record record) {
                showDeleteDialog(record);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        records = dbHelper.getAllRecords();
        adapter.updateData(records);
        updateSummary();
    }

    private void updateSummary() {
        double totalIncome = dbHelper.getTotalIncome();
        double totalExpense = dbHelper.getTotalExpense();
        double balance = totalIncome - totalExpense;

        tvTotalIncome.setText("+" + df.format(totalIncome));
        tvTotalExpense.setText("-" + df.format(totalExpense));
        tvBalance.setText(df.format(balance));

        // 使用 ContextCompat.getColor() 替代 getColor()
        if (balance >= 0) {
            tvBalance.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            tvBalance.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
    }

    private void setupFAB() {
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
            startActivity(intent);
        });
    }

    private void setupStatisticsButton() {
        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });
    }

    private void showDeleteDialog(Record record) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("删除记录")
                .setMessage("确定要删除这条记录吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    dbHelper.deleteRecord(record.getId());
                    loadData();
                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}