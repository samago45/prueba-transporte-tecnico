package org.gersystem.transporte.infrastructure.adapters.rest.mapper;

import org.gersystem.transporte.domain.model.Usuario;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.CreateUsuarioDTO;
import org.gersystem.transporte.infrastructure.adapters.rest.dto.UsuarioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "refreshTokenExpiryDate", ignore = true)
    @Mapping(target = "activo", constant = "true")
    Usuario toEntity(CreateUsuarioDTO createUsuarioDTO);
    
    UsuarioDTO toDto(Usuario usuario);
} 
