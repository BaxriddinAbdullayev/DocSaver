package org.docssaverbot.docssaverbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.UUID;

@Entity(name = "status")
@Data
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long chatId;

    private UUID folderId;

    private UUID fileId;

    private String userStatus;

    private String lastSendMessage;

    private String viewOrDelete;

}
