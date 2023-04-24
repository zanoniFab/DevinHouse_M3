package com.example.exerciciosmodulo3s01.repository;

import com.example.exerciciosmodulo3s01.model.Veiculo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class VeiculoRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private VeiculoRepository repo;

    @Test
    @DisplayName("Quando informada placa existente, deve retornar true")
    void existsVeiculoByPlaca_existente() {
        Veiculo v = new Veiculo("abc1234","carro","cinza",1980,0);
        em.persist(v);
        boolean resp = repo.existsVeiculoByPlaca(v.getPlaca());
        assertEquals(true,resp);
    }

    @Test
    @DisplayName("Quando informada placa que não existe, deve retornar false")
    void existsVeiculoByPlaca_naoExistente() {
        boolean resp = repo.existsVeiculoByPlaca("");
        assertEquals(false,resp);
    }
}


//    boolean existsVeiculoByPlaca(String placa);
//
//    Optional<Veiculo> findByPlaca(String placa);