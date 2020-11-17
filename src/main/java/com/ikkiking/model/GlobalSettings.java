package com.ikkiking.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity(name = "global_settings")
public class GlobalSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "global_settings.code may not be null")
    private String code;

    @NotNull(message = "global_settings.name may not be null")
    private String name;

    @NotNull(message = "global_settings.value may not be null")
    private String value;


}