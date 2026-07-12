package vn.edu.ptit.PhanHoangAnh.chat_realtime.service;

import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.UserDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;

import java.util.List;

public interface UserService {
    User findUserByUsername (String username);
    UserDTO findUserById(Long id);
    List<UserDTO> findAllUser();
    UserDTO saveUser(User user);
    UserDTO updateUserById(Long id, User user);
    void deleteUserById(Long id);
}
