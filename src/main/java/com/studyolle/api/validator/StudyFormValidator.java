package com.studyolle.api.validator;

import com.studyolle.api.form.StudyForm;
import com.studyolle.domain.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return StudyForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm studyForm = (StudyForm) target;

        if (studyRepository.existsByPath(studyForm.getPath())) {
            errors.rejectValue("path", "wrong.value", "스터디 경로를 사용할 수 없습니다.");
        }
    }
}
