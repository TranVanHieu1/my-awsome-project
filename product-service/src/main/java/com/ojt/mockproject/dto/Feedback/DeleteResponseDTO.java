package com.ojt.mockproject.dto.Feedback;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteResponseDTO {
    private String message;
    private String data;

    public DeleteResponseDTO(String message, String data) {
        super();
        this.message = message;
        this.data = data;
    }
}