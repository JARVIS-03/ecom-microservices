package com.ecom.payment.paymentservice.mapper;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentRequestDTOtoPaymentMapper {
    public static final PaymentRequestDTOtoPaymentMapper INSTANCE =
            Mappers.getMapper(PaymentRequestDTOtoPaymentMapper.class);
    @Mapping(target = "date",expression = "java(java.time.LocalDateTime.now())")
    public abstract Payment map(PaymentRequestDTO requestDTO);
}
