package com.example.project1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SongDto {

    private Long id;

    private String title;

    private String text;

    private Long userId;

    private UserDto userDto;



}
