package com.example.paygate.merchants;

import com.example.paygate.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@Entity
@Getter
@Setter
@Builder
@Table(name = "merchants")
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "api_key")
    private String apiKey;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;


    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "apiKey = " + apiKey + ", " +
                "userId = " + user.getId() + ")";
    }
}
