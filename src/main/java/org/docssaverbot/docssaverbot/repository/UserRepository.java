package org.docssaverbot.docssaverbot.repository;

import org.docssaverbot.docssaverbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(value = "select * from users u where u.chat_id= :chat_id",nativeQuery = true)
    User findByChatId(@Param("chat_id") Long chat_id);

}
