package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.application.PedidoApplicationService;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.gersystem.transporte.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoApplicationService pedidoApplicationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Crear pedido con datos válidos debe retornar 200 OK")
    void crearPedido_ConDatosValidos_DebeRetornar200() throws Exception {
        // Arrange
        CreatePedidoDTO createPedidoDTO = new CreatePedidoDTO();
        createPedidoDTO.setDescripcion("Pedido de prueba");
        createPedidoDTO.setPeso(new BigDecimal("500.00"));

        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setDescripcion("Pedido de prueba");
        pedidoDTO.setPeso(new BigDecimal("500.00"));
        pedidoDTO.setEstado(EstadoPedido.PENDIENTE);

        when(pedidoApplicationService.crearPedido(any(CreatePedidoDTO.class), eq(1L)))
                .thenReturn(pedidoDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/pedidos")
                        .param("vehiculoId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPedidoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descripcion").value("Pedido de prueba"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "CONDUCTOR")
    @DisplayName("Actualizar estado de pedido debe retornar 200 OK")
    void actualizarEstado_ConDatosValidos_DebeRetornar200() throws Exception {
        // Arrange
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setEstado(EstadoPedido.EN_PROCESO);

        when(pedidoApplicationService.actualizarEstado(eq(1L), eq(EstadoPedido.EN_PROCESO)))
                .thenReturn(pedidoDTO);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/pedidos/1/estado")
                        .param("nuevoEstado", "EN_PROCESO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Listar pedidos debe retornar 200 OK")
    void listarPedidos_DebeRetornar200() throws Exception {
        // Arrange
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setDescripcion("Pedido de prueba");
        pedidoDTO.setEstado(EstadoPedido.PENDIENTE);

        Page<PedidoDTO> page = new PageImpl<>(List.of(pedidoDTO));

        when(pedidoApplicationService.listarPedidos(any(), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos")
                        .param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Obtener pedido por ID debe retornar 200 OK")
    void obtenerPedido_ConIdValido_DebeRetornar200() throws Exception {
        // Arrange
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setDescripcion("Pedido de prueba");
        pedidoDTO.setEstado(EstadoPedido.PENDIENTE);

        when(pedidoApplicationService.obtenerPedido(1L))
                .thenReturn(pedidoDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("Crear pedido con datos inválidos debe retornar 400 Bad Request")
    void crearPedido_ConDatosInvalidos_DebeRetornar400() throws Exception {
        // Arrange
        CreatePedidoDTO createPedidoDTO = new CreatePedidoDTO();
        // No establecemos descripción ni peso para provocar error de validación

        // Act & Assert
        mockMvc.perform(post("/api/v1/pedidos")
                        .param("vehiculoId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPedidoDTO)))
                .andExpect(status().isBadRequest());
    }
} 