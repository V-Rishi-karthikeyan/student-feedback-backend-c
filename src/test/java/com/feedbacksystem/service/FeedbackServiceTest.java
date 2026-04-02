package com.feedbacksystem.service;

import com.feedbacksystem.dto.FeedbackRequest;
import com.feedbacksystem.dto.FeedbackResponse;
import com.feedbacksystem.entity.Course;
import com.feedbacksystem.entity.Feedback;
import com.feedbacksystem.entity.Role;
import com.feedbacksystem.entity.User;
import com.feedbacksystem.repository.CourseRepository;
import com.feedbacksystem.repository.FeedbackRepository;
import com.feedbacksystem.repository.UserRepository;
import com.feedbacksystem.service.impl.FeedbackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock private FeedbackRepository feedbackRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private User mockStudent;
    private Course mockCourse;
    private Feedback mockFeedback;

    @BeforeEach
    void setUp() {
        mockStudent = User.builder()
                .id(1L).name("Alice").email("alice@test.com")
                .password("pass").role(Role.STUDENT).build();

        mockCourse = Course.builder()
                .id(10L).courseName("Java 101").teacherId(2L).build();

        mockFeedback = Feedback.builder()
                .id(100L).studentId(1L).courseId(10L)
                .rating(5).comments("Great course!")
                .createdAt(LocalDateTime.now()).build();
    }

    @Test
    void submitFeedback_ValidData_ReturnsFeedbackResponse() {
        FeedbackRequest request = new FeedbackRequest();
        request.setCourseId(10L);
        request.setRating(5);
        request.setComments("Great course!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(mockCourse));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(mockFeedback);

        FeedbackResponse response = feedbackService.submitFeedback(1L, request);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Great course!", response.getComments());
        assertEquals("Alice", response.getStudentName());
        assertEquals("Java 101", response.getCourseName());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void getFeedbackByStudent_ReturnsListOfResponses() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(feedbackRepository.findByStudentId(1L)).thenReturn(List.of(mockFeedback));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(mockCourse));

        List<FeedbackResponse> responses = feedbackService.getFeedbackByStudent(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(5, responses.get(0).getRating());
    }

    @Test
    void getFeedbackByCourse_ReturnsListOfResponses() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(mockCourse));
        when(feedbackRepository.findByCourseId(10L)).thenReturn(List.of(mockFeedback));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockStudent));

        List<FeedbackResponse> responses = feedbackService.getFeedbackByCourse(10L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Java 101", responses.get(0).getCourseName());
    }

    @Test
    void deleteFeedback_ValidId_DeletesSuccessfully() {
        when(feedbackRepository.findById(100L)).thenReturn(Optional.of(mockFeedback));
        doNothing().when(feedbackRepository).delete(mockFeedback);

        assertDoesNotThrow(() -> feedbackService.deleteFeedback(100L));
        verify(feedbackRepository, times(1)).delete(mockFeedback);
    }
}
