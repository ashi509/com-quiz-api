package com.quiz.api.service;

import com.quiz.api.entity.Quiz;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

public interface QuizService {
    ResponseEntity<?> createQuizzes(final Quiz quiz) throws ParseException;
    ResponseEntity<?> getActiveQuizzes();
    ResponseEntity<?> getAllQuizzes();
    ResponseEntity<?> getQuizResultById(final long id);
}
