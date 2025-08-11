package com.riseup.flimbit.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.MoviePaymentTransaction;

import java.util.List;

@Repository
public interface MoviePaymentTransactionRepository extends JpaRepository<MoviePaymentTransaction, Integer> {

    // Fetch all transactions by user
    List<MoviePaymentTransaction> findByUserId(int userId);

    // Fetch all transactions for a movie
    List<MoviePaymentTransaction> findByMovieId(int movieId);

    // Fetch all pending transactions for a user and movie
    List<MoviePaymentTransaction> findByUserIdAndMovieIdAndStatus(int userId, int movieId, String status);

    // Find by Razorpay Order ID
    MoviePaymentTransaction findByOrderId(String orderId);

    // Find all transactions with specific status
    List<MoviePaymentTransaction> findByStatus(String status);
}
