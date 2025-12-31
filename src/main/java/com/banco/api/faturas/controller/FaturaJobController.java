package com.banco.api.faturas.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/faturas")
public class FaturaJobController {

    private final JobLauncher jobLauncher;
    private final Job faturaJob;

    public FaturaJobController(JobLauncher jobLauncher, Job faturaJob) {
        this.jobLauncher = jobLauncher;
        this.faturaJob = faturaJob;
    }

    @PostMapping("/gerar")
    public ResponseEntity<String> gerarFaturas() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("inicio", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(faturaJob, jobParameters);

            return ResponseEntity.ok("Job de faturas iniciado com sucesso!");
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao iniciar o job: " + e.getMessage());
        }
    }
}
