package ru.yandex.practicum.compilation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.compilation.dao.CompilationRepository;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.event.dao.EventRepository;
import ru.yandex.practicum.event.service.StatsService;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.dao.RequestRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
public class CompilationServiceImplTest {

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private CompilationServiceImpl service;

    private static final long NON_EXISTING_EVENT_ID = 2L;
    private static final long EXISTING_COMP_ID = 101L;
    private static final long NON_EXISTING_COMP_ID = 102L;

    @BeforeEach
    void setup() {
        Mockito.reset(compilationRepository, eventRepository, requestRepository, statsService);
    }

    @Test
    void add_missingEvent() {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setEvents(List.of(NON_EXISTING_EVENT_ID));

        when(eventRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> service.add(dto));
        verify(compilationRepository, never()).save(any());
    }

    @Test
    void add_emptyEvents() {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setEvents(Collections.emptyList());

        Compilation saved = new Compilation();
        when(compilationRepository.save(any())).thenReturn(saved);

        CompilationDto result = service.add(dto);
        assertEquals(0, result.getEvents().size());
    }

    @Test
    void update_notFound() {
        assertThrows(NotFoundException.class, () -> service.update(NON_EXISTING_COMP_ID, new UpdateCompilationRequest()));
        verify(compilationRepository, never()).save(any());
    }

    @Test
    void delete_valid() {
        when(compilationRepository.findById(EXISTING_COMP_ID)).thenReturn(Optional.of(new Compilation()));

        service.delete(EXISTING_COMP_ID);

        verify(compilationRepository).delete(any());
    }

    @Test
    void getCompilationById_notFound() {
        assertThrows(NotFoundException.class, () -> service.getCompilationById(NON_EXISTING_COMP_ID));
    }
}