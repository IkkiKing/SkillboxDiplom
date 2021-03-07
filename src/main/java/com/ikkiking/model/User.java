package com.ikkiking.model;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "users.is_moderator may not be null")
    @Column(name = "is_moderator")
    private boolean isModerator;

    @NotNull(message = "users.reg_time may not be null")
    @Column(name = "reg_time")
    private Date regTime;

    @NotNull(message = "users.name may not be null")
    private String name;

    @NotNull(message = "users.email may not be null")
    private String email;

    @NotNull(message = "users.password may not be null")
    @Column(nullable = false, length = 255)
    private String password;

    private String code;

    private String photo;

    public Role getRole() {
        return isModerator ? Role.MODERATOR : Role.USER;
    }
}
