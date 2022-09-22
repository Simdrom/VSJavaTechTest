package com.raulfd.vsjavatech.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class FileDownloadService {
    private Path foundFile;

    @Async
    public CompletableFuture<Resource> getFileAsResource(File targetFile) {
        Path dirPath = Paths.get(targetFile.getParent());
        try {
            Files.list(dirPath).filter(file -> file.getFileName().toString().startsWith(targetFile.getName())).forEach(file -> foundFile = file);
            return (foundFile != null) ? CompletableFuture.completedFuture(new UrlResource(foundFile.toUri())): CompletableFuture.completedFuture(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
