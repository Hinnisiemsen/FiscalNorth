package de.fiscalnorth.goal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.service.HouseholdScopeService;
import de.fiscalnorth.goal.dto.ApplyPlanRequest;
import de.fiscalnorth.goal.dto.CreateGoalRequest;
import de.fiscalnorth.goal.dto.GoalOverview;
import de.fiscalnorth.goal.dto.GoalPlanResponse;
import de.fiscalnorth.goal.dto.GoalWithProgress;
import de.fiscalnorth.goal.dto.InterviewAnswersRequest;
import de.fiscalnorth.goal.dto.InterviewSessionResponse;
import de.fiscalnorth.goal.dto.RecommendedGoalDto;
import de.fiscalnorth.goal.dto.UpdateGoalRequest;
import de.fiscalnorth.goal.model.FinancialGoal;
import de.fiscalnorth.goal.model.GoalInterviewSession;
import de.fiscalnorth.goal.model.GoalStatus;
import de.fiscalnorth.goal.model.InterviewSessionStatus;
import de.fiscalnorth.goal.repository.FinancialGoalRepository;
import de.fiscalnorth.goal.repository.GoalInterviewSessionRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {

    private final FinancialGoalRepository goalRepository;
    private final GoalInterviewSessionRepository sessionRepository;
    private final DepositAccountRepository depositAccountRepository;
    private final GoalProgressService progressService;
    private final GoalPlanningService planningService;
    private final CurrentUserService currentUserService;
    private final HouseholdScopeService householdScopeService;
    private final ObjectMapper objectMapper;

    public List<GoalWithProgress> getAllGoalsWithProgress() {
        return goalRepository.findAllByHouseholdId(householdScopeService.requireHouseholdId()).stream()
                .map(progressService::toGoalWithProgress)
                .toList();
    }

    public List<GoalWithProgress> getAllGoalsWithProgressForOwner(Long ownerId) {
        return getAllGoalsWithProgress();
    }

    public GoalOverview getOverview() {
        List<GoalWithProgress> goals = getAllGoalsWithProgress();
        int totalGoals = goals.size();
        int activeGoals = (int) goals.stream()
                .filter(g -> g.status() == GoalStatus.ACTIVE)
                .count();
        int completedCount = (int) goals.stream()
                .filter(g -> g.status() == GoalStatus.COMPLETED)
                .count();

        BigDecimal totalTarget = goals.stream()
                .map(GoalWithProgress::targetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProgress = goals.stream()
                .map(GoalWithProgress::progressAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal overallPercent = progressService.computeProgressPercent(totalProgress, totalTarget);

        BigDecimal totalRemaining = goals.stream()
                .map(GoalWithProgress::remainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new GoalOverview(totalGoals, activeGoals, completedCount, overallPercent, totalRemaining);
    }

    public GoalWithProgress getGoalById(Long id) {
        FinancialGoal goal = goalRepository.findByIdAndHouseholdId(id, householdScopeService.requireHouseholdId())
                .orElseThrow(() -> new RessourceNotFoundException("FinancialGoal", "id", id));
        return progressService.toGoalWithProgress(goal);
    }

    @Transactional
    public GoalWithProgress createGoal(CreateGoalRequest request) {
        User owner = currentUserService.getCurrentUser();
        Household household = householdScopeService.requireHousehold();
        validateLinkedAccount(request.linkedAccountId(), household.getId());

        FinancialGoal goal = new FinancialGoal();
        goal.setName(request.name());
        goal.setGoalType(request.goalType());
        goal.setTargetAmount(request.targetAmount());
        goal.setCurrentAmount(request.currentAmount() != null ? request.currentAmount() : BigDecimal.ZERO);
        goal.setTargetDate(request.targetDate());
        goal.setLinkedAccountId(request.linkedAccountId());
        goal.setMonthlyContribution(request.monthlyContribution());
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setOwner(owner);
        goal.setHousehold(household);

        return progressService.toGoalWithProgress(goalRepository.save(goal));
    }

    @Transactional
    public GoalWithProgress updateGoal(Long id, UpdateGoalRequest request) {
        FinancialGoal goal = goalRepository.findByIdAndHouseholdId(id, householdScopeService.requireHouseholdId())
                .orElseThrow(() -> new RessourceNotFoundException("FinancialGoal", "id", id));

        if (request.name() != null) {
            goal.setName(request.name());
        }
        if (request.goalType() != null) {
            goal.setGoalType(request.goalType());
        }
        if (request.targetAmount() != null) {
            goal.setTargetAmount(request.targetAmount());
        }
        if (request.currentAmount() != null) {
            goal.setCurrentAmount(request.currentAmount());
        }
        if (request.targetDate() != null) {
            goal.setTargetDate(request.targetDate());
        }
        if (request.linkedAccountId() != null) {
            validateLinkedAccount(request.linkedAccountId(), householdScopeService.requireHouseholdId());
            goal.setLinkedAccountId(request.linkedAccountId());
        }
        if (request.monthlyContribution() != null) {
            goal.setMonthlyContribution(request.monthlyContribution());
        }
        if (request.status() != null) {
            goal.setStatus(request.status());
        }

        return progressService.toGoalWithProgress(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(Long id) {
        FinancialGoal goal = goalRepository.findByIdAndHouseholdId(id, householdScopeService.requireHouseholdId())
                .orElseThrow(() -> new RessourceNotFoundException("FinancialGoal", "id", id));
        goalRepository.delete(goal);
    }

    @Transactional
    public InterviewSessionResponse startInterview() {
        User owner = currentUserService.getCurrentUser();
        GoalInterviewSession session = new GoalInterviewSession();
        session.setAnswersJson("{}");
        session.setStatus(InterviewSessionStatus.IN_PROGRESS);
        session.setOwner(owner);
        session = sessionRepository.save(session);
        return toSessionResponse(session);
    }

    @Transactional
    public InterviewSessionResponse saveInterviewAnswers(Long sessionId, InterviewAnswersRequest request) {
        GoalInterviewSession session = getSessionForCurrentUser(sessionId);
        try {
            session.setAnswersJson(objectMapper.writeValueAsString(request.answers()));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid answers", e);
        }
        return toSessionResponse(sessionRepository.save(session));
    }

    @Transactional
    public GoalPlanResponse generatePlan(Long sessionId) {
        GoalInterviewSession session = getSessionForCurrentUser(sessionId);
        Map<String, Object> answers = parseAnswers(session.getAnswersJson());
        GoalPlanResponse plan = planningService.generatePlan(answers);
        try {
            session.setPlanJson(objectMapper.writeValueAsString(plan));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to persist plan", e);
        }
        session.setStatus(InterviewSessionStatus.PLAN_READY);
        sessionRepository.save(session);
        return plan;
    }

    @Transactional
    public List<GoalWithProgress> applyPlan(Long sessionId, ApplyPlanRequest request) {
        GoalInterviewSession session = getSessionForCurrentUser(sessionId);
        User owner = session.getOwner();
        Household household = householdScopeService.requireHouseholdForUser(owner);

        List<GoalWithProgress> created = request.goals().stream()
                .map(dto -> {
                    validateLinkedAccount(dto.linkedAccountId(), household.getId());
                    FinancialGoal goal = new FinancialGoal();
                    goal.setName(dto.name());
                    goal.setGoalType(dto.goalType());
                    goal.setTargetAmount(dto.targetAmount());
                    goal.setCurrentAmount(BigDecimal.ZERO);
                    goal.setTargetDate(dto.targetDate());
                    goal.setLinkedAccountId(dto.linkedAccountId());
                    goal.setMonthlyContribution(dto.monthlyContribution());
                    goal.setStatus(GoalStatus.ACTIVE);
                    goal.setOwner(owner);
                    goal.setHousehold(household);
                    return progressService.toGoalWithProgress(goalRepository.save(goal));
                })
                .toList();

        session.setStatus(InterviewSessionStatus.COMPLETED);
        sessionRepository.save(session);
        return created;
    }

    private GoalInterviewSession getSessionForCurrentUser(Long sessionId) {
        return sessionRepository.findByIdAndOwnerId(sessionId, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("GoalInterviewSession", "id", sessionId));
    }

    private InterviewSessionResponse toSessionResponse(GoalInterviewSession session) {
        GoalPlanResponse plan = null;
        if (session.getPlanJson() != null && !session.getPlanJson().isBlank()) {
            try {
                plan = objectMapper.readValue(session.getPlanJson(), GoalPlanResponse.class);
            } catch (JsonProcessingException ignored) {
                // plan may be invalid; return null
            }
        }
        return new InterviewSessionResponse(
                session.getId(),
                session.getStatus(),
                session.getAnswersJson(),
                plan);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseAnswers(String answersJson) {
        if (answersJson == null || answersJson.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(answersJson, Map.class);
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }

    private void validateLinkedAccount(Long linkedAccountId, Long householdId) {
        if (linkedAccountId != null) {
            depositAccountRepository.findByIdAndHouseholdId(linkedAccountId, householdId)
                    .orElseThrow(() -> new RessourceNotFoundException("DepositAccount", "id", linkedAccountId));
        }
    }
}
