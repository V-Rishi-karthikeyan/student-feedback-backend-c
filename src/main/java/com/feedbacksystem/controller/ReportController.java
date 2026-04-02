package com.feedbacksystem.controller;

import com.feedbacksystem.entity.User;
import com.feedbacksystem.service.ReportService;
import com.feedbacksystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    /**
     * Get report for a specific course (Teacher/Admin)
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getCourseReport(@PathVariable Long courseId) {
        return ResponseEntity.ok(reportService.getCourseReport(courseId));
    }

    /**
     * Teacher: Get report for their own courses
     */
    @GetMapping("/teacher/my-report")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Map<String, Object>> getMyTeacherReport(Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(reportService.getTeacherReport(teacher.getId()));
    }

    /**
     * Admin: Get report for a specific teacher
     */
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTeacherReport(@PathVariable Long teacherId) {
        return ResponseEntity.ok(reportService.getTeacherReport(teacherId));
    }

    /**
     * Admin: Get overall system summary report
     */
    @GetMapping("/admin/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminSummaryReport() {
        return ResponseEntity.ok(reportService.getAdminSummaryReport());
    }
}
