package org.gersystem.transporte.integration;

import org.gersystem.transporte.TransporteApplication;
import org.gersystem.transporte.domain.model.Conductor;
import org.gersystem.transporte.domain.model.EstadoPedido;
import org.gersystem.transporte.domain.model.Pedido;
import org.gersystem.transporte.domain.model.Vehiculo;
import org.gersystem.transporte.domain.repository.ConductorRepository;
import org.gersystem.transporte.domain.repository.PedidoRepository;
import org.gersystem.transporte.domain.repository.VehiculoRepository;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreatePedidoDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ErrorResponseDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.JwtAuthenticationResponseDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.LoginRequestDTO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;

import static org.assertj.core.api.Assertions.assertThat;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.domain.model.Rol;
import org.gersystem.transporte.domain.repository.UsuarioRepository;
import java.util.List;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = TransporteApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PedidoIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    private int port;

    private Conductor conductor;
    private Vehiculo vehiculo;
    private String token;

    @BeforeEach
    void setUp() {
        // Crear conductor
        conductor = new Conductor();
        conductor.setNombre("Juan Pérez");
        conductor.setLicencia("A12345");
        conductor.setActivo(true);
        conductor = conductorRepository.save(conductor);

        // Crear vehículo
        vehiculo = new Vehiculo();
        vehiculo.setPlaca("ABC123");
        vehiculo.setCapacidad(new BigDecimal("1000.00"));
        vehiculo.setActivo(true);
        vehiculo.setConductor(conductor);
        vehiculo = vehiculoRepository.save(vehiculo);

        // Crear usuario de prueba si no existe
        String testEmail = "admin@test.com";
        if (!usuarioRepository.existsByEmail(testEmail)) {
            Usuario usuario = new Usuario();
            usuario.setUsername("admin");
            usuario.setPassword(passwordEncoder.encode("admin"));
            usuario.setEmail(testEmail);
            usuario.setNombre("Admin");
            usuario.setActivo(true);
            usuario.setRoles(List.of(Rol.ADMIN));
            usuarioRepository.save(usuario);
        }

        // Obtener token de autenticación
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsernameOrEmail("admin");
        loginRequest.setPassword("admin");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDTO> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<JwtAuthenticationResponseDTO> response = restTemplate.exchange(
            "/api/v1/auth/login",
            HttpMethod.POST,
            request,
            JwtAuthenticationResponseDTO.class
        );

        token = response.getBody().getAccessToken();
    }

    @Test
    @DisplayName("Debe completar el flujo de creación y actualización de pedido exitosamente")
    void flujoCompletoPedido_DebeSerExitoso() {
        // 1. Crear pedido
        CreatePedidoDTO createPedidoDTO = new CreatePedidoDTO();
        createPedidoDTO.setDescripcion("Pedido de prueba integración");
        createPedidoDTO.setPeso(new BigDecimal("500.00"));
        createPedidoDTO.setVehiculoId(vehiculo.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreatePedidoDTO> request = new HttpEntity<>(createPedidoDTO, headers);

        ResponseEntity<PedidoDTO> response = restTemplate.postForEntity(
                "/api/v1/pedidos",
                request,
                PedidoDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo(EstadoPedido.PENDIENTE);

        // 2. Actualizar estado del pedido
        Long pedidoId = response.getBody().getId();
        ResponseEntity<PedidoDTO> updateResponse = restTemplate.exchange(
                "/api/v1/pedidos/" + pedidoId + "/estado?nuevoEstado=EN_PROCESO",
                org.springframework.http.HttpMethod.PATCH,
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
    @DisplayName("Debe validar capacidad al crear pedido")
    void crearPedido_DebeValidarCapacidad() {
        // Arrange
        CreatePedidoDTO pedidoDTO = new CreatePedidoDTO();
        pedidoDTO.setDescripcion("Pedido de prueba");
        pedidoDTO.setPeso(new BigDecimal("2000.00")); // Excede la capacidad
        pedidoDTO.setVehiculoId(vehiculo.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreatePedidoDTO> request = new HttpEntity<>(pedidoDTO, headers);

        // Act & Assert
        ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(
            "/api/v1/pedidos",
            request,
            ErrorResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("El vehículo no tiene capacidad suficiente");
    }
} 