package com.example.hr_assistant.repository;

import com.example.hr_assistant.model.ModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с версиями моделей
 */
@Repository
public interface ModelVersionRepository extends JpaRepository<ModelVersion, Long> {
    
    List<ModelVersion> findByModelName(String modelName);
    
    Optional<ModelVersion> findByModelNameAndVersion(String modelName, String version);
    
    @Query("SELECT m FROM ModelVersion m WHERE m.isActive = true")
    List<ModelVersion> findActiveModels();
    
    @Query("SELECT m FROM ModelVersion m WHERE m.modelName = :modelName AND m.isActive = true")
    Optional<ModelVersion> findActiveByModelName(@Param("modelName") String modelName);
}
