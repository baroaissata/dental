package com.saas.dental_clinic.dto;


import com.saas.dental_clinic.model.Dentiste;
import lombok.Data;

@Data
public class CreateDentisteRequest {
    private Dentiste dentiste;
    private String password;
}
