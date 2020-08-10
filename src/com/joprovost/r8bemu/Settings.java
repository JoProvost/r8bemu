package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.Flag;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Settings {
    private final Map<String, String> options;

    public Settings(Map<String, String> options) {
        this.options = options;
    }

    public static Settings parse(String[] args) {
        Map<String, String> params = new HashMap<>();

        String key = null;
        for (var arg : args) {
            if (arg.startsWith("--")) {
                key = arg.substring(2);
                params.put(key, "true");
            } else if (key != null) {
                params.put(key, arg);
                key = null;
            }
        }

        return new Settings(params);
    }

    public Flag flag(String option, boolean value) {
        return Flag.value(Boolean.parseBoolean(options.getOrDefault(option, String.valueOf(value))));
    }

    public String string(String key, String defaultValue) {
        return options.getOrDefault(key, defaultValue);
    }

    public Path path(String key, String defaultValue) {
        return Path.of(string(key, defaultValue));
    }
}
