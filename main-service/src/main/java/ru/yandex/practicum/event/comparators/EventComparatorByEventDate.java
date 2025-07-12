package ru.yandex.practicum.event.comparators;

import ru.yandex.practicum.event.model.Event;

import java.util.Comparator;

public class EventComparatorByEventDate implements Comparator<Event> {
    @Override
    public int compare(Event o1, Event o2) {
        return o1.getEventDate().compareTo(o2.getEventDate());
    }
}