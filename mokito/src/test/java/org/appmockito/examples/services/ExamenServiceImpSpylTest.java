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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Otra forma de usar anotaciones en Mockito es
 */
@ExtendWith(MockitoExtension.class)
class ExamenServiceImpSpylTest {

    /**
     * Forma para utilizar Spy con anotaciones.
     *  Importante la Injection of Dpendency debe hacerse con clases Implementadas no con Interfaces o calses
     *  Abstractas.
     * @Spy.
     */
    @Spy
    ExamenRepositoryImpl repository;
    @Spy
    PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    ExamenServiceImpl service;

    /**
     * Utilizando el @Spy con anotaciones.
     */
    @Test
    void testSpy() {
//        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
//        PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
//        ExamenService examenService = new ExamenServiceImpl(preguntaRepository, examenRepository);

        List<String> preguntas = Arrays.asList("Polimorfismo");

        /**
         * Produce un error el utilizar el When para utilizar un Mock dentro del metodoTest
         * Ya que ace un llamado al metodo real, por esta razon utilizamos el:
         * -> doReturn().when()
         */
        //Metodo Mock hace una invocacion falsa del metodo real.
        doReturn(preguntas).when(preguntaRepository).findQuestionsByExamenId(anyLong());
//        when(preguntaRepository.findQuestionsByExamenId(anyLong())).thenReturn(preguntas);

        Examen examen = service.findExamenByNameWithQuestios("Java");

        assertEquals(5, examen.getId());
        assertEquals("Java", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Polimorfismo"));

        verify(repository).findAll();
        verify(preguntaRepository).findQuestionsByExamenId(anyLong());
    }
}