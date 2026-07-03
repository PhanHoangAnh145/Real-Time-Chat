package vn.edu.ptit.PhanHoangAnh.chat_realtime.mapper;

import org.springframework.stereotype.Component;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.MessageDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.Message;

@Component
public class MessageMapper {
    public MessageDTO toDTO (Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .content(message != null ? message.getContent() : null)
                .build();
    }
}
