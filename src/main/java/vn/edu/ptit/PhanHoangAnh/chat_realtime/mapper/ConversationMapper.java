package vn.edu.ptit.PhanHoangAnh.chat_realtime.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.MessageDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.UserDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.Conversation;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConversationMapper {

    private final UserMapper userMapper;
    private final MessageMapper messageMapper;

    @Autowired
    public ConversationMapper(UserMapper userMapper, MessageMapper messageMapper) {
        this.userMapper = userMapper;
        this.messageMapper = messageMapper;
    }

    public ConversationDTO toDTO(Conversation conversation) {
        if (conversation == null)
            return null;

        List<UserDTO> userDTOs =
                conversation.getUsers() == null
                        ? List.of()
                        : conversation.getUsers().stream()
                        .map(userMapper::toDTO)
                        .collect(Collectors.toList());

        List<MessageDTO> messageDTOs =
                conversation.getMessages() == null
                        ? List.of()
                        : conversation.getMessages().stream()
                        .map(messageMapper::toDTO)
                        .collect(Collectors.toList());

        return ConversationDTO.builder()
                .id(conversation.getId())
                .groupName(conversation.getGroupName())
                .isGroup(conversation.isGroup())
                .users(userDTOs)
                .messages(messageDTOs)
                .build();
    }
}
