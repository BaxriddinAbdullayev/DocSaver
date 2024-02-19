package org.docssaverbot.docssaverbot.repository;

import org.docssaverbot.docssaverbot.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {

    @Query(value = "SELECT * FROM folder f WHERE f.chat_id = :chat_id AND f.is_active = true", nativeQuery = true)
    List<Folder> findActiveFoldersByChatId(@Param("chat_id") Long chat_id);

}
