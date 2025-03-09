package org.esimulate.frame.model;

import javax.persistence.*;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JSONField(serialize = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JSONField(serialize = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @JSONField(serialize = false)
    private Timestamp updatedAt;

}
