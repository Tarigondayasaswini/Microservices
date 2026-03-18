package com.revplay.catalogservice.entity;
import com.revplay.catalogservice.enums.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class User {
    private Long userId;
    private String email;
    private UserRole role;
    private Boolean isActive;
}
