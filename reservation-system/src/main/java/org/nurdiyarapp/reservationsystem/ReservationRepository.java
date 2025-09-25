package org.nurdiyarapp.reservationsystem;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
//    List<ReservationEntity> findAllByStatusIs(ReservationStatus status);
    @Query(value = "SELECT r FROM ReservationEntity r WHERE r.status = :status")
    List<ReservationEntity> findAllByStatusIs(ReservationStatus status);
//
//    @Query("SELECT r FROM ReservationEntity r WHERE r.roomId = :roomId")
//    List<ReservationEntity> findAllByRoomId(@Param("roomId") Long roomId);
//
//    @Transactional
//    @Modifying
//    @Query("""
//            UPDATE ReservationEntity r
//            SET r.userId = :userId,
//                r.roomId = :roomId,
//                r.startDate = :startDate,
//                r.endDate = :endDate,
//                r.status = :status
//            WHERE r.id = :id
//                        """)
//    int updateAllFields(
//            @Param("id") Long id,
//            @Param("userId") Long userId,
//            @Param("roomId") Long roomId,
//            @Param("startDate")LocalDate startDate,
//            @Param("endDate")LocalDate endDate,
//            @Param("status") ReservationStatus status
//            );

    @Modifying
    @Query("""
            UPDATE ReservationEntity r\s
            SET r.status = :status\s
            WHERE r.id = :id
            \s""")
    void setStatus(@Param("id")Long id,
                   @Param("status")ReservationStatus reservationStatus);
}
