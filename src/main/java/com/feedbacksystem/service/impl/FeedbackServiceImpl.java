package com.feedbacksystem.service.impl;

import com.feedbacksystem.dto.FeedbackRequest;
import com.feedbacksystem.dto.FeedbackResponse;
import com.feedbacksystem.entity.Course;
import com.feedbacksystem.entity.Feedback;
import com.feedbacksystem.entity.User;
import com.feedbacksystem.exception.ResourceNotFoundException;
import com.feedbacksystem.repository.CourseRepository;
import com.feedbacksystem.repository.FeedbackRepository;
import com.feedbacksystem.repository.UserRepository;
import com.feedbacksystem.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public FeedbackResponse submitFeedback(Long studentId, FeedbackRequest request) {
        // Validate student exists
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        // Validate course exists
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        Feedback feedback = Feedback.builder()
                .studentId(studentId)
                .courseId(request.getCourseId())
                .rating(request.getRating())
                .comments(request.getComments())
                .build();

        Feedback saved = feedbackRepository.save(feedback);
        return toResponse(saved, student, course);
    }

    @Override
    public List<FeedbackResponse> getFeedbackByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        return feedbackRepository.findByStudentId(studentId).stream()
                .map(fb -> {
                    Course course = courseRepository.findById(fb.getCourseId()).orElse(null);
                    return toResponse(fb, student, course);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getFeedbackByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        return feedbackRepository.findByCourseId(courseId).stream()
                .map(fb -> {
                    User student = userRepository.findById(fb.getStudentId()).orElse(null);
                    return toResponse(fb, student, course);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getFeedbackByTeacher(Long teacherId) {
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        List<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());

        return feedbackRepository.findByCourseIdIn(courseIds).stream()
                .map(fb -> {
                    User student = userRepository.findById(fb.getStudentId()).orElse(null);
                    Course course = courses.stream()
                            .filter(c -> c.getId().equals(fb.getCourseId()))
                            .findFirst().orElse(null);
                    return toResponse(fb, student, course);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getAllFeedback() {
        return feedbackRepository.findAll().stream()
                .map(fb -> {
                    User student = userRepository.findById(fb.getStudentId()).orElse(null);
                    Course course = courseRepository.findById(fb.getCourseId()).orElse(null);
                    return toResponse(fb, student, course);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", feedbackId));
        feedbackRepository.delete(feedback);
    }

    private FeedbackResponse toResponse(Feedback fb, User student, Course course) {
        return FeedbackResponse.builder()
                .id(fb.getId())
                .studentId(fb.getStudentId())
                .studentName(student != null ? student.getName() : "Unknown")
                .courseId(fb.getCourseId())
                .courseName(course != null ? course.getCourseName() : "Unknown")
                .rating(fb.getRating())
                .comments(fb.getComments())
                .createdAt(fb.getCreatedAt())
                .build();
    }
}
