package com.fleotadezuta.youthprogrammanager.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
@Document
public class HistoryData {
    private LocalDateTime date;
    private String reporter;
}
