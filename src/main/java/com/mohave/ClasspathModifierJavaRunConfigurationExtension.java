package com.mohave;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.mohave.domain_objects.ConfigSettings;
import com.mohave.ui.ClasspathModifierSettingsEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mohave.domain_objects.ConfigSettings.*;
import static com.mohave.ui.ClasspathModifierSettingsEditor.*;

public class ClasspathModifierJavaRunConfigurationExtension extends RunConfigurationExtension {

    @Override
    @Nullable
    public SettingsEditor createEditor(@NotNull RunConfigurationBase configuration) {
        return new ClasspathModifierSettingsEditor(configuration);
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase<?> runConfiguration, @NotNull Element element) {
        Optional.ofNullable(runConfiguration.getUserData(SETTING_KEY)).ifPresent(settings -> {
            JDOMExternalizerUtil.writeField(element, FIELD_ENABLED, Boolean.toString(settings.isEnabled()));
            final Element entriesElement = new Element(ELEMENT_ENTRIES);
            for (ConfigEntry entry : settings.getEntries()) {
                final Element entryElement = new Element(ELEMENT_ENTRY);
                String path = entry.getPath();
                if (path != null) {
                    entryElement.setAttribute(ATTRIBUTE_PATH, entry.getPath());
                }
                entryElement.setAttribute(ATTRIBUTE_JARS, Boolean.toString(entry.isJars()));
                entryElement.setAttribute(ATTRIBUTE_JARS_DEEP, Boolean.toString(entry.isJarsDeep()));
                entryElement.setAttribute(ATTRIBUTE_AS_FOLDER, Boolean.toString(entry.isAsFolder()));
                entryElement.setAttribute(ATTRIBUTE_IS_FOLDER, Boolean.toString(entry.isPathDirectory()));
                entryElement.setAttribute(ATTRIBUTE_AT_END_OF_CP, Boolean.toString(entry.isEndOfClasspath()));

                entriesElement.addContent(entryElement);
            }
            element.addContent(entriesElement);
        });
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase<?> runConfiguration, @NotNull Element element) {
        List<ConfigSettings.ConfigEntry> entries = new ArrayList<>();
        Optional.ofNullable(element.getChild(ELEMENT_ENTRIES))
                .ifPresent(entry -> entry.getChildren(ELEMENT_ENTRY).stream()
                        .map(child -> new ConfigSettings.ConfigEntry(child.getAttributeValue(ATTRIBUTE_PATH),
                                Boolean.parseBoolean(child.getAttributeValue(ATTRIBUTE_JARS)),
                                Boolean.parseBoolean(child.getAttributeValue(ATTRIBUTE_JARS_DEEP)),
                                Boolean.parseBoolean(child.getAttributeValue(ATTRIBUTE_AS_FOLDER)),
                                Boolean.parseBoolean(child.getAttributeValue(ATTRIBUTE_IS_FOLDER)),
                                Boolean.parseBoolean(child.getAttributeValue(ATTRIBUTE_AT_END_OF_CP))
                                ))
                        .forEach(entries::add));

        ConfigSettings configSettings = new ConfigSettings(
                Boolean.parseBoolean(JDOMExternalizerUtil.readField(element, FIELD_ENABLED)),
                entries);

        runConfiguration.putUserData(SETTING_KEY, configSettings);
    }


    @Override
    public @NotNull String getSerializationId() {
        return SERIALIZATION_ID;
    }

    @Override
    public boolean isApplicableFor(@NotNull RunConfigurationBase<?> configuration) {
        return true;
    }

    @Override
    public boolean isEnabledFor(@NotNull RunConfigurationBase applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }

    @Override
    public <T extends RunConfigurationBase<?>> void updateJavaParameters(@NotNull T configuration, @NotNull JavaParameters params, RunnerSettings runnerSettings) throws ExecutionException {

    }
}
