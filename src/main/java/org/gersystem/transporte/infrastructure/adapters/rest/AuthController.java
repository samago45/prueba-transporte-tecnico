package org.gersystem.transporte.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
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
import org.gersystem.transporte.infrastructure.adapters.rest.exception.ValidationException;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "API para la gestión de autenticación y usuarios. Permite registro de nuevos usuarios, inicio de sesión con JWT, renovación de tokens, cierre de sesión y gestión de usuarios existentes.")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar usuarios",
        description = "Obtiene la lista de todos los usuarios registrados en el sistema. Solo accesible para administradores. La información sensible como contraseñas no se incluye en la respuesta."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No tiene permisos para listar usuarios",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
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
        description = "Autentica un usuario usando username/email y contraseña. Retorna tokens JWT para acceso y renovación."
    )
    @ApiResponses({
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
    })
    public ResponseEntity<?> login(
            @Parameter(
                description = "Credenciales de usuario. Se puede usar tanto el nombre de usuario como el email para iniciar sesión.",
                required = true,
                schema = @Schema(implementation = LoginRequestDTO.class)
            )
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            log.debug("Intentando autenticar usuario: {}", loginRequest.getUsernameOrEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
            );

            Usuario usuario = (Usuario) authentication.getPrincipal();
            log.debug("Usuario autenticado exitosamente: {}", usuario.getUsername());
            String accessToken = jwtTokenProvider.generateToken(usuario);
            String refreshToken = usuarioDomainService.generarRefreshToken(usuario);

            return ResponseEntity.ok(new JwtAuthenticationResponseDTO(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para usuario: {}", loginRequest.getUsernameOrEmail());
            throw new ValidationException("Credenciales inválidas");
        } catch (UsernameNotFoundException e) {
            log.warn("Usuario no encontrado: {}", loginRequest.getUsernameOrEmail());
            throw new ValidationException("Usuario no encontrado");
        } catch (Exception e) {
            log.error("Error durante la autenticación para usuario {}: {}", loginRequest.getUsernameOrEmail(), e.getMessage(), e);
            throw new ValidationException("Error durante la autenticación: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(
        summary = "Registrar usuario",
        description = "Crea una nueva cuenta de usuario en el sistema. Validaciones: El email debe ser único y tener un formato válido, el nombre de usuario debe ser único, la contraseña debe cumplir los requisitos mínimos de seguridad, los roles asignados deben ser válidos."
    )
    @ApiResponses({
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
    })
    public ResponseEntity<UsuarioDTO> register(
            @Parameter(
                description = "Datos del nuevo usuario",
                required = true,
                schema = @Schema(implementation = CreateUsuarioDTO.class)
            )
            @Valid @RequestBody CreateUsuarioDTO createUsuarioDTO) {
        try {
            Usuario usuario = usuarioMapper.toEntity(createUsuarioDTO);
            Usuario usuarioCreado = usuarioDomainService.crearUsuario(usuario);
            return ResponseEntity.ok(usuarioMapper.toDto(usuarioCreado));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Error en los datos del usuario: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new ValidationException("No se puede crear el usuario: " + e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    @Operation(
        summary = "Refrescar token",
        description = "Genera un nuevo token de acceso usando el token de renovación. Este endpoint debe usarse cuando el token de acceso ha expirado. El token de renovación debe ser válido y no estar revocado. Se genera un nuevo par de tokens (acceso y renovación)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tokens renovados exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JwtAuthenticationResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token de renovación inválido o expirado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<JwtAuthenticationResponseDTO> refreshToken(
            @Parameter(
                description = "Token de renovación actual",
                required = true,
                schema = @Schema(implementation = RefreshTokenRequestDTO.class)
            )
            @Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            Usuario usuario = usuarioDomainService.validarRefreshToken(request.getRefreshToken());
            String accessToken = jwtTokenProvider.generateToken(usuario);
            String newRefreshToken = usuarioDomainService.generarRefreshToken(usuario);

            return ResponseEntity.ok(new JwtAuthenticationResponseDTO(accessToken, newRefreshToken));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Token de renovación inválido");
        }
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Cerrar sesión",
        description = "Revoca el token de renovación del usuario. Esto invalida la sesión actual y fuerza al usuario a iniciar sesión nuevamente. El token de acceso actual seguirá siendo válido hasta su expiración."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Sesión cerrada exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Token de renovación inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<Void> logout(
            @Parameter(
                description = "Token de renovación a revocar",
                required = true,
                schema = @Schema(implementation = RefreshTokenRequestDTO.class)
            )
            @Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            usuarioDomainService.revocarRefreshToken(request.getRefreshToken());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Token de renovación inválido");
        }
    }
} 
