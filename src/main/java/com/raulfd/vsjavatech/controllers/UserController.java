package com.raulfd.vsjavatech.controllers;

import com.raulfd.vsjavatech.repositories.UserRepository;
import com.raulfd.vsjavatech.services.FileDownloadService;
import com.raulfd.vsjavatech.services.FileUploadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class UserController {
    final FileUploadService fileUploadUtil;
    private final UserRepository repository;

    UserController(UserRepository repository, FileUploadService fileUploadUtil) {
        this.repository = repository;
        this.fileUploadUtil = fileUploadUtil;
    }

    @GetMapping("/api/users")
    public ResponseEntity<?> downloadFile() {
        FileDownloadService downloadUtil = new FileDownloadService();

        Resource resource;
        try {
            File fileToDownload = saveFileInResourcesFolder(repository).get();
            resource = downloadUtil.getFileAsResource(fileToDownload).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/json";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    @PostMapping("/api/copy")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        File fileToSave = new File(fileName);
        Path pathOfThePrj = Path.of("").toAbsolutePath();
        Path dirPath = Paths.get(pathOfThePrj + FileSystems.getDefault().getSeparator() + "filesCopied" + FileSystems.getDefault().getSeparator() + fileToSave);

        fileUploadUtil.saveFile(multipartFile.getBytes(), dirPath);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Async
    CompletableFuture<File> saveFileInResourcesFolder(UserRepository repository) {
        String usersJSON = repository.findAll().toString();
        String FILE_NAME = "src/main/resources/users.json";
        File targetFile = new File(FILE_NAME);
        try {
            if (targetFile.exists()) //noinspection ResultOfMethodCallIgnored Not needed to save result
                targetFile.delete();
            BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
            writer.write(usersJSON);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return CompletableFuture.completedFuture(targetFile);

    }
}