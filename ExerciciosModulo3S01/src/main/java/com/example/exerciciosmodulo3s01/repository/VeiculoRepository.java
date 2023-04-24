package com.example.exerciciosmodulo3s01.repository;

import com.example.exerciciosmodulo3s01.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo,String> {

    boolean existsVeiculoByPlaca(String placa);

    Optional<Veiculo> findByPlaca(String placa);
}
