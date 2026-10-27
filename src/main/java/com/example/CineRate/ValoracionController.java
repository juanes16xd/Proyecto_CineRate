package com.example.CineRate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/valoraciones")
public class ValoracionController {
    private final ValoracionRepository repositorio;
    public ValoracionController(ValoracionRepository repositorio) {
        this.repositorio = repositorio;
    }
    @GetMapping
    public List<Valoracion> obtenerTodas() {
        return repositorio.obtenerTodas();
    }
    @GetMapping("/pelicula/{peliculaId}")
    public List<Valoracion> obtenerPorPelicula(
            @PathVariable Long peliculaId) {
        return repositorio.obtenerPorPelicula(peliculaId);
    }
    @PostMapping
    public ResponseEntity<?> registrar(
            @RequestBody Valoracion valoracion) {
        if (valoracion.getPeliculaId() == null
                || valoracion.getPeliculaId() <= 0
                || valoracion.getTipoVotante() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Datos de valoracion invalidos"));
        }
        String tipo = valoracion.getTipoVotante().toUpperCase();

        if (!tipo.equals("AUDIENCIA") && !tipo.equals("CRITICA")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "El tipo debe ser AUDIENCIA o CRITICA"));
        }
        valoracion.setTipoVotante(tipo);
        Valoracion guardada = repositorio.guardar(valoracion);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }
    @GetMapping("/pelicula/{peliculaId}/resumen")
    public Map<String, Object> obtenerResumen(
            @PathVariable Long peliculaId) {
        List<Valoracion> votos =
                repositorio.obtenerPorPelicula(peliculaId);
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("peliculaId", peliculaId);
        resumen.put("audiencia",
                calcularPorcentaje(votos, "AUDIENCIA"));
        resumen.put("critica",
                calcularPorcentaje(votos, "CRITICA"));
        return resumen;
    }
    private Map<String, Object> calcularPorcentaje(
            List<Valoracion> votos, String tipo) {
        long total = votos.stream()
                .filter(v -> v.getTipoVotante().equals(tipo))
                .count();
        long positivos = votos.stream()
                .filter(v -> v.getTipoVotante().equals(tipo))
                .filter(Valoracion::isPositiva)
                .count();
        double porcentaje = total == 0
                ? 0.0
                : positivos * 100.0 / total;

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("votosTotales", total);
        resultado.put("votosPositivos", positivos);
        resultado.put("porcentajeAprobacion",
                Math.round(porcentaje * 100.0) / 100.0);
        return resultado;
    }
}

