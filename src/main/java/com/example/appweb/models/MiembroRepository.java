package com.example.appweb.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface MiembroRepository extends JpaRepository<Miembro, Long> {

    Page<Miembro> findAllByOrderByIdDesc(Pageable pageable);

    @Query(value = "SELECT DATE(fecha_registro) AS fechas, COUNT(id) AS registros FROM miembro WHERE fecha_registro > :cutoff GROUP BY DATE(fecha_registro)",
            nativeQuery = true)
    List<MiembrosAgrupadosFecha> get_members_grouped_by_date(LocalDate cutoff);
}