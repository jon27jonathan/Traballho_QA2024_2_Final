package com.ezlearning.platform.services.core.impl;

import com.ezlearning.platform.dto.CursoDto;
import com.ezlearning.platform.model.Curso;
import com.ezlearning.platform.model.Profesor;
import com.ezlearning.platform.repositories.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CursoServiceTest {
 
    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    private CursoDto cursoDto;
    private Curso curso;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Profesor professor = new Profesor();
        cursoDto = new CursoDto("Curso1", "Desc1", "Detalhe1", "Facil", "url1", "img1", professor);
        curso = new Curso("Curso1", "Desc1", "Detalhe1", "Facil", "url1", "img1", professor);
    }
    
    @Test
    void testAdicionarCursoValido() throws Exception {
        CursoDto cursoDto = new CursoDto();
        cursoDto.setNomCurso("Curso de Teste");
        cursoDto.setDescCurso("Descrição do curso");
        cursoDto.setDetalle("Detalhes do curso");
        cursoDto.setDificultad("Intermedio");
        cursoDto.setUrl("http://example.com");
        cursoDto.setImgurl("http://example.com/image.jpg");
        cursoDto.setProfesor(new Profesor());

        when(cursoRepository.findByNomCurso("Curso de Teste")).thenReturn(null);

        cursoService.create(cursoDto);

        verify(cursoRepository).save(any(Curso.class));
    }


    @Test
    void testCursoDuplicado() {
        when(cursoRepository.findByNomCurso(cursoDto.getNomCurso())).thenReturn(curso);

        Exception exception = assertThrows(Exception.class, () -> {
            cursoService.create(cursoDto);
        });

        assertEquals("Ya existe un curso con el nombre Curso1", exception.getMessage());
        verify(cursoRepository, never()).save(any(Curso.class));
    }
    
    @Test
    void testAdicionarCursoNulo() {
        cursoDto.setNomCurso(null);

        Exception exception = assertThrows(Exception.class, () -> {
            cursoService.create(cursoDto);
        });

        assertEquals("Nome do curso não pode ser nulo", exception.getMessage());
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void testAdicionarCursoDescricaoNula() {
    	cursoDto.setDescCurso(null);
    	
    	Exception exception = assertThrows(Exception.class, () -> {
    		cursoService.create(cursoDto);
    	});
    	
    	assertEquals("Descrição do curso não pode ser nula", exception.getMessage());
    	verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void testAtualizar() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        Curso updatedCurso = new Curso("Curso1 Updated", "Desc1 Updated", "Detalle1 Updated", "Dificultad Updated", "url1", "img1", curso.getProfesor());
        cursoService.update(updatedCurso, 1L);

        verify(cursoRepository, times(1)).save(curso);
        assertEquals("Curso1 Updated", curso.getNomCurso());
    }
    
    @Test
    void testAtualizarCursoNaoEncontrado() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            cursoService.update(curso, 1L);
        });

        assertEquals("No value present", exception.getMessage());
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void testExcluir() {
        cursoService.delete(curso);

        verify(cursoRepository, times(1)).delete(curso);
    }
    
    @Test
    void testExcluirCursoInexistente() {
        doThrow(new NoSuchElementException("Curso não encontrado")).when(cursoRepository).delete(curso);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            cursoService.delete(curso);
        });

        assertEquals("Curso não encontrado", exception.getMessage());
        verify(cursoRepository, times(1)).delete(curso);
    }

    @Test
    void testListarTodos() {
        List<Curso> cursos = new ArrayList<>();
        cursos.add(curso);
        when(cursoRepository.findAll()).thenReturn(cursos);

        List<Curso> result = cursoService.getAll();

        assertEquals(1, result.size());
        assertEquals(curso, result.get(0));
    }
    
    @Test
    void testListarTodosSemCursos() {
        when(cursoRepository.findAll()).thenReturn(new ArrayList<>());

        List<Curso> result = cursoService.getAll();

        assertTrue(result.isEmpty());
    }

}
