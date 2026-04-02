package com.feedbacksystem.service;

import java.util.Map;

public interface ReportService {

    Map<String, Object> getCourseReport(Long courseId);

    Map<String, Object> getTeacherReport(Long teacherId);

    Map<String, Object> getAdminSummaryReport();
}
