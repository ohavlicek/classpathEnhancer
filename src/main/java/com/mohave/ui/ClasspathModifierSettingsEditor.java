package com.mohave.ui;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.Key;
import com.mohave.domain_objects.ConfigSettings;

import javax.swing.*;
import java.util.Optional;

public class ClasspathModifierSettingsEditor extends SettingsEditor<RunConfigurationBase> {
    private final RunConfigurationBase configuration;
    ClasspathModifierSettingsPanel classpathModifierSettingsPanel;

    public static final String TITLE = "CPMConfig";
    public static final String SERIALIZATION_ID = "com.mohave.cmpConfigPlugin";
    public static final String KEY_NAME = TITLE + " settings";
    public static final Key<ConfigSettings> SETTING_KEY = new Key<>(KEY_NAME);


    public ClasspathModifierSettingsEditor(RunConfigurationBase configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void resetEditorFrom(RunConfigurationBase runConfigurationBase) {
        Optional.ofNullable(configuration.getUserData(SETTING_KEY)).ifPresent(classpathModifierSettingsPanel::setSettings);
    }

    /**
     * called all the time
     */
    @Override
    protected void applyEditorTo(RunConfigurationBase runConfigurationBase) {
        runConfigurationBase.putUserData(SETTING_KEY, classpathModifierSettingsPanel.getSettings());
    }



    @Override
    protected JComponent createEditor() {
        classpathModifierSettingsPanel = new ClasspathModifierSettingsPanel(configuration);
        return classpathModifierSettingsPanel;
    }
}
