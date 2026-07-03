package de.fiscalnorth.goal.model;

import de.fiscalnorth.shared.BaseEntity;
import de.fiscalnorth.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GoalInterviewSession extends BaseEntity {
    @Lob
    @Column(columnDefinition = "TEXT")
    private String answersJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String planJson;

    @Enumerated(EnumType.STRING)
    private InterviewSessionStatus status = InterviewSessionStatus.IN_PROGRESS;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
