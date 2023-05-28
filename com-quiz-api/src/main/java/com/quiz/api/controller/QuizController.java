package com.quiz.api.controller;

import com.quiz.api.entity.Quiz;
import com.quiz.api.service.QuizService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Duration;

@RestController
@RequestMapping("/quizzes")
@Slf4j
public class QuizController {
    /**
     * using bucket for user can hit api only 50 time in 24 hour.
     *    If user hit Api in 24 hours of 51 then throw Exception
     */
     private final Bucket bucket;
     public  QuizController(){

             Bandwidth limit = Bandwidth.classic(50, Refill.greedy(50, Duration.ofHours(24)));
             this.bucket = Bucket.builder()
                     .addLimit(limit)
                     .build();
         }
    @Autowired
    private QuizService quizService;

    @PostMapping()
    public ResponseEntity<?> createQuizzes(@RequestBody final Quiz quiz) throws ParseException {
       if (bucket.tryConsume(1)) {
           log.info(quiz.toString());
           return quizService.createQuizzes(quiz);
       }
       return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("you have tried to 5 request in 1 min please try again after some time");

    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveQuizzes(){
        return quizService.getActiveQuizzes();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllQuizzes(){
        return quizService.getAllQuizzes();
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<?> getQuizResultById(@PathVariable("id") long id){
        return quizService.getQuizResultById(id);
    }
}
