package com.example.accountbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accountbook.database.DatabaseHelper;
import com.example.accountbook.database.Record;
import com.example.accountbook.utils.DateUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class AddRecordActivity extends AppCompatActivity {
    private EditText etAmount;
    private EditText etNote;
    private Spinner spinnerCategory;
    private ChipGroup chipGroupType;
    private Button btnSave;
    private Button btnCancel;

    private DatabaseHelper dbHelper;
    private String selectedType = "expense";
    private int editingRecordId = -1;

    // 支出类别
    private String[] expenseCategories = {"餐饮", "交通", "购物", "娱乐", "医疗", "教育", "人情", "其他"};
    // 收入类别
    private String[] incomeCategories = {"工资", "奖金", "投资", "兼职", "其他"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupTypeSelection();
        loadEditingData();
        setupSaveButton();
        setupCancelButton();
    }

    private void initViews() {
        etAmount = findViewById(R.id.et_amount);
        etNote = findViewById(R.id.et_note);
        spinnerCategory = findViewById(R.id.spinner_category);
        chipGroupType = findViewById(R.id.chip_group_type);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // 默认选择支出类别
        updateCategorySpinner("expense");
    }

    private void setupTypeSelection() {
        chipGroupType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds != null && !checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_income) {
                    selectedType = "income";
                } else if (checkedId == R.id.chip_expense) {
                    selectedType = "expense";
                }
                updateCategorySpinner(selectedType);
            }
        });

        // 默认选中支出
        Chip expenseChip = findViewById(R.id.chip_expense);
        expenseChip.setChecked(true);
    }

    private void updateCategorySpinner(String type) {
        String[] categories = type.equals("income") ? incomeCategories : expenseCategories;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void loadEditingData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("record_id")) {
            editingRecordId = intent.getIntExtra("record_id", -1);
            if (editingRecordId != -1) {
                // 设置标题为编辑模式
                setTitle("编辑记录");
                btnSave.setText("更新");

                // 填充数据
                String type = intent.getStringExtra("type");
                String category = intent.getStringExtra("category");
                double amount = intent.getDoubleExtra("amount", 0);
                String note = intent.getStringExtra("note");

                // 设置类型
                if ("income".equals(type)) {
                    Chip incomeChip = findViewById(R.id.chip_income);
                    incomeChip.setChecked(true);
                } else {
                    Chip expenseChip = findViewById(R.id.chip_expense);
                    expenseChip.setChecked(true);
                }

                // 设置金额和备注
                etAmount.setText(String.valueOf(amount));
                if (note != null && !note.isEmpty()) {
                    etNote.setText(note);
                }

                // 设置类别
                String[] categories = type.equals("income") ? incomeCategories : expenseCategories;
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equals(category)) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的金额", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, "金额必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }

            String category = spinnerCategory.getSelectedItem().toString();
            String note = etNote.getText().toString().trim();
            String date = DateUtils.getCurrentDate();
            String time = DateUtils.getCurrentTime();

            Record record = new Record(selectedType, category, amount, note, date, time);

            if (editingRecordId != -1) {
                record.setId(editingRecordId);
                dbHelper.updateRecord(record);
                Toast.makeText(this, "记录已更新", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.insertRecord(record);
                Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show();
            }

            finish();
        });
    }

    private void setupCancelButton() {
        btnCancel.setOnClickListener(v -> finish());
    }
}