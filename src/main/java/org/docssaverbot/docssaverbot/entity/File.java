package org.docssaverbot.docssaverbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fileId;

    private String extension;

    private UUID folderId;

    private Long chatId;

    private Timestamp registeredAt;

    private Boolean isActive;

}
