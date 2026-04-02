package com.feedbacksystem.service.impl;

import com.feedbacksystem.dto.FeedbackResponse;
import com.feedbacksystem.entity.Course;
import com.feedbacksystem.entity.Feedback;
import com.feedbacksystem.entity.Role;
import com.feedbacksystem.entity.User;
import com.feedbacksystem.exception.ResourceNotFoundException;
import com.feedbacksystem.repository.CourseRepository;
import com.feedbacksystem.repository.FeedbackRepository;
import com.feedbacksystem.repository.UserRepository;
import com.feedbacksystem.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final FeedbackRepository feedbackRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public Map<String, Object> getCourseReport(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        List<Feedback> feedbacks = feedbackRepository.findByCourseId(courseId);
        Double avgRating = feedbackRepository.findAverageRatingByCourseId(courseId);
        Long totalFeedbacks = feedbackRepository.countByCourseId(courseId);

        // Rating distribution
        Map<Integer, Long> ratingDistribution = feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getRating, Collectors.counting()));

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("courseId", courseId);
        report.put("courseName", course.getCourseName());
        report.put("totalFeedbacks", totalFeedbacks);
        report.put("averageRating", avgRating != null ? Math.round(avgRating * 100.0) / 100.0 : 0.0);
        report.put("ratingDistribution", ratingDistribution);
        report.put("recentComments", feedbacks.stream()
                .filter(f -> f.getComments() != null && !f.getComments().isEmpty())
                .sorted(Comparator.comparing(Feedback::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(Feedback::getComments)
                .collect(Collectors.toList()));

        return report;
    }

    @Override
    public Map<String, Object> getTeacherReport(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        List<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());

        List<Map<String, Object>> courseReports = new ArrayList<>();
        long totalFeedbacks = 0;
        double totalRatingSum = 0;
        int ratedCourses = 0;

        for (Course course : courses) {
            Double avgRating = feedbackRepository.findAverageRatingByCourseId(course.getId());
            Long count = feedbackRepository.countByCourseId(course.getId());

            Map<String, Object> cr = new LinkedHashMap<>();
            cr.put("courseId", course.getId());
            cr.put("courseName", course.getCourseName());
            cr.put("totalFeedbacks", count);
            cr.put("averageRating", avgRating != null ? Math.round(avgRating * 100.0) / 100.0 : 0.0);
            courseReports.add(cr);

            totalFeedbacks += count;
            if (avgRating != null) {
                totalRatingSum += avgRating;
                ratedCourses++;
            }
        }

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("teacherId", teacherId);
        report.put("teacherName", teacher.getName());
        report.put("totalCourses", courses.size());
        report.put("totalFeedbacks", totalFeedbacks);
        report.put("overallAverageRating", ratedCourses > 0
                ? Math.round((totalRatingSum / ratedCourses) * 100.0) / 100.0 : 0.0);
        report.put("courseReports", courseReports);

        return report;
    }

    @Override
    public Map<String, Object> getAdminSummaryReport() {
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.findByRole(Role.STUDENT).size();
        long totalTeachers = userRepository.findByRole(Role.TEACHER).size();
        long totalCourses = courseRepository.count();
        long totalFeedbacks = feedbackRepository.count();

        List<Feedback> allFeedbacks = feedbackRepository.findAll();
        double overallAvgRating = allFeedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        // Top rated courses
        List<Map<String, Object>> topCourses = courseRepository.findAll().stream()
                .map(course -> {
                    Double avg = feedbackRepository.findAverageRatingByCourseId(course.getId());
                    Long count = feedbackRepository.countByCourseId(course.getId());
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("courseId", course.getId());
                    m.put("courseName", course.getCourseName());
                    m.put("averageRating", avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0);
                    m.put("feedbackCount", count);
                    return m;
                })
                .sorted((a, b) -> Double.compare((Double) b.get("averageRating"), (Double) a.get("averageRating")))
                .limit(5)
                .collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalUsers", totalUsers);
        report.put("totalStudents", totalStudents);
        report.put("totalTeachers", totalTeachers);
        report.put("totalCourses", totalCourses);
        report.put("totalFeedbacks", totalFeedbacks);
        report.put("overallAverageRating", Math.round(overallAvgRating * 100.0) / 100.0);
        report.put("topRatedCourses", topCourses);

        return report;
    }
}
