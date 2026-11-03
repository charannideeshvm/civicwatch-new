package com.civicwatch.service;

import com.civicwatch.model.Complaint;
import com.civicwatch.model.Role;
import com.civicwatch.model.User;
import com.civicwatch.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PriorityService priorityService;

    public Complaint submitComplaint(Complaint complaint, User citizen) {
        complaint.setCitizen(citizen);
        complaint.setStatus("PENDING");

        double score = priorityService.calculateScore(complaint.getSeverity());
        complaint.setPriorityScore(score);
        complaint.setPriorityLevel(priorityService.calculateLevel(score));

        return complaintRepository.save(complaint);
    }

    public List<Complaint> getComplaintsByCitizen(User citizen) {
        return complaintRepository.findByCitizen(citizen);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id).orElse(null);
    }

    // Get complaints assigned to a particular officer
    public List<Complaint> getComplaintsByOfficer(User officer) {
        return complaintRepository.findByAssignedOfficer(officer);
    }

    // Admin assigns a complaint to an officer
    public Complaint assignComplaint(Long complaintId, String officerId, User admin) {

        if (admin == null || admin.getRole() != Role.ADMIN) {
            return null;
        }

        Complaint complaint = complaintRepository.findById(complaintId).orElse(null);

        if (complaint == null) {
            return null;
        }

        User officer = new User();
        officer.setId(officerId);

        complaint.setAssignedOfficer(officer);
        complaint.setStatus("ASSIGNED");

        return complaintRepository.save(complaint);
    }

    // Officer updates status of an assigned complaint
    public Complaint updateComplaintStatus(
            Long id,
            String status,
            String note,
            User officer) {

        if (officer == null || officer.getRole() != Role.OFFICER) {
            return null;
        }

        Optional<Complaint> opt = complaintRepository.findById(id);

        if (opt.isPresent()) {

            Complaint c = opt.get();

            // Only the assigned officer can update the complaint
            if (c.getAssignedOfficer() == null ||
                    !c.getAssignedOfficer().getId().equals(officer.getId())) {
                return null;
            }

            c.setStatus(status);

            if (note != null && !note.isEmpty()) {
                c.setResolutionNote(note);
            }

            return complaintRepository.save(c);
        }

        return null;
    }
}