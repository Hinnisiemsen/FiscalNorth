package de.fiscalnorth.goal.repository;

import de.fiscalnorth.goal.model.GoalInterviewSession;
import de.fiscalnorth.goal.model.InterviewSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalInterviewSessionRepository extends JpaRepository<GoalInterviewSession, Long> {
    Optional<GoalInterviewSession> findByIdAndOwnerId(Long id, Long ownerId);

    List<GoalInterviewSession> findAllByOwnerIdAndStatus(Long ownerId, InterviewSessionStatus status);
}
