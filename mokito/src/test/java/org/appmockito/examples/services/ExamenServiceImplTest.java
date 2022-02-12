package org.appmockito.examples.services;

import org.appmockito.examples.Data;
import org.appmockito.examples.models.Examen;
import org.appmockito.examples.repositories.ExamenRepository;
import org.appmockito.examples.repositories.ExamenRepositoryImpl;
import org.appmockito.examples.repositories.PreguntaRepository;
import org.appmockito.examples.repositories.PreguntaRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Otra forma de usar anotaciones en Mockito es
 */
@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    /**
     * Attribute Globlas of class.
     * Usando anotaciones @ para hacer la Instancia de los Mock y de la clase
     * concreta Service class ExamenServiceImpl.
     */
    @Mock
    ExamenRepositoryImpl repository;
    @Mock
    PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    ExamenServiceImpl service;

    /**
     * Anotacion para el uso del ArgumentCaptore sin Instanciar.
     */
    @Captor
    ArgumentCaptor<Long> captor;


    /**
     * Methods lifeCycle.
     */
    @BeforeEach
    void setUp() {
        /**
         * Habilitar para el uso de Anotaciones
         * MockitoAnnotations.openMocks(this);
         */
//        MockitoAnnotations.openMocks(this);
//        this.repository = mock(ExamenRepository.class);
//        this.preguntaRepository = mock(PreguntaRepository.class);
//        this.service = new ExamenServiceImpl(preguntaRepository, repository);
    }

    /**
     * Methods Test
     */
    @Test
    void findExamenByName() {

        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);
        Optional<Examen> examen = service.findExamenByName("Java");

        /**
         * Assertions.
         */
        assertTrue(examen.isPresent());
        assertEquals(5l, examen.orElseThrow().getId());
        assertEquals("Java", examen.get().getNombre());

    }

    @Test
    void findExamenByNameListaVacia() {

        List<Examen> datosDePrueba = Collections.emptyList();

        when(repository.findAll()).thenReturn(datosDePrueba);
        Optional<Examen> examen = service.findExamenByName("Java");

        /**
         * Assertions.
         */
        assertFalse(examen.isPresent());
    }

    @Test
    void preguntasExamen() {

        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);
        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(Data.PREGUNTAS_LIST);

        Examen examenWithQuestions = service.findExamenByNameWithQuestios("SpringBoot");
        /**
         * Assertions.
         */
        assertEquals(5, examenWithQuestions.getPreguntas().size());
        assertTrue(examenWithQuestions.getPreguntas().contains("Casteo"));

    }

    @Test
    void preguntasExamenVerify() {

        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);
        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(Data.PREGUNTAS_LIST);

        Examen examenWithQuestions = service.findExamenByNameWithQuestios("SpringBoot");
        /**
         * Assertions.
         */
        assertEquals(5, examenWithQuestions.getPreguntas().size());
        assertTrue(examenWithQuestions.getPreguntas().contains("Casteo"));

        /**
         * Verify.
         */
        verify(repository).findAll();
        verify(preguntaRepository).findQuestionsByExamenId(anyLong());

    }

    @Test
    void testNoExisteExamenes() {

        /**
         *  Given ->
         */
//        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);

        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(Data.PREGUNTAS_LIST);

        /**
         *  When ->
         */
        Examen examenWithQuestions = service.findExamenByNameWithQuestios("SpringBoot");
        /**
         *  Then ->
         * Assertions.
         */
//        assertNull(examenWithQuestions);
        assertNotNull(examenWithQuestions);
        /**
         * Verify.
         */
        verify(repository).findAll();
        /**
         * Metodo no Invocado ya que en el metodo findAll()-se retorna una lista vacia y no
         * entra en el if donde se encuentra el metodo findQuestionsByExamenId(anyLong())
         */
        verify(preguntaRepository).findQuestionsByExamenId(anyLong());

    }


    /**
     * Trabajando en un entorno impulsado por el desarrollo del comportamiento
     * (VDD= behaviour Development Driven)
     * -Given, When, Then./ Dado, Cuendo y Entonces
     */
    @Test
    void testGuardarExamen() {

        /**
         *  Given -> Precondiciones de nuestro entorno de prueba
         */
        Examen newExamen = Data.EXAMEN;
        newExamen.setPreguntas(Data.PREGUNTAS_LIST);
        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {
                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }

        });

        /**
         *   When -> Cuendo ejecutamos el metodo que queremos Probar.
         */
        Examen examen = service.guardar(newExamen);

        /**
         *  Then -> Entonces validamos con Assertions o Verify.
         */
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("MicroServicios", examen.getNombre());

        /**
         * Verify.
         */
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());

    }

    @Test
    void testManejoException() {
        /**
         *  When ->
         */
        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST_ID_NULL);
        when(preguntaRepository.findQuestionsByExamenId(isNull())).thenThrow(IllegalArgumentException.class);
        /**
         *  Then ->
         */
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenByNameWithQuestios("Java");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(repository).findAll();
        verify(preguntaRepository).findQuestionsByExamenId(isNull());
    }

    /**
     * Uso de Argument Matchers con Mockito.
     */
    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);
        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(Data.PREGUNTAS_LIST);

        service.findExamenByNameWithQuestios("Java");

        verify(repository).findAll();
