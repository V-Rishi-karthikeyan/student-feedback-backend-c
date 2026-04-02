package com.feedbacksystem.controller;

import com.feedbacksystem.entity.Course;
import com.feedbacksystem.entity.User;
import com.feedbacksystem.exception.ResourceNotFoundException;
import com.feedbacksystem.repository.CourseRepository;
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
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserService userService;

    /**
     * Get all courses (any authenticated user)
     */
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    /**
     * Get course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        return ResponseEntity.ok(course);
    }

    /**
     * Teacher: Get courses assigned to the current teacher
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Course>> getMyCourses(Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(courseRepository.findByTeacherId(teacher.getId()));
    }

    /**
     * Admin: Create a new course
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        Course saved = courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Admin: Update a course
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        existing.setCourseName(courseDetails.getCourseName());
        existing.setTeacherId(courseDetails.getTeacherId());
        return ResponseEntity.ok(courseRepository.save(existing));
    }

    /**
     * Admin: Delete a course
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        courseRepository.delete(course);
        return ResponseEntity.noContent().build();
    }
}
