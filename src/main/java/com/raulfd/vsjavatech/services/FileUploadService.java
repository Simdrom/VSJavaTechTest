package com.raulfd.vsjavatech.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@SuppressWarnings("CommentedOutCode")
@Service
public class FileUploadService {

    @Async
    public void saveFile(byte[] multipartFileBytes, Path dirPath) {
        File targetFile = new File(dirPath.toUri());
        File targetFileParentFile = new File(targetFile.getParent());
        if (!targetFileParentFile.exists()) //noinspection ResultOfMethodCallIgnored Not needed to save result
            targetFileParentFile.mkdirs();
        writeFileInLocalAsync(multipartFileBytes, targetFile);
    }

    @Async
    void writeFileInLocalAsync(byte[] multipartFileBytes, File targetFile) {
        // The next try-catch is for testing purposes. Uncomment to make the thread wait 10 secs.
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(Path.of(targetFile.getPath()), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer buffer = ByteBuffer.wrap(multipartFileBytes);
            asynchronousFileChannel.write(buffer, 0);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
