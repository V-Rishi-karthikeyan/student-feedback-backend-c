package com.feedbacksystem.service;

import com.feedbacksystem.dto.FeedbackRequest;
import com.feedbacksystem.dto.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    FeedbackResponse submitFeedback(Long studentId, FeedbackRequest request);

    List<FeedbackResponse> getFeedbackByStudent(Long studentId);

    List<FeedbackResponse> getFeedbackByCourse(Long courseId);

    List<FeedbackResponse> getFeedbackByTeacher(Long teacherId);

    List<FeedbackResponse> getAllFeedback();

    void deleteFeedback(Long feedbackId);
}
