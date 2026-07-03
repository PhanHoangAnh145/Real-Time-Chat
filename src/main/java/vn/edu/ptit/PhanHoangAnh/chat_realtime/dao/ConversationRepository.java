package vn.edu.ptit.PhanHoangAnh.chat_realtime.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
