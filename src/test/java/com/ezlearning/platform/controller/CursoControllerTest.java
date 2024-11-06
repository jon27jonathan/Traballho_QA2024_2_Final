package com.ezlearning.platform.controller;
import java.util.List;
import java.util.Arrays;

import com.ezlearning.platform.auth.User;
import com.ezlearning.platform.auth.UserRepository;
import com.ezlearning.platform.dto.CursoDto;
import com.ezlearning.platform.model.Curso;
import com.ezlearning.platform.model.Profesor;
import com.ezlearning.platform.repositories.CursoRepository;
import com.ezlearning.platform.repositories.MatriculaRepository;
import com.ezlearning.platform.repositories.ProfesorRepository;
import com.ezlearning.platform.services.core.impl.CursoService;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import java.util.Optional;


public class CursoControllerTest {
   @Mock
   private CursoService cursoService;
   @Mock
   private CursoRepository cursoRepository;
   @Mock
   private ProfesorRepository profesorRepository;
   @Mock
   private MatriculaRepository matriculaRepository;
   @Mock
   private UserRepository userRepository;
   @Mock
   private Model model;
   
   @Mock
   private Authentication authentication;

   
   @InjectMocks
   private CursoController cursoController;
   @BeforeEach
   public void setup() {
       MockitoAnnotations.openMocks(this);
       cursoController = new CursoController(cursoService, cursoRepository, matriculaRepository, userRepository, profesorRepository);
       
   }
   @DisplayName("Teste de Adição de Novo Curso a um Professor Específico")
   @Test
   public void testAddCursoToSpecificProfessor() throws Exception {
       Long professorId = 1L;
       Profesor professor = new Profesor();
       professor.setId_profesor(professorId);

       CursoDto cursoDto = new CursoDto();
       cursoDto.setNomCurso("Curso de Teste");

       // Configura o mock para retornar o professor existente
       when(profesorRepository.findById(professorId)).thenReturn(Optional.of(professor));

       // Configura o mock para a criação do curso
       doNothing().when(cursoService).create(cursoDto);

       // Executa o método de adição de curso
       String viewName = cursoController.saveCurso(professorId, cursoDto, model);

       // Verifica se o redirecionamento é o esperado
       assertEquals("redirect:/cursos", viewName);

       // Verifica se o método create foi chamado uma vez com o curso correto
       verify(cursoService, times(1)).create(cursoDto);
   }

   @DisplayName("Teste de Exclusão de Curso com ID Existente")
   @Test
   public void testDeleteCurso() throws Exception {
       Long idCurso = 1L;
       Curso curso = new Curso();
       curso.setId_curso(idCurso);
       // Simular o comportamento do cursoRepository e cursoService
       when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso));
       doNothing().when(cursoService).delete(any(Curso.class));
       String viewName = cursoController.deleteCurso(idCurso, model);
       assertEquals("redirect:/cursos", viewName);
       verify(cursoService, times(1)).delete(any(Curso.class));
   }
   @DisplayName("Teste de Listagem de Cursos")
   @Test
   public void testGetCursosList() {
       Curso curso1 = new Curso();
       curso1.setId_curso(1L);
       curso1.setNomCurso("Curso 1");

       Curso curso2 = new Curso();
       curso2.setId_curso(2L);
       curso2.setNomCurso("Curso 2");

       List<Curso> cursos = Arrays.asList(curso1, curso2);

       // Configura o mock para retornar a lista de cursos
       when(cursoService.getAll()).thenReturn(cursos);

       // Executa o método de listagem
       String viewName = cursoController.getCursosList(model);

       // Verifica se a visão retornada é a esperada
       assertEquals("cursos/cursos", viewName);

       // Verifica se os cursos foram adicionados ao modelo
       verify(model, times(1)).addAttribute("cursos", cursos);
       verify(cursoService, times(1)).getAll();
   }
   @DisplayName("Teste de Atualização de Curso com Professor Existente")
   @Test
   public void testUpdateCursoWithExistingProfessor() throws Exception {
       Long idCurso = 1L;
       Long idProfesor = 1L;
       Curso curso = new Curso();
       curso.setId_curso(idCurso);
       Profesor profesor = new Profesor();
       profesor.setId_profesor(idProfesor);

       when(profesorRepository.findById(idProfesor)).thenReturn(Optional.of(profesor));
       when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso));
       doNothing().when(cursoService).update(curso, idCurso);

       RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
       String viewName = cursoController.updateCurso(idProfesor, idCurso, curso, model, redirectAttributes);

       assertEquals("redirect:/cursos/{id_curso}", viewName);
       verify(cursoService, times(1)).update(curso, idCurso);
   }
   


  
}


