package com.example.exerciciosmodulo3s01.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VeiculoRequest {

    @NotEmpty(message = "Placa deve ser informada")
    private String placa;

    @NotEmpty(message = "Tipo de veículo deve ser informado: carro, caminhão, moto...")
    private String tipo;

    @NotEmpty(message = "Cor do veículo deve ser informada")
    private String cor;

    @NotNull(message = "Ano de fabricação deve ser informado")
    private Integer anoDeFabricacao;

    @NotNull(message = "Quantidade de multas deve ser informada")
    private Integer qtdMultas;
}