//        verify(preguntaRepository).findQuestionsByExamenId(argThat(arg -> arg != null && arg.equals(5L)));
        verify(preguntaRepository).findQuestionsByExamenId(argThat(arg -> arg != null && arg >= (5L)));
//        verify(preguntaRepository).findQuestionsByExamenId(eq(5L));

    }

    /**
     * Clase anidada MiArgMatchers.class
     */
    public static class MiArgMatchers implements ArgumentMatcher<Long> {
        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        /**
         * ToString para implementar el mensaje de Error.
         */
        @Override
        public String toString() {
            return "Mensaje impreso por Mockito en caso de que falle el test{" +
                    " argument= " + argument +
                    " debe ser un numero entero positivo}";
        }
    }

    @Test
    void testArgumentMatchers1() {
        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST_ID_NEGATIVO);
//        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);
        when(preguntaRepository.findQuestionsByExamenId(any())).
                thenReturn(Data.PREGUNTAS_LIST);

        Examen examen = service.findExamenByNameWithQuestios("JavaScript");

        verify(repository).findAll();
        /**
         * Utilizando la clase anidada MiArgMatchers.class
         * Como argumento enen metodo findQuestionsByExamenId(argThat(new MiArgMatchers()).
         */
        assertEquals(5, examen.getPreguntas().size());
//        verify(preguntaRepository).findQuestionsByExamenId(any());
        verify(preguntaRepository).findQuestionsByExamenId(argThat(new MiArgMatchers()));

    }

    @Test
    void testArgumentMatchers2() {

        when(repository.findAll()).thenReturn(org.appmockito.examples.Data.EXAMEN_LIST);
//        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST_ID_NEGATIVO);
        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(Data.PREGUNTAS_LIST);

//        Examen examen = service.findExamenByNameWithQuestios("Java");
        Examen examen = service.findExamenByNameWithQuestios("JavaScript");

        verify(repository).findAll();
        /**
         * Error en el test al pasar la lista de Examenes con ID negativo.
         * No cumple la condicipn del Verify()
         */
        verify(preguntaRepository).findQuestionsByExamenId(argThat((argument) -> argument != null && argument > 0));


    }

    /**
     * Uso de Argument Capture con Mockito
     * Para captura de argumentos.
     */
    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);
//        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(org.appmockito.examples.Data.PREGUNTAS_LIST);

        service.findExamenByNameWithQuestios("Java");

        /**
         * Instanciamos para el usuo del ArgumentCaptor<T>
         *     <Long>type Generic.
         *
         *     o tambien se puede usuar con la anotacion en la clase raiz
         *     -@Captor
         *     ArgumentCaptor<Long> captor;
         */
