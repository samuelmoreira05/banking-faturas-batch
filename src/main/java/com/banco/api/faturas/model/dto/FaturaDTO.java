package com.banco.api.faturas.model.dto;

import java.math.BigDecimal;

public record FaturaDTO(
        Long contaId,
        BigDecimal total
) {
}
