package com.example.paygate.merchants;

import com.example.paygate.customers.Customer;
import com.example.paygate.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "merchants")
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

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "webhook_active")
    private Boolean webhookActive = true;

    @ManyToMany(mappedBy = "merchants")
    private Set<Customer> customers = new HashSet<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public void generateApiKey() {
        this.apiKey = "pl_" + UUID.randomUUID().toString().replace("-", "");
    }

    public void generateSecretKey() {
        this.secretKey = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "Merchant(" +
                "id=" + id +
                ", name=" + name +
                ", apiKey=" + apiKey +
                ", userId=" + (user != null ? user.getId() : null) +
                ", webhookUrl=" + webhookUrl +
                ", webhookActive=" + webhookActive +
                ")";
    }
}
