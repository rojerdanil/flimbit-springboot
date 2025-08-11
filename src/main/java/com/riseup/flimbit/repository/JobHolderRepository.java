package com.riseup.flimbit.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.constant.SchedularNames;
import com.riseup.flimbit.entity.JobHolder;

import java.util.List;
import java.util.Optional;
@Repository
public interface JobHolderRepository extends JpaRepository<JobHolder, Integer> {

	@Query("SELECT f FROM JobHolder f " +
		       "WHERE LOWER(f.status) = 'active' " +
		       "AND LOWER(f.jobName) = LOWER(:jobName) " +
		       "ORDER BY f.createdDate ASC")
		Optional<JobHolder> findNextActiveJobByJobName(String jobName);
    
    JobHolder findByMovieIdAndJobNameIgnoreCase(Integer movieId,String jobName);
    

    
    
}
