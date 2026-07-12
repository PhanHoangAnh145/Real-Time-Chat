package vn.edu.ptit.PhanHoangAnh.chat_realtime.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private String content;
    private UserDTO user;
    private Long conversationId;
}
