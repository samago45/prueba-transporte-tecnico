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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens JWT")
    public ResponseEntity<JwtAuthenticationResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateToken(usuario);
        String refreshToken = usuarioDomainService.generarRefreshToken(usuario);

        return ResponseEntity.ok(new JwtAuthenticationResponseDTO(accessToken, refreshToken));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario")
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