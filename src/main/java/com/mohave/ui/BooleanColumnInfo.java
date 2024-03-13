package com.mohave.ui;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.ui.ColumnInfo;
import com.mohave.domain_objects.ConfigSettings;

public abstract class BooleanColumnInfo extends ColumnInfo<ConfigSettings.ConfigEntry, Boolean> {

    public BooleanColumnInfo(@NlsContexts.ColumnName String name) {
        super(name);
    }
    @Override
    public Class<?> getColumnClass() {
        return Boolean.class;
    }

    @Override
    public boolean isCellEditable(ConfigSettings.ConfigEntry configEntry) {
        return configEntry.isPathDirectory();
    }

}
