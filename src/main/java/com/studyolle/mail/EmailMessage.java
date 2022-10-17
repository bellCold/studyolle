package com.studyolle.mail;

import lombok.*;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class EmailMessage {
    private String to;
    private String subject;
    private String message;
}
