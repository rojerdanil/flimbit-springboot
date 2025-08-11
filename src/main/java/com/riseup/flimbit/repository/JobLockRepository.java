package com.riseup.flimbit.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.JobLockEntity;

import jakarta.persistence.LockModeType;

@Repository
public interface JobLockRepository extends JpaRepository<JobLockEntity, String> {

	@Modifying
	@Query(value = "UPDATE job_lock SET locked = TRUE, locked_at = NOW() " +
	               "WHERE job_name = :jobName AND locked = FALSE", nativeQuery = true)
	int tryAcquireLock(@Param("jobName") String jobName);

	@Modifying
	@Query(value = "UPDATE job_lock SET locked = FALSE WHERE job_name = :jobName", nativeQuery = true)
	void releaseLock(@Param("jobName") String jobName);
	
	
	@Query(value = "SELECT locked FROM job_lock WHERE job_name = :jobName", nativeQuery = true)
	boolean isLocked(@Param("jobName") String jobName);
}

