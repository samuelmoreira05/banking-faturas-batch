package com.banco.api.faturas.controller;

import com.banco.api.faturas.model.dto.RequisicaoFaturaDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<String> gerarFaturas(@RequestBody RequisicaoFaturaDTO dados) {
        try {
            System.out.printf("Pedido recebido do core: Mês " + dados.mes() + "/" + dados.ano());

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("requestId", dados.requestId())
                    .addLong("ano", (long) dados.ano())
                    .addLong("mes", (long) dados.mes())
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
