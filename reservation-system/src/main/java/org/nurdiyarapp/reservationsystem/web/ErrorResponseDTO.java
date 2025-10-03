package org.nurdiyarapp.reservationsystem.web;

import java.time.LocalDateTime;

public record ErrorResponseDTO (
        String message,
        String detailedMessage,
        LocalDateTime errorTime
){

}
