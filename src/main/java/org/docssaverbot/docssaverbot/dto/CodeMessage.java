package org.docssaverbot.docssaverbot.dto;

import lombok.Data;
import org.docssaverbot.docssaverbot.entity.File;
import org.docssaverbot.docssaverbot.enums.CodeMessageType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.List;

@Data
@Component
public class CodeMessage {

    private CodeMessageType type;

    private EditMessageText editMessageText;

    private SendMessage sendMessage;

    private SendPhoto sendPhoto;

    private SendDocument sendDocument;

    private SendAudio sendAudio;

    private SendVideo sendVideo;

    private SendVoice sendVoice;

    private List<File> fileList;

    private File file;

    private Long chatId;

    private int count;

}
