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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/valoraciones")
@Tag(name = "Valoraciones", description = "API para registrar y consultar valoraciones de películas")
public class ValoracionController {

    private final ValoracionRepository repositorio;
    public ValoracionController(ValoracionRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las valoraciones",
            description = "Devuelve la lista de todas las valoraciones registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de valoraciones obtenida con éxito")
    })
    public List<Valoracion> obtenerTodas() {
        return repositorio.obtenerTodas();
    }

    @GetMapping("/pelicula/{peliculaId}")
    @Operation(summary = "Obtener valoraciones de una película",
            description = "Devuelve las valoraciones registradas para la película indicada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valoraciones de la película obtenidas con éxito")
    })
    public List<Valoracion> obtenerPorPelicula(
            @PathVariable @Parameter(description = "ID de la película") Long peliculaId) {
        return repositorio.obtenerPorPelicula(peliculaId);
    }

    @PostMapping
    @Operation(summary = "Registrar una valoración",
            description = "Registra un voto de AUDIENCIA o CRITICA sobre una película.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Valoración registrada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o tipo de votante incorrecto")
    })
    public ResponseEntity<?> registrar(
            @RequestBody @Parameter(description = "Datos de la valoración a registrar") Valoracion valoracion) {

        if (valoracion.getPeliculaId() == null
                || valoracion.getPeliculaId() <= 0
                || valoracion.getTipoVotante() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Datos de valoracion invalidos"));
        }

        String tipo = valoracion.getTipoVotante().toUpperCase();

        if (!tipo.equals("AUDIENCIA") && !tipo.equals("CRITICA")) {
            return ResponseEntity.badRequest().body(Map.of("error",
                    "El tipo debe ser AUDIENCIA o CRITICA"));
        }

        valoracion.setTipoVotante(tipo);
        Valoracion guardada = repositorio.guardar(valoracion);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @GetMapping("/pelicula/{peliculaId}/resumen")
    @Operation(summary = "Obtener resumen de aprobación",
            description = "Devuelve el porcentaje de aprobación de audiencia y crítica, calculados por separado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumen calculado con éxito")
    })
    public Map<String, Object> obtenerResumen(
            @PathVariable @Parameter(description = "ID de la película") Long peliculaId) {

        List<Valoracion> votos = repositorio.obtenerPorPelicula(peliculaId);

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("peliculaId", peliculaId);
        resumen.put("audiencia", calcularPorcentaje(votos, "AUDIENCIA"));
        resumen.put("critica", calcularPorcentaje(votos, "CRITICA"));
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

