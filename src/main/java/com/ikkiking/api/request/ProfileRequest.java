package com.ikkiking.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String photo;
    private String name;
    private String email;
    private String password;
    private Integer removePhoto;
}
