package com.feedbacksystem.controller;

import com.feedbacksystem.dto.FeedbackRequest;
import com.feedbacksystem.dto.FeedbackResponse;
import com.feedbacksystem.entity.User;
import com.feedbacksystem.service.FeedbackService;
import com.feedbacksystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserService userService;

    /**
     * Student submits feedback for a course
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<FeedbackResponse> submitFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedbackService.submitFeedback(student.getId(), request));
    }

    /**
     * Student views their own submitted feedback
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedback(Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(feedbackService.getFeedbackByStudent(student.getId()));
    }

    /**
     * Teacher views all feedback for a specific course
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByCourse(courseId));
    }

    /**
     * Teacher views all feedback for their courses
     */
    @GetMapping("/teacher/my-courses")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<FeedbackResponse>> getTeacherFeedback(Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(feedbackService.getFeedbackByTeacher(teacher.getId()));
    }

    /**
     * Admin views all feedback in the system
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackResponse>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    /**
     * Admin deletes a feedback entry
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
