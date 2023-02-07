package com.studyolle.api;

import com.studyolle.api.form.EventForm;
import com.studyolle.api.validator.EventValidator;
import com.studyolle.application.EventService;
import com.studyolle.application.StudyService;
import com.studyolle.domain.account.Account;
import com.studyolle.domain.event.Event;
import com.studyolle.domain.study.Study;
import com.studyolle.global.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final EventService eventService;
    private final EventValidator eventValidator;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new EventForm());

        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(
            @CurrentUser Account account,
            @PathVariable String path,
            @Valid EventForm eventForm, Errors errors,
            Model model
    ) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), study, account);

        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String getEvent(
            @CurrentUser Account account,
            @PathVariable String path,
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute(account);
        model.addAttribute(eventService.findEvent(id));
        model.addAttribute(studyService.getStudy(path));
        return "event/view";
    }
}
