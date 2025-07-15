package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.domain.service.UsuarioDomainService;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.*;
import org.gersystem.transporte.infrastructure.adapters.rest.mapper.UsuarioMapper;
import org.gersystem.transporte.infrastructure.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "API para autenticación y gestión de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioDomainService usuarioDomainService;
    private final UsuarioMapper usuarioMapper;

    public AuthController(AuthenticationManager authenticationManager,
                         JwtTokenProvider jwtTokenProvider,
                         UsuarioDomainService usuarioDomainService,
                         UsuarioMapper usuarioMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.usuarioDomainService = usuarioDomainService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping("/usuarios")
    @Operation(
        summary = "Listar usuarios",
        description = "Obtiene la lista de todos los usuarios registrados",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de usuarios obtenida exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDTO.class)
                )
            )
        }
    )
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<Usuario> usuarios = usuarioDomainService.listarUsuarios();
        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDTO);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario usando username/email y contraseña, devuelve tokens JWT",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Autenticación exitosa",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JwtAuthenticationResponseDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Credenciales inválidas",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            )
        }
    )
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
            );

            Usuario usuario = (Usuario) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.generateToken(usuario);
            String refreshToken = usuarioDomainService.generarRefreshToken(usuario);

            return ResponseEntity.ok(new JwtAuthenticationResponseDTO(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Credenciales inválidas", "Las credenciales proporcionadas son incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Error de autenticación", "Ha ocurrido un error durante la autenticación");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/register")
    @Operation(
        summary = "Registrar usuario",
        description = "Crea una nueva cuenta de usuario",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuario registrado exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Datos de entrada inválidos",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "409",
                description = "El email o nombre de usuario ya está registrado",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
                )
            )
        }
    )
    public ResponseEntity<UsuarioDTO> register(@Valid @RequestBody CreateUsuarioDTO createUsuarioDTO) {
        Usuario usuario = usuarioMapper.toEntity(createUsuarioDTO);
        Usuario usuarioCreado = usuarioDomainService.crearUsuario(usuario);
        return ResponseEntity.ok(usuarioMapper.toDto(usuarioCreado));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refrescar token", description = "Genera un nuevo access token usando el refresh token")
    public ResponseEntity<JwtAuthenticationResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        Usuario usuario = usuarioDomainService.validarRefreshToken(request.getRefreshToken());
        String accessToken = jwtTokenProvider.generateToken(usuario);
        String newRefreshToken = usuarioDomainService.generarRefreshToken(usuario);

        return ResponseEntity.ok(new JwtAuthenticationResponseDTO(accessToken, newRefreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Revoca el refresh token del usuario")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        usuarioDomainService.revocarRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }
} 