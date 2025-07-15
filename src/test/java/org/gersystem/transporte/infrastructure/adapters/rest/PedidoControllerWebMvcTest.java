package org.gersystem.transporte.infrastructure.adapters.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gersystem.transporte.application.PedidoApplicationService;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
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
class PedidoControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoApplicationService pedidoApplicationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe crear un pedido exitosamente")
    void crearPedido_DebeCrearExitosamente() throws Exception {
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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe validar el peso del pedido")
    void crearPedido_DebeValidarPeso() throws Exception {
        // Arrange
        CreatePedidoDTO createPedidoDTO = new CreatePedidoDTO();
        createPedidoDTO.setDescripcion("Pedido de prueba");
        createPedidoDTO.setPeso(BigDecimal.ZERO); // Peso inválido

        // Act & Assert
        mockMvc.perform(post("/api/v1/pedidos")
                        .param("vehiculoId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPedidoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("peso")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe actualizar el estado de un pedido exitosamente")
    void actualizarEstado_DebeActualizarExitosamente() throws Exception {
        // Arrange
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setEstado(EstadoPedido.EN_PROCESO);

        when(pedidoApplicationService.actualizarEstado(1L, EstadoPedido.EN_PROCESO))
                .thenReturn(pedidoDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/pedidos/1/estado")
                        .param("nuevoEstado", "EN_PROCESO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe listar pedidos paginados")
    void listarPedidos_DebeListarPaginado() throws Exception {
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
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Debe filtrar pedidos por estado")
    void listarPedidos_DebeFiltrarPorEstado() throws Exception {
        // Arrange
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setEstado(EstadoPedido.EN_PROCESO);

        Page<PedidoDTO> page = new PageImpl<>(List.of(pedidoDTO));

        when(pedidoApplicationService.listarPedidos(eq(EstadoPedido.EN_PROCESO), any(), any()))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos")
                        .param("estado", "EN_PROCESO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].estado").value("EN_PROCESO"));
    }
} 