package com.fleotadezuta.youthprogrammanager.constants;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class Disease {
    private LocalDateTime diagnosedAt;
    private String name;
}