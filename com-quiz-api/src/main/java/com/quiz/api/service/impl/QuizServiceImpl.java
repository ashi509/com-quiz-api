package com.quiz.api.service.impl;

import com.quiz.api.constants.ApiConstant;
import com.quiz.api.entity.Quiz;
import com.quiz.api.exception.CustomException;
import com.quiz.api.repo.QuizRepository;
import com.quiz.api.service.QuizService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final SimpleDateFormat simpleDateFormat;

    /**
     * save a quiz
     * validate start date and end date
     * @param quiz
     * @return
     */
    @Override
    public ResponseEntity<?> createQuizzes(Quiz quiz) throws ParseException {
        quiz.setStartDateTime(simpleDateFormat.parse(quiz.getStartDate()));
        quiz.setEndDateTime(simpleDateFormat.parse(quiz.getEndDate()));
        if(!quiz.getStartDateTime().before(quiz.getEndDateTime()))
            throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Start date should be less than end date");
        if(quiz.getRightAnswer()>=quiz.getOptions().size())
            throw new CustomException(HttpStatus.BAD_REQUEST.value(), "Illegal index of the Right answer");
        long currentTime=System.currentTimeMillis();
        /**
         * if current time is < quiz start time that means quiz is inactive
         */
        if(currentTime<quiz.getStartDateTime().getTime())
            quiz.setStatus(ApiConstant.IN_ACTIVE);
        /**
         * if(quiz start time 7 < current time AND quiz end time>current time)
         * that means quiz is active
         */
        else if(quiz.getStartDateTime().getTime()<currentTime && quiz.getEndDateTime().getTime()>currentTime)
            quiz.setStatus(ApiConstant.ACTIVE);
        /**
         * if  (current time is>quiz end time) that means the quiz is finished
         */
        else if(quiz.getEndDateTime().getTime()<currentTime)
            quiz.setStatus(ApiConstant.FINISHED);
        quiz.setId(0);
        var response=quizRepository.save(quiz);
        if(response==null)
            throw new CustomException(HttpStatus.REQUEST_TIMEOUT.value(), "Something went wrong");
        return ResponseEntity.ok(response);
    }

    /**
     * getting all active quizzes
     * active -> start date of the quiz is less than current date and end date is greater than current date
     * @return
     */
    @Override
    public ResponseEntity<?> getActiveQuizzes() {
        var response=quizRepository.findAll().stream().
                filter(quiz -> quiz.getStartDateTime().getTime()<System.currentTimeMillis()
                        && quiz.getEndDateTime().getTime()>System.currentTimeMillis())
                .collect(Collectors.toList());
                response.stream().forEach(quiz -> quiz.setStatus(ApiConstant.ACTIVE));
        return ResponseEntity.ok(response);
    }

    /**
     * getting all quizzes
     * @return
     */
    @Override
    public ResponseEntity<?> getAllQuizzes() {
        var response= quizRepository.findAll();
        response.stream().forEach(quiz -> {
            long currentTime=System.currentTimeMillis();
            if(currentTime<quiz.getStartDateTime().getTime())
                quiz.setStatus(ApiConstant.IN_ACTIVE);
            else if(quiz.getStartDateTime().getTime()<currentTime && quiz.getEndDateTime().getTime()>currentTime)
                quiz.setStatus(ApiConstant.ACTIVE);
            else if(quiz.getEndDateTime().getTime()<currentTime)
                quiz.setStatus(ApiConstant.FINISHED);
        });
        return ResponseEntity.ok(response);
    }

    /**
     * getting result of the quiz id
     * @param id
     * @return
     */
    @Override
    public ResponseEntity<?> getQuizResultById(long id) {
        var response= quizRepository.findById(id).
                orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND.value(),
                        "Result not found of the given id "+id));
        long currentTime=System.currentTimeMillis();
        /**
         *  After 5 min of end quiz time return right answer.
         */
        long quizExpireTime= response.getEndDateTime().getTime()+ TimeUnit.MINUTES.toMillis(5);
        if(!(quizExpireTime<currentTime))
            throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Result unavailable for this time");
        return ResponseEntity.ok(response.getOptions().get(response.getRightAnswer()));
    }
}