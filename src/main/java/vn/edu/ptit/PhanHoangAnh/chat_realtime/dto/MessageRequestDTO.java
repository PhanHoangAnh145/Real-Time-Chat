package vn.edu.ptit.PhanHoangAnh.chat_realtime.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequestDTO {
    private String content;
    private Long userId;
    private Long conversationId;
}
