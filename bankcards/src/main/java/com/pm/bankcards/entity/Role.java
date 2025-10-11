package com.pm.bankcards.entity;

import com.pm.bankcards.security.RoleName;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 32)
    private RoleName name;

    protected Role() {}

    public static Role of(RoleName name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }

    public Long getId() {
        return id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" + name + '}';
    }
}
