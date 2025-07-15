package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.application.VehiculoApplicationService;
import org.gersystem.transporte.config.TestJpaConfig;
import org.gersystem.transporte.config.TestSecurityConfig;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.VehiculoDTO;
import org.gersystem.transporte.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiculoController.class)
@Import({TestJpaConfig.class, TestSecurityConfig.class})
@ActiveProfiles("test")
class VehiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehiculoApplicationService vehiculoApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void debeObtenerVehiculoPorIdExitosamente() throws Exception {
        // Arrange
        Long vehiculoId = 1L;
        VehiculoDTO vehiculoDTO = new VehiculoDTO();
        vehiculoDTO.setId(vehiculoId);
        vehiculoDTO.setPlaca("ABC-123");
        vehiculoDTO.setCapacidad(new BigDecimal("1000"));

        given(vehiculoApplicationService.obtenerVehiculoPorId(vehiculoId)).willReturn(vehiculoDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehiculos/{id}", vehiculoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(vehiculoId))
                .andExpect(jsonPath("$.placa").value("ABC-123"));
    }
    
    @Test
    void debeDevolver401CuandoNoAutenticado() throws Exception {
        // Arrange
        Long vehiculoId = 1L;

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehiculos/{id}", vehiculoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
} 