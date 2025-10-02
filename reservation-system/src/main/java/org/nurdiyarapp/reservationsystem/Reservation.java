package org.nurdiyarapp.reservationsystem;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;

public record Reservation (
    @Null
    Long id,

    @NotNull
    Long userId,

    @NotNull
    Long roomId,

    @FutureOrPresent
    @NotNull
    LocalDate startDate,

    @FutureOrPresent
    @NotNull
    LocalDate endDate,
    ReservationStatus status
) {

}
