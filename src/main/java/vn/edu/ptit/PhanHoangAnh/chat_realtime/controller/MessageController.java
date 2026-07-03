package vn.edu.ptit.PhanHoangAnh.chat_realtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.MessageDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.MessageRequestDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.helper.ApiResponse;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController (MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageDTO>> findMessageById (@PathVariable Long id) {
        return ApiResponse.success(this.messageService.findMessageById(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageDTO>>> findAllMessage() {
        return ApiResponse.success(this.messageService.findAllMessage());
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<MessageDTO>> saveMessage(@RequestBody MessageRequestDTO requestDTO) {
        return ApiResponse.created(this.messageService.saveMessage(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageDTO>> updateMessageById(@PathVariable Long id, @RequestBody MessageDTO messageDTO) {
        return ApiResponse.success(this.messageService.updateMessageById(id, messageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMessageById(@PathVariable Long id) {
        this.messageService.deleteMessageById(id);
        return ApiResponse.success("xoa thanh cong");
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> findMessageByConversationId(@PathVariable Long conversationId) {
        return ApiResponse.success(this.messageService.findMessageByConversationId(conversationId));
    }
}
