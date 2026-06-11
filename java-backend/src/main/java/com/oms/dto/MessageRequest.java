package com.oms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequest {

    @NotBlank(message = "Text must not be blank")
    @Size(max = 5000, message = "Message must not exceed 5000 characters")
    private String text;

    @NotBlank(message = "Author must not be blank")
    private String author;

    private String platform = "unknown";
}
