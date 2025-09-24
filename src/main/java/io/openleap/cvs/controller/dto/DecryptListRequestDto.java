package io.openleap.cvs.controller.dto;

import java.util.Map;

public record DecryptListRequestDto(Map<String, String> data, String iv) {
}
