package com.studyolle.api.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class StudyDescriptionForm {

    @NotBlank
    @Length(max = 300)
    private String shortDescription;

    @NotBlank
    private String fullDescription;
}
