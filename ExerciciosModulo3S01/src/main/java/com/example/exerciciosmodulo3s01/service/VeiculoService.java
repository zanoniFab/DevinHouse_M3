package com.example.exerciciosmodulo3s01.service;

import com.example.exerciciosmodulo3s01.exception.FalhaExclusaoVeiculoComMultasException;
import com.example.exerciciosmodulo3s01.exception.RegistroExistenteException;
import com.example.exerciciosmodulo3s01.exception.RegistroNaoEncontradoException;
import com.example.exerciciosmodulo3s01.model.Veiculo;
import com.example.exerciciosmodulo3s01.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository repo;
    public Veiculo cadastrar(Veiculo veiculo) {
        boolean isPlacaCadastrada = repo.existsVeiculoByPlaca(veiculo.getPlaca());
        if (isPlacaCadastrada) {
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
       return repo.findByPlaca(placa).orElseThrow(RegistroNaoEncontradoException::new);
    }

    public Veiculo adicionarMulta(String placa) {
        Veiculo veiculo = repo.findByPlaca(placa).orElseThrow(RegistroNaoEncontradoException::new);
        int qtd = veiculo.getQtdMultas()+1;
        veiculo.setQtdMultas(qtd);
        return repo.save(veiculo);
    }

    public void excluir(String placa) {
        Veiculo veiculo = repo.findByPlaca(placa).orElseThrow(RegistroNaoEncontradoException::new);
        if (veiculo.getQtdMultas() != 0)
            throw new FalhaExclusaoVeiculoComMultasException();
        repo.deleteById(placa);
    }
}
