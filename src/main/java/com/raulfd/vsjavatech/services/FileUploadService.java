package com.raulfd.vsjavatech.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

public class FileUploadService {
    @Async("asyncImportExecutor")
    public CompletableFuture<File> saveFile(MultipartFile multipartFile, Path dirPath) {
        File file = new File(dirPath.toUri());
        File parentFolder = new File(dirPath.getParent().toUri());
        if (!parentFolder.exists()) parentFolder.mkdirs();
        try (AsynchronousFileChannel asyncFile = AsynchronousFileChannel.open(dirPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            asyncFile.write(ByteBuffer.wrap(multipartFile.getInputStream().readAllBytes()), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return CompletableFuture.completedFuture(file);
    }
}