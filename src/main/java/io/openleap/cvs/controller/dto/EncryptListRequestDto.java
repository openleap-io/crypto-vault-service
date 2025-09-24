package io.openleap.cvs.controller.dto;

import java.util.Map;

public record EncryptListRequestDto(Map<String, String> data, String iv) {
}
