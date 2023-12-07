package com.collabed.core.data.dto;

import java.util.List;

public record UserGroupResponseDto(String id, String name, List<UserResponseDto> users) {}
