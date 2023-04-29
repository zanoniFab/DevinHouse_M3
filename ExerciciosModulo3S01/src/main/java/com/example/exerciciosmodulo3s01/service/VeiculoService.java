package com.example.exerciciosmodulo3s01.service;

import com.example.exerciciosmodulo3s01.exception.FalhaExclusaoVeiculoComMultasException;
import com.example.exerciciosmodulo3s01.exception.RegistroExistenteException;
import com.example.exerciciosmodulo3s01.exception.RegistroNaoEncontradoException;
import com.example.exerciciosmodulo3s01.model.Veiculo;
import com.example.exerciciosmodulo3s01.repository.VeiculoRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VeiculoService {

    @Autowired
    private VeiculoRepository repo;
    public Veiculo cadastrar(Veiculo veiculo) {
            boolean isPlacaCadastrada = repo.existsVeiculoByPlaca(veiculo.getPlaca());
            if (isPlacaCadastrada) {
                log.error("Cadastro não realizado: placa já registrada.");
                throw new RegistroExistenteException();
            }
            veiculo = repo.save(veiculo);
        return veiculo;
    }

    public List<Veiculo> consultar() {
        List<Veiculo> veiculos = repo.findAll();
        return veiculos;
    }

    public Veiculo obterPlaca(String placa) {
        Optional<Veiculo> veiculoOptional = repo.findByPlaca(placa);
        if (veiculoOptional.isEmpty()){
            log.error("Placa {} não encontrada.",placa);
            throw new RegistroNaoEncontradoException();
        }
        return veiculoOptional.get();
    }

    public Veiculo adicionarMulta(String placa) {
        Optional<Veiculo> veiculoOptional = repo.findByPlaca(placa);
        if(veiculoOptional.isEmpty()){
            log.error("Veículo de placa {} não encontrada.",placa);
            throw new RegistroNaoEncontradoException();
        }
        var veiculo = veiculoOptional.get();
        int qtd = veiculo.getQtdMultas()+1;
        veiculo.setQtdMultas(qtd);
        return repo.save(veiculo);
    }

    public void excluir(String placa) {
        Optional<Veiculo> veiculoOptional = repo.findByPlaca(placa);
        if (veiculoOptional.isEmpty()) {
            log.error("Veículo de placa {} não encontrada.", placa);
            throw new RegistroNaoEncontradoException();
        }
        var veiculo = veiculoOptional.get();
        if (veiculo.getQtdMultas() != 0){
            log.error("Veículo de placa {} não pode ser excluído pois tem {} multas.", placa, veiculo.getQtdMultas());
        throw new FalhaExclusaoVeiculoComMultasException();
        }
        repo.deleteById(placa);
    }
}
