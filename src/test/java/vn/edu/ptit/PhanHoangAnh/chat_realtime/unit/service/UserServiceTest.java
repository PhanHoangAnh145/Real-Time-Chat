package vn.edu.ptit.PhanHoangAnh.chat_realtime.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dao.UserRepository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.UserDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.mapper.UserMapper;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.UserService;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UserDTO mockUserDTO;

    private User inputUser;
    private User savedUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("phanhoanganh");

        mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);
        mockUserDTO.setUsername("phanhoanganh");

        inputUser = new User();
        inputUser.setUsername("phanhoanganh");
        inputUser.setPassword("123456");

        savedUser = new User();
        savedUser.setUsername("phanhoanganh");
        savedUser.setPassword("$2a$12$sYRhJ.wEBGXlLiKCRe79K.kHZjQlZlm45Qm6kHRpKRqpSUN1PWld.");
    }

    @Test
    void testFindAllUser_ShouldReturnListOf() {
        // Dàn xếp Repository
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        when(userMapper.toDTO(mockUser)).thenReturn(mockUserDTO);

        List<UserDTO> users = this.userService.findAllUser();

        assertEquals(1, users.size());
        assertEquals("phanhoanganh", users.get(0).getUsername());

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDTO(mockUser); // Kiểm tra xem có gọi mapper không
    }

    @Test
    void testFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDTO(mockUser)).thenReturn(mockUserDTO);

        UserDTO userDTO = this.userService.findUserById(1L);

        assertNotNull(userDTO);
        assertEquals("phanhoanganh", userDTO.getUsername());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toDTO(mockUser); // Kiểm tra xem có gọi mapper không
    }

    @Test
    void testFindById_ShouldThrowNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findUserById(2L));

        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void testSaveUser_ShouldReturnUser() {
        when(passwordEncoder.encode("123456")).thenReturn("$2a$12$sYRhJ.wEBGXlLiKCRe79K.kHZjQlZlm45Qm6kHRpKRqpSUN1PWld.");
        when(userRepository.save(inputUser)).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(mockUserDTO);

        UserDTO userDTO = this.userService.saveUser(inputUser);

        assertNotNull(userDTO);
        assertEquals("phanhoanganh", savedUser.getUsername());
        assertEquals("$2a$12$sYRhJ.wEBGXlLiKCRe79K.kHZjQlZlm45Qm6kHRpKRqpSUN1PWld.", savedUser.getPassword());
        assertEquals("phanhoanganh", mockUser.getUsername());
        assertEquals(1L, mockUser.getId());

        verify(passwordEncoder, times(1)).encode("123456");
        verify(userRepository, times(1)).save(inputUser);
        verify(userMapper, times(1)).toDTO(savedUser);
    }
}