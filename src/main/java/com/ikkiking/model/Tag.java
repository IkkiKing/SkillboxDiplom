package com.ikkiking.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "tags.name may not be null")
    private String name;
}
