package com.example.accountbook;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.accountbook.database.DatabaseHelper;
import com.example.accountbook.database.Record;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {
    private PieChart pieChartIncome;
    private PieChart pieChartExpense;
    private TextView tvTotalIncome;
    private TextView tvTotalExpense;
    private TextView tvBalance;
    private TextView tvRecordCount;
    private ImageView btnBack;

    private DatabaseHelper dbHelper;
    private DecimalFormat df = new DecimalFormat("#0.00");
    private List<Record> allRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = DatabaseHelper.getInstance(this);
        allRecords = dbHelper.getAllRecords();

        initViews();
        setupListeners();
        setupPieCharts();
        updateSummary();
    }

    private void initViews() {
        pieChartIncome = findViewById(R.id.pie_chart_income);
        pieChartExpense = findViewById(R.id.pie_chart_expense);
        tvTotalIncome = findViewById(R.id.tv_total_income);
        tvTotalExpense = findViewById(R.id.tv_total_expense);
        tvBalance = findViewById(R.id.tv_balance);
        tvRecordCount = findViewById(R.id.tv_record_count);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupPieCharts() {
        setupIncomePieChart();
        setupExpensePieChart();
        setupRecordCount();
    }

    private void setupIncomePieChart() {
        List<Record> incomeRecords = new ArrayList<>();
        for (Record record : allRecords) {
            if ("income".equals(record.getType())) {
                incomeRecords.add(record);
            }
        }

        if (incomeRecords.isEmpty()) {
            pieChartIncome.setNoDataText("暂无收入数据");
            pieChartIncome.invalidate();
            return;
        }

        // 按类别分组统计
        Map<String, Double> categoryMap = new HashMap<>();
        for (Record record : incomeRecords) {
            String category = record.getCategory();
            double amount = record.getAmount();

            if (categoryMap.containsKey(category)) {
                categoryMap.put(category, categoryMap.get(category) + amount);
            } else {
                categoryMap.put(category, amount);
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            colors.add(ColorTemplate.VORDIPLOM_COLORS[index % ColorTemplate.VORDIPLOM_COLORS.length]);
            index++;
        }

        setupPieChartStyle(pieChartIncome, entries, colors, "收入分类");
    }

    private void setupExpensePieChart() {
        List<Record> expenseRecords = new ArrayList<>();
        for (Record record : allRecords) {
            if ("expense".equals(record.getType())) {
                expenseRecords.add(record);
            }
        }

        if (expenseRecords.isEmpty()) {
            pieChartExpense.setNoDataText("暂无支出数据");
            pieChartExpense.invalidate();
            return;
        }

        // 按类别分组统计
        Map<String, Double> categoryMap = new HashMap<>();
        for (Record record : expenseRecords) {
            String category = record.getCategory();
            double amount = record.getAmount();

            if (categoryMap.containsKey(category)) {
                categoryMap.put(category, categoryMap.get(category) + amount);
            } else {
                categoryMap.put(category, amount);
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            colors.add(ColorTemplate.JOYFUL_COLORS[index % ColorTemplate.JOYFUL_COLORS.length]);
            index++;
        }

        setupPieChartStyle(pieChartExpense, entries, colors, "支出分类");
    }

    private void setupPieChartStyle(PieChart pieChart, ArrayList<PieEntry> entries,
                                    ArrayList<Integer> colors, String description) {
        PieDataSet dataSet = new PieDataSet(entries, description);
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueLinePart1Length(0.4f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSliceSpace(3f); // 增加切片间距
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 10);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setCenterText(description);
        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void updateSummary() {
        double totalIncome = dbHelper.getTotalIncome();
        double totalExpense = dbHelper.getTotalExpense();
        double balance = totalIncome - totalExpense;

        tvTotalIncome.setText("+" + df.format(totalIncome));
        tvTotalExpense.setText("-" + df.format(totalExpense));
        tvBalance.setText(df.format(balance));

        if (balance >= 0) {
            tvBalance.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            tvBalance.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
    }

    private void setupRecordCount() {
        int totalCount = allRecords.size();
        int incomeCount = 0;
        int expenseCount = 0;

        for (Record record : allRecords) {
            if ("income".equals(record.getType())) {
                incomeCount++;
            } else {
                expenseCount++;
            }
        }

        tvRecordCount.setText("共 " + totalCount + " 条记录  |  收入 " + incomeCount + " 条  |  支出 " + expenseCount + " 条");
    }
}