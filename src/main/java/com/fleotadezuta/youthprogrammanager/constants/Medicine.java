package com.fleotadezuta.youthprogrammanager.constants;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class Medicine {
    private String name;
    private String dose;
    private LocalDateTime takenSince;
}
