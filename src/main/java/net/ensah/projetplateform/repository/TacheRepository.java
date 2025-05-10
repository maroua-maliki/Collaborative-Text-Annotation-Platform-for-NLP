package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.Taches;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TacheRepository extends JpaRepository<Taches, Long> {


    Page<Taches> findByAnnotateurLogin(String login, Pageable pageable);

    @Query("SELECT t FROM Taches t WHERE t.annotateur.login = :login AND t.dataset.nomDataset LIKE %:keyword%")
    Page<Taches> findByAnnotateurLoginAndKeyword(@Param("login") String login, @Param("keyword") String keyword, Pageable pageable);
}