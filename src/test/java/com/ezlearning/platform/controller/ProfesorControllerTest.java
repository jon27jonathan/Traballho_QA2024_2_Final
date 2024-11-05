package com.ezlearning.platform.controller;

import com.ezlearning.platform.dto.ProfesotDto;
import com.ezlearning.platform.model.Curso;
import com.ezlearning.platform.model.Profesor;
import com.ezlearning.platform.repositories.CursoRepository;
import com.ezlearning.platform.repositories.ProfesorRepository;
import com.ezlearning.platform.services.core.impl.ProfesorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfesorControllerTest {

    @InjectMocks
    private ProfesorController profesorController;

    @Mock
    private ProfesorService profesorService;

    @Mock
    private ProfesorRepository profesorRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdicionarProfessor() {
        String view = profesorController.addProfesor(model);
        verify(model).addAttribute("profesor", new ProfesotDto());
        assertEquals("profesores/profesor-add", view);
    }

    @Test
    void testSalvarProfessor() {
        ProfesotDto profesorDto = new ProfesotDto();
        String view = profesorController.saveProfesor(profesorDto);
        verify(profesorService).create(profesorDto);
        assertEquals("redirect:/profesores", view);
    }

    @Test
    void testGetProfessorAtualizar() {
        Long id = 1L;
        Profesor profesor = new Profesor();
        when(profesorRepository.findById(id)).thenReturn(Optional.of(profesor));

        String view = profesorController.getProfesorForUpdate(id, model);
        verify(model).addAttribute("profesor", profesor);
        assertEquals("profesores/profesor-edit", view);
    }
    
    @Test
    void testGetListaVaziaProfessor() {
        List<Profesor> profesores = List.of();
        when(profesorService.getAll()).thenReturn(profesores);
        
        String view = profesorController.getProfesoresList(model);
        
        verify(model).addAttribute("profesores", profesores);
        assertEquals("profesores/profesores", view);
    }

    @Test
    void testAtualizarProfessor() {
        Long id = 1L;
        Profesor currentProfesor = new Profesor();
        currentProfesor.setNomProfesor("Joao");
        currentProfesor.setApeProfesor("Jo");

        when(profesorRepository.findById(id)).thenReturn(Optional.of(currentProfesor));
        
        Profesor updatedProfesor = new Profesor();
        updatedProfesor.setNomProfesor("Joana");
        updatedProfesor.setApeProfesor("Jo");
        
        String view = profesorController.updateProfesor(id, updatedProfesor, redirectAttributes, model);
        verify(profesorService).update(updatedProfesor);
        verify(redirectAttributes).addAttribute("id_profesor", id);
        assertEquals("redirect:/profesores/{id_profesor}", view);
    }

    @Test
    void testExcluirProfessor() {
        Long id = 1L;
        Profesor profesor = new Profesor();
        when(profesorRepository.findById(id)).thenReturn(Optional.of(profesor));

        String view = profesorController.deleteProfesor(id, model);
        verify(profesorService).delete(profesor);
        assertEquals("redirect:/profesores", view);
    }

    @Test
    void testGetListaProfessor() {
        List<Profesor> profesores = List.of(new Profesor(), new Profesor());
        when(profesorService.getAll()).thenReturn(profesores);
        
        String view = profesorController.getProfesoresList(model);
        
        verify(model).addAttribute("profesores", profesores);
        assertEquals("profesores/profesores", view);
    }
    
    @Test
    void testGetDetalhesProfessor() {
        Long id = 1L;
        Profesor profesor = new Profesor();
        when(profesorRepository.findById(id)).thenReturn(Optional.of(profesor));

        String view = profesorController.getProfesorDetail(id, model);

        verify(model).addAttribute("profesor", profesor);
        assertEquals("profesores/profesor-detail", view);
    }
    
    @Test
    void testPatchProfessor() {
        Long id = 1L;
        Profesor currentProfesor = new Profesor();
        currentProfesor.setDetalleProfesor("Detail");

        when(profesorRepository.findById(id)).thenReturn(Optional.of(currentProfesor));

        String view = profesorController.patchProfesor(id, currentProfesor, redirectAttributes, model);

        verify(profesorService).patch(currentProfesor);
        verify(redirectAttributes).addAttribute("id_profesor", id);
        assertEquals("redirect:/profesores/{id_profesor}", view);
    }
    
    @Test
    void testGetTamanhoListaProfessor() {
        List<Profesor> profesores = List.of(new Profesor(), new Profesor(), new Profesor()); // Three professors
        when(profesorService.getAll()).thenReturn(profesores);
        
        String view = profesorController.getProfesoresList(model);
        
        verify(model).addAttribute("profesores", profesores);
        assertEquals(3, profesores.size());
        assertEquals("profesores/profesores", view);
    }

}

