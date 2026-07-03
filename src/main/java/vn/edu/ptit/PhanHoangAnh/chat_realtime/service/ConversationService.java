package vn.edu.ptit.PhanHoangAnh.chat_realtime.service;

import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationCreateRequest;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationUpdateRequest;

import java.util.List;

public interface ConversationService {
    ConversationDTO findConversationById(Long id);
    List<ConversationDTO> findAllConversation();
    ConversationDTO saveConversation(ConversationCreateRequest request);
    ConversationDTO updateConversationById(Long id, ConversationUpdateRequest request);
    ConversationDTO addUserToConversation(Long conversationId, Long userId);
    ConversationDTO removeUserFromConversation(Long conversationId, Long userId);
    void deleteConversationById(Long id);
}
