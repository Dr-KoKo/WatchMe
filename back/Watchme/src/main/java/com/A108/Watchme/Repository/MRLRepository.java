package com.A108.Watchme.Repository;

import com.A108.Watchme.DTO.Room.RoomDetMemDTO;
import com.A108.Watchme.VO.ENUM.Status;
import com.A108.Watchme.VO.Entity.log.MemberRoomLog;
import com.A108.Watchme.VO.Entity.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface MRLRepository extends JpaRepository<MemberRoomLog, Long> {

    List<MemberRoomLog> findBymember_id(Long memberId);
    List<MemberRoomLog> findByStartAtAfter(Timestamp date);
//    List<MemberRoomLog> findBymember_idBystart_atAfter(Long id, Date date);
    List<MemberRoomLog> findByRoomId(List<Long> roomId);
    Optional<MemberRoomLog> findByMemberIdAndRoomId(Long memberId, Long roomId);

    List<MemberRoomLog> findByMemberIdAndRoomId(Long memberId, List<Long> roomId);
}
