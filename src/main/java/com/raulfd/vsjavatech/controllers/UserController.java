package com.raulfd.vsjavatech.controllers;

import com.raulfd.vsjavatech.repositories.UserRepository;
import com.raulfd.vsjavatech.services.FileDownloadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RestController
public class UserController {
    private final String FILE_NAME = "src/main/resources/users.json";
    private final UserRepository repository;

    UserController(UserRepository repository){
        this.repository = repository;
    }

    @GetMapping("/api/users")
    public ResponseEntity<?> downloadFile() {
        FileDownloadService downloadUtil = new FileDownloadService();

        Resource resource = null;
        try {
            File fileToDownload = saveFileInResourcesFolder(repository);
            resource = downloadUtil.getFileAsResource(fileToDownload);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
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
    private File saveFileInResourcesFolder(UserRepository repository) {
        String usersJSON = repository.findAll().toString();
        System.out.println(repository.findAll());
        File targetFile = new File(FILE_NAME);
        try {
            if(targetFile.exists())targetFile.delete();
            BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
            writer.write(usersJSON);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return targetFile;

    }
}