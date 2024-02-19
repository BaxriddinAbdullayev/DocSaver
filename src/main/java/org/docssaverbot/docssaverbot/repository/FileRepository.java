package org.docssaverbot.docssaverbot.repository;

import org.docssaverbot.docssaverbot.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

    @Query(value = "select * from file f where f.folder_id= :folder_id and f.is_active = true",nativeQuery = true)
    List<File> findAllActiveFilesByFolderId(@Param("folder_id") UUID folder_id);

    @Query(value = "select * from file f where f.file_id= :file_id",nativeQuery = true)
    File findActiveFileByFileId(@Param("file_id") String file_id);

    @Query(value = "select * from file f where f.id= :id",nativeQuery = true)
    Optional<File> findById(@Param("id") UUID id);
}
