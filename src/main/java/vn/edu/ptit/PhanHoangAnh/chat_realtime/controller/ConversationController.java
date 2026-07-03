package vn.edu.ptit.PhanHoangAnh.chat_realtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationCreateRequest;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationUpdateRequest;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.helper.ApiResponse;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.ConversationService;

import java.util.List;

@RestController
@RequestMapping("/api/conversation")
public class ConversationController {

    private final ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConversationDTO>> findConversationById(@PathVariable Long id) {
        return ApiResponse.success(this.conversationService.findConversationById(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConversationDTO>>> findAllConversation() {
        return ApiResponse.success(this.conversationService.findAllConversation());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConversationDTO>> saveConversation(@RequestBody ConversationCreateRequest request) {
        return ApiResponse.created(this.conversationService.saveConversation(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConversationDTO>> updateConversationById(@PathVariable Long id,
                                                                               @RequestBody ConversationUpdateRequest request) {
        return ApiResponse.success(this.conversationService.updateConversationById(id, request));
    }

    @PostMapping("/{conversationId}/user/{userId}")
    public ResponseEntity<ApiResponse<ConversationDTO>> addUserToConversation(@PathVariable Long conversationId,
                                                                              @PathVariable Long userId) {
        return ApiResponse.success(this.conversationService.addUserToConversation(conversationId, userId));
    }

    @DeleteMapping("/{conversationId}/user/{userId}")
    public ResponseEntity<ApiResponse<ConversationDTO>> removeUserFromConversation(@PathVariable Long conversationId,
                                                                                   @PathVariable Long userId) {
        return ApiResponse.success(this.conversationService.removeUserFromConversation(conversationId, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteConversationById(@PathVariable Long id) {
        this.conversationService.deleteConversationById(id);
        return ApiResponse.success("delete success...");
    }


}