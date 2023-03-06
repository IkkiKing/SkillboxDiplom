package com.ikkiking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Entity(name = "global_settings")
@AllArgsConstructor
@NoArgsConstructor
public class GlobalSettings {

    //test 7
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "global_settings.code may not be null")
    private String code;

    @NotNull(message = "global_settings.name may not be null")
    private String name;

    @NotNull(message = "global_settings.value may not be null")
    private String value;

}