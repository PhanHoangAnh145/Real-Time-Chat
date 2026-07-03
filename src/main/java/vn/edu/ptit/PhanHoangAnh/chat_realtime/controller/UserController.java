package vn.edu.ptit.PhanHoangAnh.chat_realtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.UserDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.helper.ApiResponse;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController (UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> findUserById (@PathVariable Long id) {
        return ApiResponse.success(this.userService.findUserById(id));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserDTO>>> findAllUser () {
        List<UserDTO> userDTOS = this.userService.findAllUser();
        return ApiResponse.success(userDTOS);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> saveUser (@RequestBody User user) {
        return ApiResponse.created(this.userService.saveUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserById (@PathVariable Long id, @RequestBody User user) {
        return ApiResponse.success(this.userService.updateUserById(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUserById (@PathVariable Long id) {
        this.userService.deleteUserById(id);
        return ApiResponse.success("delete success...");
    }

    @GetMapping(params = "username")
    public ResponseEntity<ApiResponse<UserDTO>> findUserByUsername (@RequestParam String username) {
        return ApiResponse.success(this.userService.findUserByUsername(username));
    }
}
