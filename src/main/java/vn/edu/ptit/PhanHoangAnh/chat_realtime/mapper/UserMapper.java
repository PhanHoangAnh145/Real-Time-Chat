package vn.edu.ptit.PhanHoangAnh.chat_realtime.mapper;

import org.springframework.stereotype.Component;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.UserDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        if (user == null)
            return null;

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
