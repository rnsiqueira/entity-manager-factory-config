package com.example.project1.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name= "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String fileKey;

    private String path;

    public Song(byte[] base64SongByName) {
    }

    public Song() {

    }
}
