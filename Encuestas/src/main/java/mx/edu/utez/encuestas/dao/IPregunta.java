package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Pregunta;

import java.util.List;

public interface IPregunta {
    int insertarPregunta(String texto, int idEncuesta);
    boolean actualizarPregunta(String texto, int idEncuesta, int idPregunta);
    boolean eliminarPregunta(int idPregunta);
    List<Pregunta> obtenerPreguntasPorEncuesta(int idEncuesta);
    int contarPreguntasPorEncuesta(int idEncuesta);
}
