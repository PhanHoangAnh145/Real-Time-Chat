package vn.edu.ptit.PhanHoangAnh.chat_realtime.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationCreateRequest {
    private String groupName;
    private boolean isGroup;
    private List<Long> userIds;
}
