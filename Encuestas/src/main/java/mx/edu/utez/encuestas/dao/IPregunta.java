package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Pregunta;

import java.util.List;

public interface IPregunta {
    public int insertarPregunta(String texto, int idEncuesta);
    public List<Pregunta> obtenerPreguntasPorEncuesta(int idEncuesta);
}
