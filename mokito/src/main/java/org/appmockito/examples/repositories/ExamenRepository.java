package org.appmockito.examples.repositories;

import org.appmockito.examples.models.Examen;

import java.util.List;

public interface ExamenRepository {

    /**
     * Methods.
     */
    List<Examen> findAll();

    Examen guardar(Examen examen);
}
