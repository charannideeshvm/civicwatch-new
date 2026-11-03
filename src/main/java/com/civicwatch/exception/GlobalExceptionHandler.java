package com.civicwatch.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
        return "error"; // We will map this to an error page or reuse login
    }
    
    @ExceptionHandler(ComplaintNotFoundException.class)
    public String handleComplaintNotFound(ComplaintNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}
