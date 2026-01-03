package com.banco.api.faturas.model.config;

import com.banco.api.faturas.model.Fatura;
import com.banco.api.faturas.model.dto.FaturaDTO;
import com.banco.api.faturas.processor.FaturaProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class BatchConfig {

    @Bean
    public Step faturaStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           @Qualifier("faturaReaderBean") ItemReader<FaturaDTO> reader,
                           FaturaProcessor processor,
                           @Qualifier("faturaWriterBean") ItemWriter<Fatura> writer){

        return new StepBuilder("faturaStep", jobRepository)
                .<FaturaDTO, Fatura>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job faturaJob(JobRepository jobRepository, Step faturaStep){
        return new JobBuilder("geracaoFaturaJob", jobRepository)
                .start(faturaStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
