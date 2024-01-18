package com.fleotadezuta.youthprogrammanager.model;

import com.fleotadezuta.youthprogrammanager.persistence.document.HistoryData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "ticket")
public class TicketUpdateDto {
    private String childId;
    private String ticketTypeId;
    private LocalDateTime issueDate;
    private LocalDateTime expirationDate;
    private Integer price;
    private Integer numberOfParticipation;
    private List<HistoryData> historyLog;
}
