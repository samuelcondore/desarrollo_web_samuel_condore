package com.example.appweb.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    Page<Comentario> findAllByActividadOrderByIdDesc(Actividad actividad, Pageable pageable);

    long countByActividad(Actividad actividad);
}
