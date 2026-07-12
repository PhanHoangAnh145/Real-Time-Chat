package vn.edu.ptit.PhanHoangAnh.chat_realtime.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.MessageDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.Message;

@Component
public class MessageMapper {

    private final UserMapper userMapper;

    @Autowired
    public MessageMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public MessageDTO toDTO (Message message) {
        if (message == null) return null;
        
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .user(message.getUser() != null ? userMapper.toDTO(message.getUser()) : null)
                .conversationId(message.getConversation() != null ? message.getConversation().getId() : null)
                .build();
    }
}
