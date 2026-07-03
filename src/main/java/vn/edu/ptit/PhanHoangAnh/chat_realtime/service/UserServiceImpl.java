package vn.edu.ptit.PhanHoangAnh.chat_realtime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dao.UserRepository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.UserDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl (UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO findUserByUsername(String username) {
        return this.userMapper.toDTO(this.userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Invalid username")));
    }

    @Override
    public UserDTO findUserById(Long id) {
        return this.userMapper.toDTO(this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid id")));
    }

    @Override
    public List<UserDTO> findAllUser() {
        return this.userRepository.findAll()
                .stream()
                .map( user -> userMapper.toDTO(user))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO saveUser(User user) {
        return this.userMapper.toDTO(this.userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO updateUserById(Long id, User user) {
        User userDb = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid id"));
        userDb.setUsername(user.getUsername());
        userDb.setPassword(user.getPassword());
        userDb.setAvatar(user.getAvatar());

        return this.userMapper.toDTO(this.userRepository.saveAndFlush(userDb));
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User userDb = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid id"));
        this.userRepository.delete(userDb);
    }
}
