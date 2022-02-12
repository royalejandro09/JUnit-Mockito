package org.appmockito.examples.repositories;

import org.appmockito.examples.Data;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PreguntaRepositoryImpl implements PreguntaRepository {


    @Override
    public List<String> findQuestionsByExamenId(Long id) {
        System.out.println("PreguntaRepositoryImpl.findQuestionsByExamenId");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Data.PREGUNTAS_LIST;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.guardarVarias");

    }
}
