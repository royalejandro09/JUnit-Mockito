package org.appmockito.examples;

import org.appmockito.examples.models.Examen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Data {

    public static final List<Examen> EXAMEN_LIST = Arrays.asList(new Examen(5L, "Java"),
            new Examen(6L, "SpringBoot"),
            new Examen(7L, "JavaScript"));

    public static final List<Examen> EXAMEN_LIST_ID_NULL = Arrays.asList(new Examen(null, "Java"),
            new Examen(null, "SpringBoot"),
            new Examen(null, "JavaScript"));

    public static final List<Examen> EXAMEN_LIST_ID_NEGATIVO = Arrays.asList(new Examen(-5l, "Java"),
            new Examen(-6l, "SpringBoot"),
            new Examen(null, "JavaScript"));

    public static final List<String> PREGUNTAS_LIST = Arrays.asList("Polimorfismo",
            "Herencia", "Class Abstractas", "Casteo", "Patrones de Diseño");

    public static final Examen EXAMEN = new Examen(null, "MicroServicios");
}
