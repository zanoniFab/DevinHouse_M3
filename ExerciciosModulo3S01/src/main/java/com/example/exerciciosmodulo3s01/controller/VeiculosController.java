package com.example.exerciciosmodulo3s01.controller;

import com.example.exerciciosmodulo3s01.dto.VeiculoRequest;
import com.example.exerciciosmodulo3s01.dto.VeiculoResponse;
import com.example.exerciciosmodulo3s01.model.Veiculo;
import com.example.exerciciosmodulo3s01.service.VeiculoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
@Slf4j
public class VeiculosController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private VeiculoService service;

    @PostMapping
    public ResponseEntity cadastrar (@RequestBody @Valid VeiculoRequest request) {
        log.debug("Cadastro de veiculo: {}",request);
        Veiculo veiculo = modelMapper.map(request, Veiculo.class);
        veiculo = service.cadastrar(veiculo);
        VeiculoResponse resp = modelMapper.map(veiculo,VeiculoResponse.class);
        return ResponseEntity.created(URI.create(resp.getPlaca().toString())).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<VeiculoResponse>> consultar () {
        log.debug("Realizada consulta geral de veículos");
        List<Veiculo> veiculos = service.consultar();
        log.info("Registros encontrados: {}.",veiculos.size());
        List<VeiculoResponse> resp = veiculos.stream().map(p->modelMapper.map(p,VeiculoResponse.class)).toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("{placa}")
    public ResponseEntity<VeiculoResponse> consultarPlaca (@PathVariable String placa) {
        log.debug("Realizada consulta de veículo com placa: {}", placa);
        Veiculo veiculo = service.obterPlaca(placa);
        VeiculoResponse resp = modelMapper.map(veiculo,VeiculoResponse.class);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("{placa}/multas")
    public ResponseEntity<VeiculoResponse> adicionarMulta(@PathVariable String placa) {
        log.debug("Aplicada multa ao veículo: {}.",placa);
        Veiculo veiculo = service.adicionarMulta(placa);
        VeiculoResponse resp = modelMapper.map(veiculo,VeiculoResponse.class);
        return ResponseEntity.ok(resp);

    }

    @DeleteMapping("{placa}")
    public ResponseEntity excluir(@PathVariable String placa) {
        log.debug("Excluido veículo placa: {}",placa);
        service.excluir(placa);
        return ResponseEntity.noContent().build();
    }
}
