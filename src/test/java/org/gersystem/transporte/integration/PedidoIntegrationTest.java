package org.gersystem.transporte.integration;

import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.PedidoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PedidoIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ConductorRepository conductorRepository;

    private String token;
    private Conductor conductor;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        conductor = new Conductor();
        conductor.setNombre("Juan Pérez");
        conductor.setActivo(true);
        conductor = conductorRepository.save(conductor);

        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo.setConductor(conductor);
        vehiculo = vehiculoRepository.save(vehiculo);

        // Obtener token de autenticación
        token = obtenerToken();
    }

    @Test
    @DisplayName("Debe completar el flujo de creación y actualización de pedido exitosamente")
    void flujoCompletoPedido_DebeSerExitoso() {
        // 1. Crear pedido
        CreatePedidoDTO createPedidoDTO = new CreatePedidoDTO();
        createPedidoDTO.setDescripcion("Pedido de prueba integración");
        createPedidoDTO.setPeso(new BigDecimal("500.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CreatePedidoDTO> request = new HttpEntity<>(createPedidoDTO, headers);

        ResponseEntity<PedidoDTO> response = restTemplate.postForEntity(
                "/api/v1/pedidos?vehiculoId=" + vehiculo.getId(),
                request,
                PedidoDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo(EstadoPedido.PENDIENTE);

        // 2. Actualizar estado del pedido
        Long pedidoId = response.getBody().getId();
        ResponseEntity<PedidoDTO> updateResponse = restTemplate.exchange(
                "/api/v1/pedidos/" + pedidoId + "/estado?nuevoEstado=EN_PROCESO",
                org.springframework.http.HttpMethod.PUT,
                new HttpEntity<>(headers),
                PedidoDTO.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().getEstado()).isEqualTo(EstadoPedido.EN_PROCESO);

        // Verificar en base de datos
        Pedido pedidoGuardado = pedidoRepository.findById(pedidoId).orElseThrow();
        assertThat(pedidoGuardado.getEstado()).isEqualTo(EstadoPedido.EN_PROCESO);
        assertThat(pedidoGuardado.getVehiculo().getId()).isEqualTo(vehiculo.getId());
        assertThat(pedidoGuardado.getConductor().getId()).isEqualTo(conductor.getId());
    }

    @Test
    @DisplayName("Debe validar capacidad del vehículo al crear pedido")
    void crearPedido_DebeValidarCapacidad() {
        // Crear pedido que excede capacidad
        CreatePedidoDTO createPedidoDTO = new CreatePedidoDTO();
        createPedidoDTO.setDescripcion("Pedido excede capacidad");
        createPedidoDTO.setPeso(new BigDecimal("1500.00")); // Excede capacidad de 1000

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CreatePedidoDTO> request = new HttpEntity<>(createPedidoDTO, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/pedidos?vehiculoId=" + vehiculo.getId(),
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("capacidad");
    }

    private String obtenerToken() {
        // Implementar lógica para obtener token de autenticación
        // Este es un ejemplo simplificado
        return "token_de_prueba";
    }
} 