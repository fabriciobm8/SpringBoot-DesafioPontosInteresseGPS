package com.example.gps.controller;

import com.example.gps.model.PontosInteresse;
import com.example.gps.model.PontosInteresseRequestDTO;
import com.example.gps.model.PontosInteresseResponseDTO;
import com.example.gps.repositoy.PontosInteresseRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Validated
@RestController
public class PontosInteresseController {

    @Autowired
    private PontosInteresseRepository repository;

    @PostMapping("/pontos-de-interesse")
    public ResponseEntity<Void> pontosInteresseCriar(@RequestBody @Valid PontosInteresseRequestDTO body) {
        PontosInteresse pontosInteresse = PontosInteresse.builder()
                .nome(body.nome())
                .x(body.x())
                .y(body.y())
                .build();
        repository.save(pontosInteresse);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/listar/pontos-de-interesse")
    public ResponseEntity<Page<PontosInteresseResponseDTO>> pontosInteresseListar(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "A página deve ser maior ou igual a zero") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "O tamanho da página deve ser maior que zero") @Max(value = 100, message = "O tamanho da página não pode exceder 100") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort sortOrder = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<PontosInteresseResponseDTO> pontosPage = repository.findAll(pageable)
                .map(ponto -> new PontosInteresseResponseDTO(
                        ponto.getId(),
                        ponto.getNome(),
                        ponto.getX(),
                        ponto.getY()));

        return ResponseEntity.ok(pontosPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontosInteresseResponseDTO> pontosInteresseBuscarPorId(@PathVariable Long id) {
        PontosInteresse ponto = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ponto de interesse com ID " + id + " não encontrado"));
        return ResponseEntity.ok(new PontosInteresseResponseDTO(
                ponto.getId(),
                ponto.getNome(),
                ponto.getX(),
                ponto.getY()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontosInteresseResponseDTO> pontosInteresseAtualizar(@PathVariable Long id, @RequestBody @Valid PontosInteresseRequestDTO body) {
        PontosInteresse ponto = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ponto de interesse com ID " + id + " não encontrado"));
        ponto.setNome(body.nome());
        ponto.setX(body.x());
        ponto.setY(body.y());
        PontosInteresse atualizado = repository.save(ponto);
        return ResponseEntity.ok(new PontosInteresseResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getX(),
                atualizado.getY()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> pontosInteresseDeletar(@PathVariable Long id) {
        PontosInteresse ponto = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ponto de interesse com ID " + id + " não encontrado"));
        repository.delete(ponto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listar/pontos-proximos")
    public ResponseEntity<Page<PontosInteresseResponseDTO>> listarPontosProximos(
            @RequestParam("x") @NotNull(message = "A coordenada X é obrigatória") @Positive(message = "A coordenada X deve ser maior que zero") Long x,
            @RequestParam("y") @NotNull(message = "A coordenada Y é obrigatória") @Positive(message = "A coordenada Y deve ser maior que zero") Long y,
            @RequestParam("dmax") @NotNull(message = "A distância máxima é obrigatória") @Positive(message = "A distância máxima deve ser maior que zero") Long dmax,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "A página deve ser maior ou igual a zero") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "O tamanho da página deve ser maior que zero") @Max(value = 100, message = "O tamanho da página não pode exceder 100") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort sortOrder = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        long xMin = x - dmax;
        long xMax = x + dmax;
        long yMin = y - dmax;
        long yMax = y + dmax;

        Page<PontosInteresse> pontosPage = repository.findPontosInteresseProximos(xMin, xMax, yMin, yMax, pageable);
        List<PontosInteresseResponseDTO> pontosFiltradosList = pontosPage.getContent()
                .stream()
                .filter(ponto -> distanciaEuclidiana(x, y, ponto.getX(), ponto.getY()) <= dmax)
                .map(ponto -> new PontosInteresseResponseDTO(
                        ponto.getId(),
                        ponto.getNome(),
                        ponto.getX(),
                        ponto.getY()))
                .collect(Collectors.toList());

        Page<PontosInteresseResponseDTO> pontosFiltrados = new PageImpl<>(
                pontosFiltradosList,
                pageable,
                pontosFiltradosList.size());

        return ResponseEntity.ok(pontosFiltrados);
    }

    private double distanciaEuclidiana(long x1, long y1, long x2, long y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }
}
