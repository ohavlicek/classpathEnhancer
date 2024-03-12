package com.mohave.domain_objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration settings wrapper
 */
public class ConfigSettings {

    public static final String FIELD_ENABLED = "ENABLED";
    public static final String ELEMENT_ENTRIES = "entries";
    public static final String ELEMENT_ENTRY = "entry";
    public static final String ATTRIBUTE_PATH = "path";
    public static final String ATTRIBUTE_JARS = "jars";
    public static final String ATTRIBUTE_JARS_DEEP = "jarsDeep";
    public static final String ATTRIBUTE_AS_FOLDER = "asFolder";
    public static final String ATTRIBUTE_IS_FOLDER = "isFolder";
    public static final String ATTRIBUTE_AT_END_OF_CP = "atTheEnd";
    private final boolean enabled;
    private final List<ConfigEntry> entries;

    public ConfigSettings(boolean enabled, List<ConfigEntry> entries) {
        this.enabled = enabled;
        this.entries = entries;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<ConfigEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public static class ConfigEntry {
        private final String path;
        private boolean jars;
        private boolean jarsDeep;
        private boolean asFolder;
        private boolean pathDirectory;
        private boolean endOfClasspath;


        public ConfigEntry(String path, boolean pathDirectory) {
            this.path = path;
            this.jars = pathDirectory;
            this.jarsDeep = false;
            this.asFolder = false;
            this.pathDirectory = pathDirectory;
            this.endOfClasspath = false;
        }
        public ConfigEntry(String path,boolean jars, boolean jarsDeep, boolean asFolder, boolean pathDirectory, boolean endOfClasspath){
            this.path = path;
            this.jars = jars;
            this.jarsDeep = jarsDeep;
            this.asFolder = asFolder;
            this.pathDirectory = pathDirectory;
            this.endOfClasspath = endOfClasspath;
        }

        public String getPath() {
            return path;
        }

        public boolean isJars() {
            return jars;
        }

        public boolean isJarsDeep() {
            return jarsDeep;
        }

        public boolean isAsFolder() {
            return asFolder;
        }

        public void setJars(boolean jars) {
            this.jars = jars;
        }

        public void setJarsDeep(boolean jarsDeep) {
            this.jarsDeep = jarsDeep;
        }

        public void setAsFolder(boolean asFolder) {
            this.asFolder = asFolder;
        }

        public boolean isPathDirectory() {
            return pathDirectory;
        }

        public void setPathDirectory(boolean pathDirectory) {
            this.pathDirectory = pathDirectory;
        }

        public boolean isEndOfClasspath() {
            return endOfClasspath;
        }

        public void setEndOfClasspath(boolean endOfClasspath) {
            this.endOfClasspath = endOfClasspath;
        }
    }
}

