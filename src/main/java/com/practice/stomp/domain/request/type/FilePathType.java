package com.practice.stomp.domain.request.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilePathType {
    PROFILE("user/profiles/%s/"),
    ;

    private final String pathFormat;

    public String generateFilePath(String... variables) {
        if (variables == null || variables.length == 0) {
            return this.pathFormat;
        }

        return String.format(pathFormat, variables);
    }
}
