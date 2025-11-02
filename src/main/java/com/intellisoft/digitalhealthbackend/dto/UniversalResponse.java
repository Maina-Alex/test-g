package com.intellisoft.digitalhealthbackend.dto;

import lombok.Builder;

@Builder
public record UniversalResponse(int status, String message, Object data) {
}
