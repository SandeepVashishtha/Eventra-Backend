package com.eventra.dto;

import com.eventra.entity.Project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String message;
    private Project project;
    
    public MessageResponse(String message) {
        this.message = message;
    }
}
