package org.nurdiyarapp.reservationsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
            ) {
        log.info("getReservationById");
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(reservationService.getReservationById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        }
//        return reservationService.getReservationById(id);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        log.info("getAllReservations");
        return ResponseEntity.ok(reservationService.findAllReservation());
//        return reservationService.findAllReservation();
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestBody Reservation reservationToCreate
    ){
        log.info("createReservation");
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("test-header", "123")
                .body(reservationService.createReservation(reservationToCreate));
//        return reservationService.createReservation(reservationToCreate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") Long id,
            @RequestBody Reservation reservationToUpdate
    ) {
        log.info("updateReservation id={}, reservationToUpdate={}",
                id, reservationToUpdate);

        var updated = reservationService.updateReservation(id, reservationToUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable("id") Long id
    ) {
        log.info("deleteReservation: id={}", id);
        try{
            reservationService.cancelReservation(id);
            return ResponseEntity.ok()
                    .build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable("id") Long id
    ) {
        log.info("approveReservation: id={}", id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}
