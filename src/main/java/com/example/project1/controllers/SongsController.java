package com.example.project1.controllers;

import com.example.project1.dto.SongsDto;
import com.example.project1.entity.Song;
import com.example.project1.repository.SongRepository;
import com.example.project1.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequiredArgsConstructor
public class SongsController {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private final StorageService storageService;

    private final SongRepository songRepository;

    @GetMapping("/Songs/{id}")
    public ResponseEntity<?> getSongById(@PathVariable String id) throws IOException {
        byte[] base64SongByName = storageService.getByteArrayByFileName(id);
        return ResponseEntity.ok(new Song(base64SongByName));
    }

    @GetMapping("/song/url/{id}")
    public void getSong(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Song song = songRepository.findById(id).get();
        File file = new File(song.getPath());
        response.setContentType("application/mp3");
        response.addHeader(CONTENT_DISPOSITION, "attachment;  filename=song.mp3");
        InputStream inputStream = new FileInputStream(file);
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }
}
