package com.civicwatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civicwatch.model.Complaint;
import com.civicwatch.model.User;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByCitizen(User citizen);

    List<Complaint> findByAssignedOfficer(User officer);

    List<Complaint> findByStatus(String status);
}