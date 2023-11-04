package com.example.assesment.goalmanagement.repository;

import com.example.assesment.goalmanagement.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    }