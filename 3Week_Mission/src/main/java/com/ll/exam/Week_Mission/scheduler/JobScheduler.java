package com.ll.exam.Week_Mission.scheduler;

import lombok.AllArgsConstructor;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Component
@EnableScheduling
@AllArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;

    private final Job makeRebateOrderItemJob;

    //@Scheduled(cron = "0 0 4 15 * *") // 매달 15일 새벽 4시
    @Scheduled(cron = "1 * * * * *") // 매분 1초마다 테스트
    public void jobScheduler(){

        // jobParameters.yearMonth를 한 달 전으로 설정
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH , -1);
        String yearMonth = new SimpleDateFormat("YYYY-MM").format(cal.getTime());

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
