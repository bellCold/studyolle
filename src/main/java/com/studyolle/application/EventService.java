package com.studyolle.application;

import com.studyolle.domain.account.Account;
import com.studyolle.domain.event.Event;
import com.studyolle.domain.event.EventRepository;
import com.studyolle.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Study study, Account account) {
        event.addData(study, account);
        return eventRepository.save(event);
    }

    public Event findEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(RuntimeException::new);
    }
}
