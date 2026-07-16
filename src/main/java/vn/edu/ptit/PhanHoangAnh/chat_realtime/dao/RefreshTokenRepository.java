package vn.edu.ptit.PhanHoangAnh.chat_realtime.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
