package com.ikkiking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Data
@Entity(name = "tag2post")
@AllArgsConstructor
@NoArgsConstructor
public class Tag2Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "tag2post.post_id may not be null")
    @Column(name = "post_id")
    private Long postId;


    @NotNull(message = "tag2post.tag_id may not be null")
    @Column(name = "tag_id")
    private Long tagId;
}
