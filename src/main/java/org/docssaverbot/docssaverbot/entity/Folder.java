package org.docssaverbot.docssaverbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity(name = "folder")
@Data
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long chatId;

    private String folderName;

    private Timestamp registeredAt;

    private Timestamp changedAt;

    private Boolean isActive;

}
