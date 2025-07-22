package com.riseup.flimbit.entity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "user_share_reward")
public class UserShareReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // ID field for the record

    @Column(name = "user_reward_id", nullable = false)
    private int userRewardId; // Foreign key referencing User Reward

    @Column(name = "movie_id", nullable = false)
    private int movieId; // Movie ID associated with the reward

    @Column(name = "share_type_id", nullable = false)
    private int shareTypeId; // Share Type ID for this record

    @Column(name = "number_of_shares", nullable = false)
    private Integer numberOfShares; // Number of shares allocated

    @Column(name = "allocated_at", nullable = false)
    private Timestamp allocatedAt = new Timestamp(System.currentTimeMillis());

  
}
