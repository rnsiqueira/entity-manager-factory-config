package com.example.project1.service;

import com.example.project1.entity.Song;
import com.example.project1.exceptions.FileNotFoundException;
import com.example.project1.exceptions.OutOfStorageException;
import com.example.project1.properties.StorageProperties;
import com.example.project1.repository.SongRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@SuppressWarnings("ALL")
@Service
public class StorageServiceImpl  implements  StorageService {

    private final Path rootLocation;

    private  final SongRepository songRepository;

    @Autowired
    public StorageServiceImpl(StorageProperties storageProperties, SongRepository songRepository) {
        this.rootLocation = Paths.get(storageProperties.getLocation());
        this.songRepository = songRepository;
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new OutOfStorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new OutOfStorageException("Cant save an empty file");
            }
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new OutOfStorageException(
                        "Impossible to save file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new OutOfStorageException("Failed to save file.", e);
        }
    }

    @Override
    public Stream<Path> uploadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new OutOfStorageException("Failed to read saved files", e);
        }
    }

    @Override
    public Path upload(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public UrlResource uploadAsResource(String filename) {
        try {
            Path file = upload(filename);
            UrlResource resource =  new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()){
                return resource;
            }
            else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public byte[] getByteArrayByFileName(String fileName) throws IOException {
        Path destinationFile = this.rootLocation.resolve(
                        Paths.get(fileName))
                .normalize().toAbsolutePath();
        byte[] fileContent = FileUtils.readFileToByteArray(destinationFile.toFile());
        return fileContent;
    }

    @Override
    public String getBase64SongById(Long id) throws IOException {
        Song song = songRepository.getById(id);
        File saved =new File(song.getPath());
        byte [] songContent = FileUtils.readFileToByteArray(saved);
        return String.valueOf(songContent);

    }


}
