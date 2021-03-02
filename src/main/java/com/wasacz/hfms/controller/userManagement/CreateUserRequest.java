package com.wasacz.hfms.controller.userManagement;

import com.wasacz.hfms.controller.utils.valueOfEnumValidator.ValueOfEnum;
import com.wasacz.hfms.persistence.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank // only for controller layer validation
    private String username;
    @NotBlank
    private String password;
    @ValueOfEnum(enumClass = Role.class)
    private String role;
}
