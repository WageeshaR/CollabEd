package com.collabed.core.data.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public record UserGroupResponseDTO(String id, String name, List<UserResponseDto> users) {}
