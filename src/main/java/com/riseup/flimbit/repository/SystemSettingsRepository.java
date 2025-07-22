package com.riseup.flimbit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.SystemSettings;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Integer> {

    // Fetch the setting value based on key and group name
	Optional<SystemSettings> findByKeyIgnoreCaseAndGroupNameIgnoreCase(String key, String groupName);
    
    @Query(value = """
           
           select  sy.* from system_settings sy
           WHERE (:search IS NULL OR LOWER(sy.key) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:group IS NULL OR LOWER(sy.group_name) = LOWER(:group))
            """, nativeQuery = true)
    Page<SystemSettings>   fetchSystemSetting(
            @Param("group") String group,
            @Param("search") String search
            , Pageable pageable
        );
    
    @Query(value = "SELECT u.group_name FROM system_settings u GROUP BY u.group_name", nativeQuery = true)
    List<String> getGroups();
    
}
