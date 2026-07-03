package de.fiscalnorth.goal.controller;

import de.fiscalnorth.goal.dto.ApplyPlanRequest;
import de.fiscalnorth.goal.dto.CreateGoalRequest;
import de.fiscalnorth.goal.dto.GoalOverview;
import de.fiscalnorth.goal.dto.GoalPlanResponse;
import de.fiscalnorth.goal.dto.GoalWithProgress;
import de.fiscalnorth.goal.dto.InterviewAnswersRequest;
import de.fiscalnorth.goal.dto.InterviewSessionResponse;
import de.fiscalnorth.goal.dto.UpdateGoalRequest;
import de.fiscalnorth.goal.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalWithProgress>> getAllGoals() {
        return ResponseEntity.ok(goalService.getAllGoalsWithProgress());
    }

    @GetMapping("/overview")
    public ResponseEntity<GoalOverview> getOverview() {
        return ResponseEntity.ok(goalService.getOverview());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalWithProgress> getGoalById(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.getGoalById(id));
    }

    @PostMapping
    public ResponseEntity<GoalWithProgress> createGoal(@RequestBody @Valid CreateGoalRequest request) {
        return new ResponseEntity<>(goalService.createGoal(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalWithProgress> updateGoal(
            @PathVariable Long id, @RequestBody UpdateGoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/interview/start")
    public ResponseEntity<InterviewSessionResponse> startInterview() {
        return new ResponseEntity<>(goalService.startInterview(), HttpStatus.CREATED);
    }

    @PutMapping("/interview/{sessionId}/answers")
    public ResponseEntity<InterviewSessionResponse> saveAnswers(
            @PathVariable Long sessionId, @RequestBody InterviewAnswersRequest request) {
        return ResponseEntity.ok(goalService.saveInterviewAnswers(sessionId, request));
    }

    @PostMapping("/interview/{sessionId}/generate-plan")
    public ResponseEntity<GoalPlanResponse> generatePlan(@PathVariable Long sessionId) {
        return ResponseEntity.ok(goalService.generatePlan(sessionId));
    }

    @PostMapping("/interview/{sessionId}/apply-plan")
    public ResponseEntity<List<GoalWithProgress>> applyPlan(
            @PathVariable Long sessionId, @RequestBody ApplyPlanRequest request) {
        return new ResponseEntity<>(goalService.applyPlan(sessionId, request), HttpStatus.CREATED);
    }
}
