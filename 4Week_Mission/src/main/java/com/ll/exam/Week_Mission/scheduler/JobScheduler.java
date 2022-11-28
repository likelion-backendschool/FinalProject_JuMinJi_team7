package com.ll.exam.Week_Mission.scheduler;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;

    private final Job makeRebateOrderItemJob;

    //@Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시 (성공 시 중복인수는 재실행X, 실패 시 다시 실행하도록)
    //@Scheduled(cron = "1 * * * * *") // 매분 1초마다 테스트
    public void jobScheduler(){
        // 현재 날짜가 15일 이전이면 yearMonth를 두 달 전으로, 15일 이후면 한 달 전으로
        LocalDate today = LocalDate.now();
        today = today.isBefore(today.withDayOfMonth(15)) ? today.minusMonths(2) : today.minusMonths(1);
        String  yearMonth = today.format(DateTimeFormatter.ofPattern("YYYY-MM"));

        // jobParameters.yearMonth 생성
        JobParameters jobParameters = new JobParametersBuilder().addString("yearMonth", yearMonth).toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(makeRebateOrderItemJob, jobParameters);
            System.out.println("Job's Status:::"+jobExecution.getStatus());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
}
