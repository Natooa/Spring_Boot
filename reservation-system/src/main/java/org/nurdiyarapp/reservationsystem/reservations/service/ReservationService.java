package org.nurdiyarapp.reservationsystem.reservations.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.nurdiyarapp.reservationsystem.reservations.entity.Reservation;
import org.nurdiyarapp.reservationsystem.reservations.entity.ReservationEntity;
import org.nurdiyarapp.reservationsystem.reservations.entity.ReservationStatus;
import org.nurdiyarapp.reservationsystem.reservations.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation getReservationById(
            Long id
    ) {
        reservationRepository.findAllByStatusIs(ReservationStatus.PENDING);

        ReservationEntity reservationEntity = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found by id: " + id));
        return toDomainReservation(reservationEntity);
    }

    public List<Reservation> findAllReservation() {
        List<ReservationEntity> allEntities = reservationRepository.findAll();

        return allEntities.stream()
                .map(this::toDomainReservation)
                .toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        if(!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())) {
            throw new IllegalArgumentException("Start date should be after end date min in 1 day");
        }
        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        var savedEntity = reservationRepository.save(entityToSave);
        return toDomainReservation(savedEntity);
    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {
        var reservationEntity = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found by id: " + id));
        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot modify reservation status=" + reservationEntity.getStatus());
        }
        if(!reservationToUpdate.endDate().isAfter(reservationToUpdate.startDate())) {
            throw new IllegalArgumentException("Start date should be after end date min in 1 day");
        }
        var reservationToSave = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        var updatedReservation = reservationRepository.save(reservationToSave);
        return toDomainReservation(updatedReservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        var reservation = reservationRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Reservation not found by id: " + id));
        if(reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new IllegalArgumentException("Cannot cancel approved reservation status with id=" + id);
        }
        if(reservation.getStatus().equals(ReservationStatus.CANCELED)) {
            throw new IllegalArgumentException("Cannot cancel already canceled reservation status with id=" + id);
        }
        reservationRepository.setStatus(id, ReservationStatus.CANCELED);
        LOGGER.info("Successfully cancel reservation by id: " + id);
    }

    public Reservation approveReservation(Long id) {
        var reservationEntity = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found by id: " + id));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot approve reservation status=" + reservationEntity.getStatus());
        }

        var isConflict = isReservationConflict(reservationEntity);
        if (isConflict) {
            throw new IllegalArgumentException("Cannot approve reservation because of conflict");
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);
        reservationRepository.save(reservationEntity);

        return toDomainReservation(reservationEntity);
    }

    private boolean isReservationConflict(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Long> conflictingIds = reservationRepository.findConflictReservationIds(
                roomId,
                startDate,
                endDate,
                ReservationStatus.APPROVED
        );
        if(conflictingIds.isEmpty()) {
            return false;
        }
        LOGGER.info("Conflicting reservation ids: " + conflictingIds);
        return true;
    }

    private Reservation toDomainReservation(ReservationEntity reservationEntity) {
        return new Reservation(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                reservationEntity.getStatus()
        );
    }
}
