package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Opcion;

import java.util.List;

public interface IOpcion {
    int insertarOpcion(String texto, int idEncuesta);
    List<Opcion> obtenerOpcionesPorPregunta(int idPregunta);
    boolean eliminarOpcionesPorPregunta(int idPregunta);
}
