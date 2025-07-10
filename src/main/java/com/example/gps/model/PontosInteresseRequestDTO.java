package com.example.gps.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PontosInteresseRequestDTO(

        @NotBlank(message = "O nome não pode ser nulo")
        String nome,
        @PositiveOrZero(message = "A Coordenada X tem que ser Maior ou Igual a Zero")
        @NotNull(message = "A coordenada X não pode ser nula")
        Long x,
        @PositiveOrZero(message = "A Coordenada Y tem que ser Maior ou Igual a Zero")
        @NotNull(message = "A coordenada Y não pode ser nula")
        Long y) {

}
