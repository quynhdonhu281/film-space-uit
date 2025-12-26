package com.example.filmspace_mobile.data.model;

public class SettingItem {
    private int iconResId;
    private String title;
    private String value;
    private SettingType type;

    public enum SettingType {
        NORMAL,
        TOGGLE,
        LOGOUT
    }

    public SettingItem(int iconResId, String title, SettingType type) {
        this.iconResId = iconResId;
        this.title = title;
        this.type = type;
    }

    public SettingItem(int iconResId, String title, String value, SettingType type) {
        this.iconResId = iconResId;
        this.title = title;
        this.value = value;
        this.type = type;
    }

    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public SettingType getType() { return type; }
    public void setType(SettingType type) { this.type = type; }
}