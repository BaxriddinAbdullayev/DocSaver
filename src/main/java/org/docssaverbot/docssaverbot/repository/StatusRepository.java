package org.docssaverbot.docssaverbot.repository;

import org.docssaverbot.docssaverbot.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StatusRepository extends JpaRepository<Status, UUID> {


    @Query(value = "select * from status s where s.chat_id= :chat_id",nativeQuery = true)
    Status findByChatId(@Param("chat_id") Long chat_id);

}
