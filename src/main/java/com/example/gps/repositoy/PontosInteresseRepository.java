package com.example.gps.repositoy;

import com.example.gps.model.PontosInteresse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PontosInteresseRepository extends JpaRepository<PontosInteresse, Long> {

    @Query(
            """
                SELECT p FROM PontosInteresse p
                WHERE (p.x >= :xMin AND p.x <= :xMax AND p.y >= :yMin AND p.y <= :yMax)
            """
    )
    Page<PontosInteresse> findPontosInteresseProximos
            (
                    @Param("xMin") long xMin,
                    @Param("xMax") long xMax,
                    @Param("yMin") long yMin,
                    @Param("yMax") long yMax,
                    Pageable pageable
            );

}
