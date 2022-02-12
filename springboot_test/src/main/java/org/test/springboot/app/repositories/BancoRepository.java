package org.test.springboot.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.springboot.app.models.Banco;

import java.util.List;

public interface BancoRepository extends JpaRepository<Banco, Long> {

    /**
     * Methods
     */
//    List<Banco> findAll();

//    Banco findById(Long idBanco);

//    void update(Banco banco);
}
