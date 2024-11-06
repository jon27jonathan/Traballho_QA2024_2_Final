package com.ezlearning.platform.services.core.impl;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ezlearning.platform.auth.User;
import com.ezlearning.platform.auth.UserRepository;
import com.ezlearning.platform.model.Curso;
import com.ezlearning.platform.model.Matricula;
import com.ezlearning.platform.repositories.CursoRepository;
import com.ezlearning.platform.repositories.MatriculaRepository;
import com.ezlearning.platform.services.core.impl.MatriculaService;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@DisplayName("Testes Matricula Service")
public class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MatriculaService matriculaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @DisplayName("Usuario tenta se matricular num curso ja matriculado")
    @Test
    public void testCreateMatricula_DuplicateEnrollment_ThrowsException() {
        Long cursoId = 1L;
        String username = "dummy1";
        Curso curso = new Curso();
        User user = new User();
        Matricula existingMatricula = new Matricula(LocalDate.now(), user, curso);

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(curso));
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(matriculaRepository.findByCursoAndUsuario(curso, user)).thenReturn(existingMatricula);

        Exception exception = assertThrows(Exception.class, () -> {
            matriculaService.createMatricula(cursoId, username);
        });

        assertEquals("Ya se encuentra matriculado en este curso", exception.getMessage());
    }

    @DisplayName("Criacao bem sucedida d")
    @Test
    public void testCreateMatricula_ValidEnrollment_SavesMatricula() throws Exception {
        Long cursoId = 1L;
        String username = "dummy2";
        Curso curso = new Curso();
        User user = new User();

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(curso));
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(matriculaRepository.findByCursoAndUsuario(curso, user)).thenReturn(null);

        matriculaService.createMatricula(cursoId, username);

        verify(matriculaRepository, times(1)).save(any(Matricula.class));
    }
    
    
    @DisplayName("Curso não encontrado")
    @Test
    public void testCreateMatricula_CourseNotFound_ThrowsNoSuchElementException() {
        Long cursoId = 1L;
        String username = "dummy3";

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.empty());

        // aqui tive que trocar pra esse exception especifico (noSuchElementException)
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            matriculaService.createMatricula(cursoId, username);
        });

        // verifica a mensagem, se aplicável
        assertEquals("No value present", exception.getMessage());
    }
    
}
