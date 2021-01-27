package com.ikkiking.api.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileErrorResponse {
    private String email;
    private String photo;
    private String name;
    private String password;
}
