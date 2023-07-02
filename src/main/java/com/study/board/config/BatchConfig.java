//package com.study.board.config;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//
//@Slf4j // log 사용을 위한 lombok 어노테이션
//@Configuration
//@RequiredArgsConstructor
//public class BatchConfig {
//
//    @Bean
//    public Job testSimpleJob(JobRepository jobRepository, Step testStep1, Step testStep2){
//        return new JobBuilder("testSimpleJob", jobRepository)
//                .start(testStep2)
//                .next(testStep1)
//                .build();
//
//    }
//
//    @Bean
//    public Step testStep1(JobRepository jobRepository, Tasklet testTasklet, PlatformTransactionManager platformTransactionManager){
//        return new StepBuilder("testStep1", jobRepository)
//                .tasklet(testTasklet, platformTransactionManager)
//                .build();
//    }
//
//    @Bean
//    public Step testStep2(JobRepository jobRepository, Tasklet testTasklet, PlatformTransactionManager platformTransactionManager){
//        return new StepBuilder("testStep2", jobRepository)
//                .tasklet(testTasklet, platformTransactionManager)
//                .build();
//    }
//}