package com.civicwatch.controller;

import com.civicwatch.model.Complaint;
import com.civicwatch.model.Role;
import com.civicwatch.model.User;
import com.civicwatch.service.ComplaintService;
import com.civicwatch.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private ComplaintService complaintService;

    public static String UPLOAD_DIRECTORY =
            System.getProperty("user.dir") + "/uploads";

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService.login(email, password);

        if (user != null) {
            session.setAttribute("user", user);

            if (user.getRole() == Role.CITIZEN)
                return "redirect:/citizen";

            if (user.getRole() == Role.OFFICER)
                return "redirect:/officer";

            if (user.getRole() == Role.ADMIN)
                return "redirect:/admin";
        }

        model.addAttribute("error", "Invalid credentials");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {
        user.setRole(Role.CITIZEN);
        userService.register(user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ================= CITIZEN =================

    @GetMapping("/citizen")
    public String citizenDashboard(
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.CITIZEN)
            return "redirect:/login";

        model.addAttribute(
                "complaints",
                complaintService.getComplaintsByCitizen(user)
        );

        return "citizen";
    }

    @GetMapping("/complaint-form")
    public String complaintForm(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.CITIZEN)
            return "redirect:/login";

        return "complaint-form";
    }

    @PostMapping("/submit-complaint")
    public String submitComplaint(
            Complaint complaint,
            @RequestParam("image") MultipartFile file,
            HttpSession session) throws IOException {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.CITIZEN)
            return "redirect:/login";

        if (!file.isEmpty()) {

            File uploadDir = new File(UPLOAD_DIRECTORY);

            if (!uploadDir.exists())
                uploadDir.mkdir();

            String filename =
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path fileNameAndPath =
                    Paths.get(UPLOAD_DIRECTORY, filename);

            Files.write(fileNameAndPath, file.getBytes());

            complaint.setImagePath("/uploads/" + filename);
        }

        complaintService.submitComplaint(complaint, user);

        return "redirect:/citizen";
    }

    // ================= OFFICER =================

    @GetMapping("/officer")
    public String officerDashboard(
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.OFFICER)
            return "redirect:/login";

        // Officer sees ONLY assigned complaints
        model.addAttribute(
                "complaints",
                complaintService.getComplaintsByOfficer(user)
        );

        return "officer";
    }

    // ================= ADMIN =================

    @GetMapping("/admin")
    public String adminDashboard(
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.ADMIN)
            return "redirect:/login";

        // Admin sees ALL complaints
        model.addAttribute(
                "complaints",
                complaintService.getAllComplaints()
        );

        // Admin gets list of officers for assignment
        model.addAttribute(
                "officers",
                userService.getOfficers()
        );

        return "admin";
    }

    // Admin assigns complaint to officer
    @PostMapping("/assign-complaint")
    public String assignComplaint(
            @RequestParam Long complaintId,
            @RequestParam String officerId,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getRole() != Role.ADMIN)
            return "redirect:/login";

        complaintService.assignComplaint(
                complaintId,
                officerId,
                user
        );

        return "redirect:/admin";
    }

    // ================= COMPLAINT DETAILS =================

    @GetMapping("/complaint-details/{id}")
    public String complaintDetails(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null)
            return "redirect:/login";

        Complaint complaint =
                complaintService.getComplaintById(id);

        if (complaint == null)
            return "redirect:/";

        // Citizen can only view own complaint
        if (user.getRole() == Role.CITIZEN) {

            if (complaint.getCitizen() == null ||
                    !complaint.getCitizen()
                            .getId()
                            .equals(user.getId())) {

                return "redirect:/citizen";
            }
        }

        // Officer can only view assigned complaint
        if (user.getRole() == Role.OFFICER) {

            if (complaint.getAssignedOfficer() == null ||
                    !complaint.getAssignedOfficer()
                            .getId()
                            .equals(user.getId())) {

                return "redirect:/officer";
            }
        }

        model.addAttribute("complaint", complaint);
        model.addAttribute("user", user);

        return "complaint-details";
    }

    // ================= STATUS UPDATE =================

    @PostMapping("/update-status")
    public String updateStatus(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam String note,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        // ONLY OFFICERS can change complaint status
        if (user == null || user.getRole() != Role.OFFICER)
            return "redirect:/login";

        complaintService.updateComplaintStatus(
                id,
                status,
                note,
                user
        );

        return "redirect:/officer";
    }
}