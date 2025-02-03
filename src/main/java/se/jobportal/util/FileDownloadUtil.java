package se.jobportal.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadUtil {

    public static Resource getFileAsResource(String downloadDir, String fileName) throws IOException {
        Path path = Paths.get(downloadDir, fileName);
        if (Files.exists(path) && Files.isReadable(path)) {
            return new UrlResource(path.toUri());
        }
        throw new IOException("File not found or not readable: " + fileName);
    }
}
