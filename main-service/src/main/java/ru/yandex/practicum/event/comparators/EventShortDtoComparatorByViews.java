package ru.yandex.practicum.event.comparators;

import ru.yandex.practicum.event.dto.get.EventShortDto;

import java.util.Comparator;

public class EventShortDtoComparatorByViews implements Comparator<EventShortDto> {
    @Override
    public int compare(EventShortDto o1, EventShortDto o2) {
        return (int) (o2.getViews() - o1.getViews());
    }
}
