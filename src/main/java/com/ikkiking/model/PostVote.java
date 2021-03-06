package com.ikkiking.model;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "post_votes.user_id may not be null")
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @NotNull(message = "post_votes.post_id may not be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull(message = "post_votes.time may not be null")
    private Date time;

    @NotNull(message = "post_votes.value may not be null")
    private Integer value;

}
