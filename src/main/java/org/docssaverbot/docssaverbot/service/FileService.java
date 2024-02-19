package org.docssaverbot.docssaverbot.service;

import org.docssaverbot.docssaverbot.dto.CodeMessage;
import org.docssaverbot.docssaverbot.entity.File;
import org.docssaverbot.docssaverbot.enums.CodeMessageType;
import org.docssaverbot.docssaverbot.enums.UserStatus;
import org.docssaverbot.docssaverbot.repository.FileRepository;
import org.docssaverbot.docssaverbot.util.ButtonUtil;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Component
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private StatusService statusService;

    public File save(File file){
        return this.fileRepository.save(file);
    }

    public List<File> getAllByFolderId(UUID folderId){
        return this.fileRepository.findAllActiveFilesByFolderId(folderId);
    }

    public Optional<File> getFileById(UUID id){
        return this.fileRepository.findById(id);
    }

    public File getByFileId(String fileId){
        return this.fileRepository.findActiveFileByFileId(fileId);
    }

    public List<Integer> extractNumbers(String input) {
        List<Integer> numbers = new ArrayList<>();
        String[] parts = input.split("\\s+"); // Splitting by spaces

        for (String part : parts) {
            int number = Integer.parseInt(part);
            numbers.add(number);
        }

        return numbers;
    }

    public CodeMessage addFile(Message message,String extension,String fileId){

        File file=new File();
        Long chatId = message.getChatId();
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(chatId);
        CodeMessage codeMessage=new CodeMessage();

        file.setChatId(chatId);
        file.setFolderId(statusService.getStatusByUserId(chatId).getFolderId());
        file.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
        file.setExtension(extension);
        file.setFileId(fileId);
        file.setIsActive(true);

        this.save(file);
        sendMessage.setText(extension.substring(0,1)+extension.substring(1).toLowerCase()+" added success!");
        sendMessage.setReplyMarkup(ButtonUtil.crudOperation());
        this.statusService.changeUserStatus(UserStatus.CHOOSING_CRUD_OPERATION.toString(), chatId);
        codeMessage.setSendMessage(sendMessage);
        codeMessage.setType(CodeMessageType.MESSAGE);

        return codeMessage;
    }

    public CodeMessage setCodeMessageField(UserStatus changeViewOrDeleteStatus,
                                            UserStatus changeUserStatus,
                                            CodeMessageType codeMessageType,
                                            Long chatId,
                                            SendMessage sendMessage,
                                            List<File> fileList,
                                            File file){
        CodeMessage codeMessage=new CodeMessage();

        codeMessage.setFileList(fileList);
        codeMessage.setFile(file);
        codeMessage.setSendMessage(sendMessage);
        codeMessage.setType(codeMessageType);
        codeMessage.setChatId(chatId);

        this.statusService.changeViewOrDeleteStatus(changeViewOrDeleteStatus.toString(), chatId);
        this.statusService.changeUserStatus(changeUserStatus.toString(), chatId);
        return codeMessage;
    }


}
