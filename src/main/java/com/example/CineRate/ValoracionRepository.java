package com.example.CineRate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class ValoracionRepository {

    private final List<Valoracion> valoraciones = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Valoracion guardar(Valoracion valoracion) {
        valoracion.setId(contador.getAndIncrement());
        valoraciones.add(valoracion);
        return valoracion;
    }

    public List<Valoracion> obtenerTodas() {
        return new ArrayList<>(valoraciones);
    }

    public List<Valoracion> obtenerPorPelicula(Long peliculaId) {
        return valoraciones.stream()
                .filter(v -> v.getPeliculaId().equals(peliculaId))
                .toList();
    }
}
