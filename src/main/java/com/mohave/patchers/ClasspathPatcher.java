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

                if (settings.isEnabled()) {
                    for (ConfigSettings.ConfigEntry entry : settings.getEntries()) {
                        if (entry.getPath() == null) continue;

                        // Might be a good idea to remove the path from the classpath before adding it again?

                        File modificationPath = new File(entry.getPath());

                        if (!modificationPath.exists()) {
                            logger.debug("Path " + modificationPath + " does not exist. Trying to resolve it with the project base path");
                            modificationPath = new File(((ApplicationConfiguration) configuration).getProject().getBasePath(), entry.getPath());
                            if (modificationPath.exists()) {
                                logger.trace("Resolved path " + modificationPath);
                            } else {
                                logger.error("Path " + modificationPath + " does not exist. Skipping");
                                continue;
                            }
                        }

                        if (entry.isAsFolder()) {
                            addToClasspath(parameters, entry, modificationPath.getAbsolutePath();
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
}
