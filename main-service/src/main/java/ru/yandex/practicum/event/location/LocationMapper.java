package ru.yandex.practicum.event.location;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LocationMapper {
    public LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }

    public Location toLocation(LocationDto locationDto, long id) {
        return new Location(id, locationDto.getLat(), locationDto.getLon());
    }
}
