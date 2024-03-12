package com.mohave.ui;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnActionButtonUpdater;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import com.mohave.domain_objects.ConfigSettings;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ClasspathModifierSettingsPanel extends JPanel {
    private final RunConfigurationBase config;
    private final JCheckBox checkBox;
    private final ListTableModel<ConfigSettings.ConfigEntry> files;
    private final TableView<ConfigSettings.ConfigEntry> table;

    public ClasspathModifierSettingsPanel(RunConfigurationBase config) {

        this.config = config;

        ColumnInfo<ConfigSettings.ConfigEntry, String> file = new ColumnInfo<ConfigSettings.ConfigEntry, String>("Path") {
            @Override
            public @Nullable String valueOf(ConfigSettings.ConfigEntry configEntry) {
                return configEntry.getPath();
            }

        };
        ColumnInfo<ConfigSettings.ConfigEntry, Boolean> jars = new BooleanColumnInfo("JARs") {
            @Override
            public @Nullable Boolean valueOf(ConfigSettings.ConfigEntry configEntry) {
                return configEntry.isJars();
            }
            @Override
            public void setValue(ConfigSettings.ConfigEntry configEntry, Boolean value) {
                configEntry.setJars(value);
            }
        };

        ColumnInfo<ConfigSettings.ConfigEntry, Boolean> folder = new BooleanColumnInfo("Folder") {
            @Override
            public @Nullable Boolean valueOf(ConfigSettings.ConfigEntry configEntry) {
                return configEntry.isAsFolder();
            }
            @Override
            public void setValue(ConfigSettings.ConfigEntry configEntry, Boolean value) {
                configEntry.setAsFolder(value);
            }


        };

        ColumnInfo<ConfigSettings.ConfigEntry, Boolean> subfolder = new BooleanColumnInfo("JARs deep") {
            @Override
            public @Nullable Boolean valueOf(ConfigSettings.ConfigEntry configEntry) {
                return configEntry.isJarsDeep();
            }
            @Override
            public void setValue(ConfigSettings.ConfigEntry configEntry, Boolean value) {
                configEntry.setJarsDeep(value);
            }
        };
        ColumnInfo<ConfigSettings.ConfigEntry, Boolean> endOfCp = new BooleanColumnInfo("Add at the end") {
            @Override
            public @Nullable Boolean valueOf(ConfigSettings.ConfigEntry configEntry) {
                return configEntry.isEndOfClasspath();
            }
            @Override
            public void setValue(ConfigSettings.ConfigEntry configEntry, Boolean value) {
                configEntry.setEndOfClasspath(value);
            }

            @Override
            public boolean isCellEditable(ConfigSettings.ConfigEntry configEntry) {
                return true;
            }
        };


        files = new ListTableModel<>(file, jars, subfolder, folder, endOfCp);
        table = new TableView<>(files);
        table.getEmptyText().setText("No File/Folder selected!");
        table.setColumnSelectionAllowed(false);
        table.setShowGrid(false);
        table.setDragEnabled(true);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setPreferredSize(new Dimension(-1, 100));
        table.setEnabled(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(40);
        table.getColumnModel().getColumn(4).setPreferredWidth(40);



        // enable checkbox
        checkBox = new JCheckBox("Enable classpath modification plugin");
        checkBox.addActionListener(e -> table.setEnabled(checkBox.isSelected()));

        // action bar
        final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(table);
        final AnActionButtonUpdater updater = event -> checkBox.isSelected();

        decorator.setAddAction(button -> doAddAction(table, files))
                .setAddActionUpdater(updater)
                .setRemoveActionUpdater(event -> updater.isEnabled(event) && table.getSelectedRowCount() >= 1);

        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, JBUI.scale(5), JBUI.scale(5)));
        checkboxPanel.add(checkBox);

        JPanel jPanel = decorator.createPanel();
        Dimension size = new Dimension(-1, 150);
        jPanel.setMinimumSize(size);
        jPanel.setPreferredSize(size);

        setLayout(new BorderLayout());
        add(checkboxPanel, BorderLayout.NORTH);
        add(jPanel, BorderLayout.CENTER);
    }

    private void doAddAction(final TableView<ConfigSettings.ConfigEntry> table, final ListTableModel<ConfigSettings.ConfigEntry> model) {

        final FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory
                .createSingleFileOrFolderDescriptor()
                .withTitle("Select file or folder");

        VirtualFile path = FileChooser.chooseFile(chooserDescriptor, config.getProject(), null);
        if (path != null) {
            String selectedPath = path.getPath();
            String baseDir = config.getProject().getBaseDir().getPath();
            if (selectedPath.startsWith(baseDir)) {
                selectedPath = selectedPath.substring(baseDir.length() + 1);
            }
            // Generate new data
            ArrayList<ConfigSettings.ConfigEntry> newModelData = new ArrayList<>(model.getItems());
            newModelData.add(new ConfigSettings.ConfigEntry(selectedPath, path.isDirectory()));
            model.setItems(newModelData);
            int index = model.getRowCount() - 1;
            // Fire data update events
            model.fireTableRowsInserted(index, index);
            table.setRowSelectionInterval(index, index);

        }
    }

    ConfigSettings getSettings() {
        return new ConfigSettings(checkBox.isSelected(), files.getItems());
    }

    void setSettings(ConfigSettings settings) {
        this.checkBox.setSelected(settings.isEnabled());
        this.table.setEnabled(settings.isEnabled());
        files.setItems(settings.getEntries());
    }

}
