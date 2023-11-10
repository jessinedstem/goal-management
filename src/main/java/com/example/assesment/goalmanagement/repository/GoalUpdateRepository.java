package com.example.assesment.goalmanagement.repository;

import com.example.assesment.goalmanagement.model.GoalUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface GoalUpdateRepository extends JpaRepository<GoalUpdate, Long> {
}
