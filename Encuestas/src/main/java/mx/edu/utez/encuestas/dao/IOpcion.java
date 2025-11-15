package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Opcion;

import java.util.List;

public interface IOpcion {
    public boolean insertarOpcion(String texto, int idPregunta);
    public List<Opcion> obtenerOpcionesPorPregunta(int idPregunta);
}
