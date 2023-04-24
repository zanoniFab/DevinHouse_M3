package com.example.exerciciosmodulo3s01.controller;

import com.example.exerciciosmodulo3s01.dto.VeiculoRequest;
import com.example.exerciciosmodulo3s01.exception.RegistroExistenteException;
import com.example.exerciciosmodulo3s01.exception.RegistroNaoEncontradoException;
import com.example.exerciciosmodulo3s01.model.Veiculo;
import com.example.exerciciosmodulo3s01.service.VeiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(SpringExtension.class)
@WebMvcTest

class VeiculosControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VeiculoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    @DisplayName("Quando tentar cadastrar veiculo com placa já existente, deve retornar erro")
    void cadastrar_placaExistente() throws Exception {
        Mockito.when(service.cadastrar(Mockito.any(Veiculo.class))).thenThrow(RegistroExistenteException.class);
        var req = new VeiculoRequest("abcd1234","caminhao","branco",1990,0);
        String requestJson = objectMapper.writeValueAsString(req);
        mockMvc.perform(post("/api/veiculos")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro", containsStringIgnoringCase("Registro já cadastrado!")));
    }

    @Test
    @DisplayName("Quando tentar cadastrar veiculo com placa não existente, deve retornar sucesso")
    void cadastrar_placaNaoExistente() throws Exception {
        var req = new VeiculoRequest("abcd1234","caminhao","branco",1990,0);
        Veiculo veiculo = modelMapper.map(req,Veiculo.class);
        String requestJson = objectMapper.writeValueAsString(req);
        Mockito.when(service.cadastrar(Mockito.any(Veiculo.class))).thenReturn(veiculo);
        mockMvc.perform(post("/api/veiculos")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa", is(notNullValue())))
                .andExpect(jsonPath("$.placa", is(veiculo.getPlaca())));
    }

    @Test
    @DisplayName("Quando não houver registros, deve retornar lista vazia")
    void consultar_semRegistros() throws Exception {
        mockMvc.perform(get("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",is(empty())));
    }

    @Test
    @DisplayName("Quando houver registros, deve retornar lista com os registros")
    void consultar_comRegistros() throws Exception {
        var listaVeiculos = List.of(
                new Veiculo("abcd1234","moto","preto",1989,0),
                new Veiculo("efdg1234","carro","cinza",1989,0)
                );
        Mockito.when(service.consultar()).thenReturn(listaVeiculos);
        mockMvc.perform(get("/api/veiculos").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    @DisplayName("Quando não há placa registrada, deve retornar erro")
    void consultarPlaca_registroInexistente() throws Exception {
        Mockito.when(service.obterPlaca(Mockito.anyString())).thenThrow(RegistroNaoEncontradoException.class);
        mockMvc.perform(get("/api/veiculos/{placa}","abcd1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Quando há placa registrada, deve retornar o veiculo")
    void consultarPlaca_registroExistente() throws Exception {
        Veiculo veiculo = new Veiculo("abcd1234","moto","preto",1989,0);
        Mockito.when(service.obterPlaca(Mockito.anyString())).thenReturn(veiculo);
        mockMvc.perform(get("/api/veiculos/{placa}", veiculo.getPlaca())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa",is(veiculo.getPlaca())));
    }


    @Test
    @DisplayName("Quando não há placa cadastrada, deve retornar erro")
    void multar_placaInexistente() throws Exception {
        Mockito.when(service.adicionarMulta(Mockito.anyString())).thenThrow(RegistroNaoEncontradoException.class);
        mockMvc.perform(put("/api/veiculos/{placa}/multas", "abcd1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Quando há placa cadastrada, deve retornar sucesso")
    void multar_placaExistente() throws Exception {
        Veiculo veiculo = new Veiculo("abcd1234","moto","preto",1989,0);
        final int qtdMultasInicial = veiculo.getQtdMultas();
        veiculo.setQtdMultas(2);
        Mockito.when(service.adicionarMulta(Mockito.anyString())).thenReturn(veiculo);
        mockMvc.perform(put("/api/veiculos/{placa}/multas", veiculo.getPlaca())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa",is(notNullValue())))
                .andExpect(jsonPath("$.qtdMultas",is(qtdMultasInicial+2)));
    }
}