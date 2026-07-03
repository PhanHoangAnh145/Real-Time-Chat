package vn.edu.ptit.PhanHoangAnh.chat_realtime.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDTO {
    private Long id;
    private String groupName;
    private boolean isGroup;
    private List<UserDTO> users;
}
