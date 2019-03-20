package it.infocert.eigor.cli;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {

    public static File copyResourceToFolder(String resourcePath, File destinationFolder) throws IOException {
        URL resource = Object.class.getResource(resourcePath);
        String path = resource.getPath();
        path = path.substring(path.lastIndexOf('/') + 1);
        File file = new File(destinationFolder, path);
        Path p = file.toPath();
        Files.copy(
                resource.openStream(),
                p
        );
        return file;
    }
}
