package org.appmockito.examples.services;

import org.appmockito.examples.models.Examen;

import java.util.Optional;

public interface ExamenService {

    /**
     * Method.
     * @param nombre
     * @return Examen.
     */
    Optional<Examen> findExamenByName(String nombre);

    Examen findExamenByNameWithQuestios(String nombre);

    /**
     * Method Guardar
     * @param examen
     * @return Examen
     */
    Examen guardar(Examen examen);
}
