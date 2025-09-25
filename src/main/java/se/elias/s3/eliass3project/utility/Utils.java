package se.elias.s3.eliass3project.utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {
    public static String zipFolder(String folderPath) throws IOException {
        String zipFile = folderPath + ".zip";
        Path sourceDir = Paths.get(folderPath);

        try (ZipOutputStream zs = new ZipOutputStream(new FileOutputStream(zipFile))) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry entry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zs.putNextEntry(entry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
        return zipFile;
    }
}