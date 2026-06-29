package com.example.appweb.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    Page<Actividad> findAllByOrderByIdDesc(Pageable pageable);

    @Query(value = "SELECT tipo, COUNT(id) AS actividades FROM actividad GROUP BY tipo",
            nativeQuery = true)
    List<ActividadAgrupadasTipo> getActivitiesGroupedByTipo();

    @Query(value = "SELECT C.nombre AS comuna, COUNT(A.id) AS actividades FROM actividad A JOIN miembro M ON M.id = A.miembro_id JOIN comuna C ON M.comuna_id = C.id GROUP BY C.nombre",
            nativeQuery = true)
    List<ActividadAgrupadasComuna> getActivitiesGroupedByComuna();

    List<Actividad> findAllByMiembro(Miembro miembro);

    long countByMiembro(Miembro miembro);

    @Query(value = "SELECT A.* FROM actividad A JOIN miembro M ON A.miembro_id = M.id JOIN comuna C ON M.comuna_id = C.id WHERE C.nombre LIKE %:q% OR A.nombre LIKE %:q% OR A.descripcion LIKE %:q% ORDER BY A.id DESC",
    nativeQuery = true)
    List<Actividad> freeSearch(@Param("q") String query);
}