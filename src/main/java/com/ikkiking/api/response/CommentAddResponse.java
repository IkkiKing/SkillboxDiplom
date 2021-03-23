package com.ikkiking.api.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentAddResponse {

    private Long id;

    private boolean result;

    private CommentAddError errors;

}
