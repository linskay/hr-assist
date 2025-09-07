package com.example.hr_assistant.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class InterviewStateDto {
    private Long vacancyId;
    private Long candidateId;
    private List<MessageDto> messages;
}
