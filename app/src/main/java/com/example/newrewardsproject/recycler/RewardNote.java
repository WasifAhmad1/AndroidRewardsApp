package com.example.newrewardsproject.recycler;

public class RewardNote {
    private String giverName;
    private String amount;
    private String note;
    private String awardDate;

    public RewardNote(String giverName, String amount, String note, String awardDate) {
        this.giverName = giverName;
        this.amount = amount;
        this.note = note;
        this.awardDate = awardDate;
    }

    public String getGiverName() {
        return giverName;
    }

    public String getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getAwardDate() {
        return awardDate;
    }

    public void setGiverName(String giverName) {
        this.giverName = giverName;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAwardDate(String awardDate) {
        this.awardDate = awardDate;
    }
}
