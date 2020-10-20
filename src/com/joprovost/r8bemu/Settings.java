package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.discrete.Flag;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Settings {
    private final Map<String, String> options;
    private final StringBuilder help = new StringBuilder();

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

    public Flag flag(String option, boolean value, String description) {
        help(option, "[true|false]", description);
        return Flag.value(Boolean.parseBoolean(options.getOrDefault(option, String.valueOf(value))));
    }

    public String string(String option, String value, String description) {
        help(option, "<text>", description);
        return options.getOrDefault(option, value);
    }

    public Path path(String option, String value, String description) {
        help(option, "<path>", description, value.replace(System.getProperty("user.home"), "~"));
        return Path.of(options.getOrDefault(option, value));
    }

    public int integer(String option, int value, String description) {
        help(option, "<number>", description);
        return Integer.parseInt(options.getOrDefault(option, String.valueOf(value)));
    }

    private void help(String option, String type, String description) {
        help.append("  ")
            .append(column(22, "--" + option))
            .append(column(16, type))
            .append(description)
            .append('\n');
    }

    private void help(String option, String type, String description, String value) {
        help(option, type, column(42, description) + " (default: " + value + ")");
    }

    public void help() {
        System.out.println("Usage: r8bemu [options]");
        System.out.println();
        System.out.println(help);
    }

    private String column(int size, Object string) {
        return string + " ".repeat(Math.max(size - string.toString().length(), 1));
    }
}
