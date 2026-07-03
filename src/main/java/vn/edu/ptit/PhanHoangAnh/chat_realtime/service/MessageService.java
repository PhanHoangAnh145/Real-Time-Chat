package vn.edu.ptit.PhanHoangAnh.chat_realtime.service;



import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.MessageDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.MessageRequestDTO;

import java.util.List;

public interface MessageService {
    MessageDTO findMessageById(Long id);
    List<MessageDTO> findAllMessage();
    List<MessageDTO> findMessageByConversationId(Long conversationId);
    MessageDTO saveMessage(MessageRequestDTO requestDTO);
    MessageDTO updateMessageById(Long id, MessageDTO messageDTO);
    void deleteMessageById(Long id);


}
