package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Opcion;

import java.util.List;

public interface IOpcion {
    boolean insertarOpcion(String texto, int idPregunta);
    List<Opcion> obtenerOpcionesPorPregunta(int idPregunta);
    boolean eliminarOpcionesPorPregunta(int idPregunta);
}
