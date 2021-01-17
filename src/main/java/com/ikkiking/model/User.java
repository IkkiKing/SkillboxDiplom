package com.ikkiking.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public Role getRole(){
        return isModerator ? Role.MODERATOR : Role.USER;
    }
}
