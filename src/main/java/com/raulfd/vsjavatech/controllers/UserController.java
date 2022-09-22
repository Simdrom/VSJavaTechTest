package com.raulfd.vsjavatech.controllers;

import com.raulfd.vsjavatech.domain.FileUpload;
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
    private final UserRepository repository;

    UserController(UserRepository repository) {
        this.repository = repository;
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
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        File fileToSave = new File(fileName);
        long size = multipartFile.getSize();
        Path pathOfThePrj = Path.of("").toAbsolutePath();
        Path dirPath = Paths.get(pathOfThePrj + FileSystems.getDefault().getSeparator() + "filesCopied" + FileSystems.getDefault().getSeparator() + fileToSave);
        FileUploadService fileUploadUtil = new FileUploadService();
        CompletableFuture<File> savedFile = fileUploadUtil.saveFile(multipartFile, dirPath);
        FileUpload response = new FileUpload();
        if (savedFile.isDone()) {
            try {
                File fileDone = savedFile.get();
                if (fileDone.exists()) {
                    response.setFileName(fileDone.getName());
                    response.setSize(size);
                    response.setDownloadUri(dirPath.toString());
                    response.setHttpStatus(HttpStatus.OK.toString());
                } else {
                    response.setHttpStatus(HttpStatus.BAD_REQUEST.toString());
                    response.setErrorMessage("Something goes wrong.");
                }
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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