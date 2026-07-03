package vn.edu.ptit.PhanHoangAnh.chat_realtime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dao.ConversationRepository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dao.MessageRepository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dao.UserRepository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.MessageDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.MessageRequestDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.Conversation;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.Message;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.mapper.MessageMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    @Autowired
    public MessageServiceImpl (MessageRepository messageRepository, MessageMapper messageMapper, UserRepository userRepository, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
    }

    @Override
    public MessageDTO findMessageById(Long id) {
        return this.messageMapper.toDTO(this.messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid")));
    }

    @Override
    public List<MessageDTO> findAllMessage() {
        List<Message> messages = this.messageRepository.findAll();
        return messages.stream()
                .map(message -> this.messageMapper.toDTO(message))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageDTO saveMessage(MessageRequestDTO requestDTO) {
        User user = this.userRepository.findById(requestDTO.getUserId()).orElseThrow(() -> new RuntimeException("Invalid"));
        Conversation conversation = this.conversationRepository.findById(requestDTO.getConversationId()).orElseThrow(() -> new RuntimeException("Invalid"));
        Message message = new Message();
        user.addMessage(message);
        conversation.addMessage(message);
        message.setContent(requestDTO.getContent());

        return this.messageMapper.toDTO(this.messageRepository.save(message));
    }

    @Override
    @Transactional
    public MessageDTO updateMessageById(Long id, MessageDTO messageDTO) {
        Message message = this.messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid"));
        message.setContent(messageDTO.getContent());

        return this.messageMapper.toDTO(this.messageRepository.saveAndFlush(message));
    }

    @Override
    @Transactional
    public void deleteMessageById(Long id) {
        Message message = this.messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid"));
        this.messageRepository.delete(message);
    }

    @Override
    public List<MessageDTO> findMessageByConversationId(Long conversationId) {
        return this.messageRepository.findByConversationId(conversationId)
                .stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
    }
}
