package com.banco.api.faturas.model.config;

import com.banco.api.faturas.model.Fatura;
import com.banco.api.faturas.model.dto.FaturaDTO;
import com.banco.api.faturas.processor.FaturaProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
public class BatchConfig {

    @Bean
    @StepScope
    public JdbcCursorItemReader<FaturaDTO> faturaReader (
            DataSource dataSource,
            @Value("#{jobParameters['mes']}") Integer mes,
            @Value("#{jobParameters['ano']}") Integer ano){ // ESTA TRAZENDO OS DADOS AGRUPADOS DO MYSQL

        String sql = String.format(
                "SELECT conta_id, SUM(valor) as total FROM transacoes " +
                 "WHERE MONTH(data_transacao) = %d AND YEAR(data_transacao) = %d " +
                 "GROUP BY conta_id", mes, ano
        );

        return new JdbcCursorItemReaderBuilder<FaturaDTO>()
                .name("faturaReader")
                .dataSource(dataSource)
                .sql(sql)
                .rowMapper(new DataClassRowMapper<>(FaturaDTO.class))
                .build();

    }

    @Bean
    public ItemProcessor<FaturaDTO, Fatura> faturaProcessor(){ //TRANSFORMA O DTO EM FATURA
        return dto -> {
            return Fatura.builder()
                    .contaId(dto.contaId())
                    .valorTotal(dto.total())
                    .dataCriacao(LocalDateTime.now())
                    .status("FECHADA")
                    .mes(LocalDateTime.now().getMonthValue())
                    .ano(LocalDateTime.now().getYear())
                    .build();
        };
    }

    @Bean
    public MongoItemWriter<Fatura> faturaWriter(MongoTemplate mongoTemplate){ //SALVA NO MONGO
        return new MongoItemWriterBuilder<Fatura>()
                .template(mongoTemplate)
                .collection("faturas")
                .build();
    }

    @Bean
    public Step faturaStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           JdbcCursorItemReader<FaturaDTO> reader,
                           FaturaProcessor processor,
                           MongoItemWriter<Fatura> writer){

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
