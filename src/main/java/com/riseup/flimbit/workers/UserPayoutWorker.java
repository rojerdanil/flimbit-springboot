package com.riseup.flimbit.workers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riseup.flimbit.entity.UserPayoutInitiation;

public class UserPayoutWorker implements Runnable{
	Logger logger = LoggerFactory.getLogger(UserPayoutWorker.class);

	UserPayoutInitiation userPayoutInitiation;
	UserPayoutTask userPayoutTask;
	UserPayoutWorker(UserPayoutInitiation userPayoutInitiation,UserPayoutTask userPayoutTask)
	{
		this.userPayoutInitiation = userPayoutInitiation;
		this.userPayoutTask = userPayoutTask;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	      
		try {
			userPayoutTask.execute(userPayoutInitiation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
