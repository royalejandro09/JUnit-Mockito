package org.appmockito.examples.repositories;

import org.appmockito.examples.Data;
import org.appmockito.examples.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryImpl implements ExamenRepository {

    /**
     * Method.
     *
     * @return
     */
    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositoryImpl.findAll");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Data.EXAMEN_LIST;
    }

    /**
     * Method Guardar
     *
     * @param examen
     * @return
     */
    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositoryImpl.guardar");
        return Data.EXAMEN;
    }
}
