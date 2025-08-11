package com.riseup.flimbit.workers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.riseup.flimbit.repository.JobLockRepository;

@Service
public class JobLockService {

    @Autowired
    JobLockRepository repo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean acquire(String jobName) {
    	
    	//System.out.println("inside try lock " + repo.isLocked(jobName));
    	int updated = repo.tryAcquireLock(jobName);
    	//  if it faslse get  1
    	//System.out.println("updated" + updated);
    	 boolean locked  = updated == 0 ?  true : false;
        repo.flush();
        return locked;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void release(String jobName) {
        repo.releaseLock(jobName);
        repo.flush();

    }
}
