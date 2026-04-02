package com.feedbacksystem.repository;

import com.feedbacksystem.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByStudentId(Long studentId);

    List<Feedback> findByCourseId(Long courseId);

    List<Feedback> findByCourseIdIn(List<Long> courseIds);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.courseId = :courseId")
    Double findAverageRatingByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.courseId = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
}