//        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(preguntaRepository).findQuestionsByExamenId(captor.capture());
        assertEquals(5L, captor.getValue());
    }

    /**
     * Familia de metodos DoThrow/DoAnswer... (Lanzar, tirar, responder)
     * Utilizado cuando el metodo no retorna nada es Void.
     */
    @Test
    void testDoThrow() {
        Examen examen = Data.EXAMEN;
        examen.setPreguntas(Data.PREGUNTAS_LIST);
        /**
         * Al ser el metodo Void
         * Se hace/lanza -> cuando se invoque el metodo Void.
         * doThrow().when()
         */
        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);
//        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(org.appmockito.examples.Data.PREGUNTAS_LIST);

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Data.PREGUNTAS_LIST : Collections.emptyList();
        }).when(preguntaRepository).findQuestionsByExamenId(anyLong());

        Examen examen = service.findExamenByNameWithQuestios("Java");

        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Patrones de Diseño"));
        assertEquals(5L, examen.getId());
        assertEquals("Java", examen.getNombre());

        verify(repository).findAll();
        verify(preguntaRepository).findQuestionsByExamenId(anyLong());

    }

    @Test
    void testDoAnswerGuardarExamen() {

        /**
         *  Given -> Precondiciones de nuestro entorno de prueba
         */
        Examen newExamen = Data.EXAMEN;
        newExamen.setPreguntas(Data.PREGUNTAS_LIST);

        doAnswer(new Answer<Examen>() {

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {
                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(repository).guardar(any(Examen.class));

        /**
         *   When -> Cuendo ejecutamos el metodo que queremos Probar.
         */
        Examen examen = service.guardar(newExamen);

        /**
         *  Then -> Entonces validamos con Assertions o Verify.
         */
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("MicroServicios", examen.getNombre());

        /**
         * Verify.
         */
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());

    }

    @Test
    void doCallRealMethod() {
        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);
//        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(Data.PREGUNTAS_LIST);

        /**
         * Implementacion del doCallRealMethod() con la clase Concreta
         * Injection of Dependency. preguntaRepositoryImpl = preguntaRepository
         */
        Mockito.doCallRealMethod().when(preguntaRepository).findQuestionsByExamenId(anyLong());

        Examen examen = service.findExamenByNameWithQuestios("Java");

//        assertEquals(5, examen.getPreguntas().size());
        assertEquals(5L, examen.getId());
        assertEquals("Java", examen.getNombre());

    }

    @Test
    void testSpy() {
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
        ExamenService examenService = new ExamenServiceImpl(preguntaRepository, examenRepository);

        List<String> preguntas = Arrays.asList("Polimorfismo");

        /**
         * Produce un error el utilizar el When para utilizar un Mock dentro del metodoTest
         * Ya que ace un llamado al metodo real, por esta razon utilizamos el:
         * -> doReturn().when()
         */
        //Mock hace una invocacion falsa del metodo real.
        doReturn(preguntas).when(preguntaRepository).findQuestionsByExamenId(anyLong());
//        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(preguntas);

        Examen examen = examenService.findExamenByNameWithQuestios("Java");

        assertEquals(5, examen.getId());
        assertEquals("Java", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Polimorfismo"));

        verify(examenRepository).findAll();
        verify(preguntaRepository).findQuestionsByExamenId(anyLong());
    }

    @Test
    void testOrderOfInvocation() {

        when(repository.findAll()).thenReturn(Data.EXAMEN_LIST);
        service.findExamenByNameWithQuestios("Java");
        service.findExamenByNameWithQuestios("JavaScript");

        /**
         * Inicializacion de la clase InOrder
         * Clase Static de Mockito.
         */
        InOrder order = inOrder(repository, preguntaRepository);

        /**
         * Veces invocados el metodo FindAll()
         * times(2)
         */
//        order.verify(repository).findAll(); //Este verifica que solo se invoca una vez por eso falla.
        verify(repository, times(2)).findAll();

        /**
         * Verifica el orden en que se ejecutan los metodos.
         */
        order.verify(preguntaRepository).findQuestionsByExamenId(5L);

        order.verify(repository).findAll();
        order.verify(preguntaRepository).findQuestionsByExamenId(7L);
    }


}