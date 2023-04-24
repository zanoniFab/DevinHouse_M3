package com.example.exerciciosmodulo3s01.service;

import com.example.exerciciosmodulo3s01.exception.FalhaExclusaoVeiculoComMultasException;
import com.example.exerciciosmodulo3s01.exception.RegistroExistenteException;
import com.example.exerciciosmodulo3s01.exception.RegistroNaoEncontradoException;
import com.example.exerciciosmodulo3s01.model.Veiculo;
import com.example.exerciciosmodulo3s01.repository.VeiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {
    @Mock
    private VeiculoRepository repo;

    @InjectMocks
    private VeiculoService service;

    @Test
    @DisplayName("Quando tentar cadastrar placa já existente, deve retornar erro")
    void cadastrarVeiculo_placaExistente() {
        //given
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("abc1234");
        //when
        Mockito.when(repo.existsVeiculoByPlaca(Mockito.anyString())).thenReturn(true);
        //then
        assertThrows(RegistroExistenteException.class, () -> service.cadastrar(veiculo));
    }

    @Test
    @DisplayName("Quando tentar cadastrar placa não existente, deve retornar veiculo cadastrado")
    void cadastrarVeiculo() {
        //given
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("abc1234");
        Mockito.when(repo.existsVeiculoByPlaca(Mockito.anyString())).thenReturn(false);
        Mockito.when(repo.save(Mockito.any(Veiculo.class))).thenReturn(veiculo);
        //when
        var resultado = service.cadastrar(veiculo);
        //then
        assertNotNull(resultado);
        assertEquals(veiculo.getPlaca(),resultado.getPlaca());
    }

    @Test
    @DisplayName("Quando não houver registros, deve retornar lista vazia")
    void consultar_semRegistros() {
        List<Veiculo> resultado = service.consultar();
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Quando houver registros, deve retornar lista com os registros")
    void consultar_comRegistros() {
        //given
        var listaVeiculos = List.of(
                new Veiculo("abc1234","carro","cinza",1980,0),
                new Veiculo("ghi1234","caminhao","preto",1980,0),
                new Veiculo("def1234","moto","vermelho",1980,0)
        );
        Mockito.when(repo.findAll()).thenReturn(listaVeiculos);
        //when
        List<Veiculo> resultado = service.consultar();
        //then
        assertFalse(resultado.isEmpty());
        assertEquals(listaVeiculos.size(),resultado.size());
    }

    @Test
    @DisplayName("Quando não houver placa cadastrada, deve retornar erro")
    void obter_placaInexistente() {
        Mockito.when(repo.findByPlaca(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(RegistroNaoEncontradoException.class, ()->service.obterPlaca("abc1234"));
    }

    @Test
    @DisplayName("Quando houver placa cadastrada, deve retornar o veiculo cadastrado com a placa")
    void obter_placaExistente() {
        Mockito.when(repo.findByPlaca(Mockito.anyString())).thenReturn(Optional.of(new Veiculo()));
        var resultado = service.obterPlaca("abc1234");
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Quando placa não cadastrada, deve retornar erro")
    void adicionarMulta_placaInexistente() {
        Mockito.when(repo.findByPlaca(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(RegistroNaoEncontradoException.class,()->service.adicionarMulta("abcd1234"));
    }

    @Test
    @DisplayName("Quando placa cadastrada, deve adicionar multa")
    void adicionarMulta_placaExistente() {
        Veiculo veiculo = new Veiculo("abcd1234","moto","vermelho",1980,0);
        Mockito.when(repo.findByPlaca(Mockito.anyString())).thenReturn(Optional.of(veiculo));
        var resultado = service.adicionarMulta(veiculo.getPlaca());
        assertEquals(1,veiculo.getQtdMultas());
    }

    @Test
    @DisplayName("Quando não houver veiculo cadastrado com a placa informada, deve retornar erro")
    void excluir_veiculoNaoCadastrado() {
        Mockito.when(repo.findByPlaca(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(RegistroNaoEncontradoException.class,()->service.excluir("abcd1234"));
    }

    @Test
    @DisplayName("Quando houver veiculo cadastrado sem multas com a placa informada, deve excluir o mesmo")
    void excluir_veiculoSemMultas() {
        Veiculo veiculo = new Veiculo("abcd1234","moto","cinza",1980,0);
        Mockito.when(repo.findByPlaca(Mockito.anyString())).thenReturn(Optional.of(veiculo));
        assertDoesNotThrow(()->service.excluir(veiculo.getPlaca()));
    }

    @Test
    @DisplayName("Quando houver veiculo cadastrado com multas com a placa informada, deve excluir o mesmo")
    void excluir_veiculoComMultas() {
        Veiculo veiculo = new Veiculo("abcd1234","moto","cinza",1980,1);
        Mockito.when(repo.findByPlaca(Mockito.anyString())).thenReturn(Optional.of(veiculo));
        assertThrows(FalhaExclusaoVeiculoComMultasException.class,()->service.excluir(veiculo.getPlaca()));
    }
    
}


