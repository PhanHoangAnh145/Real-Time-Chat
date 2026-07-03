package vn.edu.ptit.PhanHoangAnh.chat_realtime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dao.ConversationRepository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dao.UserRepository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationCreateRequest;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ConversationUpdateRequest;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.Conversation;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.mapper.ConversationMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;

    @Autowired
    public ConversationServiceImpl(ConversationRepository conversationRepository,
                                   UserRepository userRepository,
                                   ConversationMapper conversationMapper) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.conversationMapper = conversationMapper;
    }

    @Override
    public ConversationDTO findConversationById(Long id) {
        return this.conversationMapper.toDTO(
                this.conversationRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Invalid")));
    }

    @Override
    public List<ConversationDTO> findAllConversation() {
        return this.conversationRepository.findAll()
                .stream()
                .map(conversationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConversationDTO saveConversation(ConversationCreateRequest request) {
        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
            throw new RuntimeException("Invalid");
        }

        List<User> users = this.userRepository.findAllById(request.getUserIds());
        if (users.size() != request.getUserIds().size()) {
            throw new RuntimeException("Invalid");
        }

        Conversation conversation = new Conversation();
        conversation.setGroupName(request.getGroupName());
        conversation.setGroup(request.isGroup());
        conversation.setUsers(users);

        return this.conversationMapper.toDTO(this.conversationRepository.save(conversation));
    }

    @Override
    @Transactional
    public ConversationDTO updateConversationById(Long id, ConversationUpdateRequest request) {
        Conversation conversationDb = this.conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid"));

        conversationDb.setGroupName(request.getGroupName());

        return this.conversationMapper.toDTO(this.conversationRepository.saveAndFlush(conversationDb));
    }

    @Override
    @Transactional
    public ConversationDTO addUserToConversation(Long conversationId, Long userId) {
        Conversation conversation = this.conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Invalid"));
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Invalid"));

        if (!conversation.getUsers().contains(user)) {
            conversation.getUsers().add(user);
        }

        return this.conversationMapper.toDTO(this.conversationRepository.saveAndFlush(conversation));
    }

    @Override
    @Transactional
    public ConversationDTO removeUserFromConversation(Long conversationId, Long userId) {
        Conversation conversation = this.conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Invalid"));

        conversation.getUsers().removeIf(u -> u.getId().equals(userId));

        return this.conversationMapper.toDTO(this.conversationRepository.saveAndFlush(conversation));
    }

    @Override
    @Transactional
    public void deleteConversationById(Long id) {
        Conversation conversationDb = this.conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid"));
        this.conversationRepository.delete(conversationDb);
    }
}