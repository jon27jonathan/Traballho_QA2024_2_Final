import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import com.ezlearning.platform.auth.User;
import com.ezlearning.platform.auth.UserRepository;
import com.ezlearning.platform.controller.MatriculaController;
import com.ezlearning.platform.model.Curso;
import com.ezlearning.platform.repositories.CursoRepository;
import com.ezlearning.platform.services.core.impl.MatriculaService;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MatriculaControllerTest {

    @Mock
    private MatriculaService matriculaService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private MatriculaController matriculaController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Teste para salvar a matricula de usuário registrado")
    @Test
    public void testSaveMatriculaWithAuthenticatedUser() throws Exception {
        // Configuração
        Long idCurso = 1L;
        String username = "userTest";
        
        // Criação do usuário simulado
        User user = new User();
        user.setUsername(username);

        // Criação do curso simulado
        Curso curso = new Curso();
        
        // Definindo o ID do curso usando reflexão
        Field idField = Curso.class.getDeclaredField("id_curso");
        idField.setAccessible(true);
        idField.set(curso, idCurso); // Define o ID do curso para 1L

        // Configuração dos mocks
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(cursoRepository.findById(idCurso)).thenReturn(java.util.Optional.of(curso));

        // Ação
        String viewName = matriculaController.saveMatricula(idCurso, authentication, model);

        // Verificação
        verify(matriculaService, times(1)).createMatricula(idCurso, username);
        verify(model, times(1)).addAttribute("curso", curso);
        verify(model, times(1)).addAttribute("user", user);
        assertEquals("matricula-success", viewName);
    }

    @DisplayName("Teste para detectar erro quando o curso não é encontrado")
    @Test
    public void testSaveMatriculaWhenCursoNotFound() throws Exception {
        Long idCurso = 1L;
        String username = "testUser";

        // Configura o comportamento dos mocks
        when(authentication.getName()).thenReturn(username);
        doNothing().when(matriculaService).createMatricula(idCurso, username);
        // Configura o cursoRepository para lançar a exceção diretamente, simulando curso não encontrado
        when(cursoRepository.findById(idCurso)).thenReturn(Optional.empty());

        // Chama o método e captura a view retornada
        String viewName = matriculaController.saveMatricula(idCurso, authentication, model);

        // Verifica se a view retornada é "error"
        assertEquals("error", viewName);
        // Verifica se o modelo contém a exceção com a chave "error"
        verify(model).addAttribute(eq("error"), any(RuntimeException.class));
    }}
