package com.mohave.patchers;

import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.mohave.domain_objects.ConfigSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static com.mohave.ui.ClasspathModifierSettingsEditor.SETTING_KEY;

public class ClasspathPatcher extends JavaProgramPatcher {

    private static final Logger logger = Logger.getInstance(ClasspathPatcher.class);
    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters parameters) {

        logger.warn("Patching classpath");

        if (configuration instanceof ApplicationConfiguration) {

            Optional.ofNullable(((ApplicationConfiguration) configuration).getUserData(SETTING_KEY)).ifPresent(settings -> {
                for (ConfigSettings.ConfigEntry entry : settings.getEntries()) {
                    if (entry.getPath() == null) continue;

                    // Might be a good idea to remove the path from the classpath before adding it again?

                    File modificationPath = new File(entry.getPath());

                    if (entry.isAsFolder()) {
                        addToClasspath(parameters, entry, entry.getPath());
                    }

                    if (entry.isJars()) {
                        try {
                            if (entry.isJarsDeep()) {
                                Files.walk(modificationPath.toPath()).forEach(file -> {
                                    if (FileUtil.extensionEquals(file.toFile().getName(), "jar")) {
                                        addToClasspath(parameters, entry,file.toFile().getAbsolutePath());
                                    }
                                });
                            } else {
                                Files.list(modificationPath.toPath()).forEach(file -> {
                                    if (FileUtil.extensionEquals(file.toFile().getName(), "jar")) {
                                        addToClasspath(parameters, entry,file.toFile().getAbsolutePath());
                                    }
                                });
                            }


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }

    private static void addToClasspath(JavaParameters parameters, ConfigSettings.ConfigEntry entry, String element) {
        if (entry.isEndOfClasspath()) {
            parameters.getClassPath().addTail(element);
            logger.debug("Adding " + element + " to the end of the classpath");
        } else {
            parameters.getClassPath().addFirst(element);
            logger.debug("Adding " + element + " to the beginning of the classpath");
        }
    }


            /*for (ModuleBasedConfigurationOptions.ClasspathModification modification : ((ApplicationConfiguration) configuration).getClasspathModifications()) {

                if (modification.getPath() == null) continue;

                parameters.getClassPath().remove(modification.getPath());

                File modificationPath = new File(modification.getPath());
                if (modificationPath.isDirectory()) {
                    AtomicBoolean hasJars = new AtomicBoolean(false);
                    try {
                        Files.list(modificationPath.toPath()).forEach(file -> {
                            if (FileUtil.extensionEquals(file.toFile().getName(), "jar")) {
                                parameters.getClassPath().addTail(file.toFile().getAbsolutePath());
                                hasJars.set(true);
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if (!hasJars.get()) {
                        parameters.getClassPath().addTail(modification.getPath());
                    }
                } else {
                    parameters.getClassPath().addTail(modification.getPath());
                }
            }*/

    //}
}
