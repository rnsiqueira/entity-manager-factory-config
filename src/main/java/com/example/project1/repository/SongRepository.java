package com.example.project1.repository;

import com.example.project1.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public interface SongRepository extends JpaRepository<Song, Long> {
}
