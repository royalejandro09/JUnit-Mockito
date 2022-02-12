package org.appmockito.examples.services;

import org.appmockito.examples.models.Examen;
import org.appmockito.examples.repositories.ExamenRepository;
import org.appmockito.examples.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService {

    /**
     * Attribute.
     * Dpendency of Injection
     */
    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    /**
     * Constructor.
     * Injection of Dependency By Constructor.
     */
    public ExamenServiceImpl(PreguntaRepository preguntaRepository, ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }

    /**
     * Methods.
     *
     * @param nombre
     * @return Examen
     */

    @Override
    public Optional<Examen> findExamenByName(String nombre) {
        return examenRepository.findAll().
                stream().
                filter(e -> e.getNombre().contains(nombre)).
                findFirst();
    }

    @Override
    public Examen findExamenByNameWithQuestios(String nombre) {
        Optional<Examen> findExamenOptional = findExamenByName(nombre);
        Examen examen = null;

        if (findExamenOptional.isPresent()) {
            examen = findExamenOptional.orElseThrow();
            List<String> findQuestions = preguntaRepository.
                    findQuestionsByExamenId(examen.getId());

            examen.setPreguntas(findQuestions);
        }
        return examen;
    }

    /**
     * Method Guardar.
     * @param examen
     * @return
     */
    @Override
    public Examen guardar(Examen examen) {
        if (!examen.getPreguntas().isEmpty()){
            preguntaRepository.guardarVarias(examen.getPreguntas());
        }
        return examenRepository.guardar(examen);
    }


}
