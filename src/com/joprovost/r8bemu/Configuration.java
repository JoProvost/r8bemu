package com.joprovost.r8bemu;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.stream.Collectors;

public class Configuration {

    public static final String RESOURCES = "/template";

    public static void prepare(Path home) throws IOException, URISyntaxException {
        if (!Files.exists(home)) {
            Files.createDirectories(home);
            copy(resource(RESOURCES), home);
        }
    }

    private static Path resource(String name) throws IOException, URISyntaxException {
        return relative(Configuration.class.getResource(name).toURI(), name);
    }

    private static void copy(Path source, Path destination) throws IOException {
        for (Path path : Files.walk(source).collect(Collectors.toList())) {
            Files.copy(path, destination.resolve(source.relativize(path).toString()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Path relative(URI uri, String path) throws IOException {
        if (uri.getScheme().equals(FileSystems.getDefault().provider().getScheme())) {
            return FileSystems.getDefault().getPath(uri.getPath());
        } else {
            return externalFileSystem(uri).getPath("/" + path);
        }
    }

    private static FileSystem externalFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException ignored) {
            return FileSystems.newFileSystem(uri, Map.of());
        }
    }
}
