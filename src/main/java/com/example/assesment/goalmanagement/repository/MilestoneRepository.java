package com.example.assesment.goalmanagement.repository;

import com.example.assesment.goalmanagement.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {}
