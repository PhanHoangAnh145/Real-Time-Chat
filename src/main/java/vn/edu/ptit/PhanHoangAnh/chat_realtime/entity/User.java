package vn.edu.ptit.PhanHoangAnh.chat_realtime.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Lob
    @Column(name = "avatar")
    private Blob avatar;

    @ManyToMany(mappedBy = "users")
    private List<Conversation> conversations;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Message> messages;

    @OneToMany(mappedBy = "user")
    private List<RefreshToken> refreshTokens;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable (
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    public void addMessage (Message message) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(message);
        message.setUser(this);
    }

}
