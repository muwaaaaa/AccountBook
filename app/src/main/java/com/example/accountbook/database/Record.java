package com.example.accountbook.database;

public class Record {
    private int id;
    private String type; // "income" 或 "expense"
    private String category;
    private double amount;
    private String note;
    private String date;
    private String time;

    public Record() {}

    public Record(String type, String category, double amount, String note, String date, String time) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.time = time;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}