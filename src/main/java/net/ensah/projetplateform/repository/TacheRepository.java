package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.Taches;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface TacheRepository extends JpaRepository<Taches, Long> {

    @Query("SELECT t FROM Taches t WHERE t.annotateur.login = :login AND t.isFinished = false ")
    Page<Taches> findByAnnotateurLogin(String login, Pageable pageable);

    @Query("SELECT t FROM Taches t WHERE t.annotateur.login = :login AND t.dataset.nomDataset LIKE %:keyword% AND t.isFinished = false ")
    Page<Taches> findByAnnotateurLoginAndKeyword(@Param("login") String login, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t FROM Taches t WHERE t.isFinished = false ")
    List<Taches> findByIsFinishedFalse();

    List<Taches> findByIsFinishedFalseAndDateLimiteBefore(Date today);

    @Query("SELECT COUNT(t) FROM Taches t WHERE t.annotateur.login = :username AND t.isFinished = false")
    long countByAnnotateurLoginAndIsFinishedFalse(@Param("username") String username);

    @Query("SELECT COUNT(t) FROM Taches t WHERE t.annotateur.login = :username AND t.isFinished = true")
    long countByAnnotateurLoginAndIsFinishedTrue(@Param("username") String username);
}