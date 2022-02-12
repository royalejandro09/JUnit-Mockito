package org.appmockito.examples.repositories;

import org.appmockito.examples.models.Examen;

import java.util.List;

public interface PreguntaRepository {

    /**
     * Methods.
     */
    List<String> findQuestionsByExamenId(Long id);

    /**
     * Guardar lista de Preguntas
     * No retorna nada.
     * @param preguntas
     */
    void guardarVarias(List<String> preguntas);
}
