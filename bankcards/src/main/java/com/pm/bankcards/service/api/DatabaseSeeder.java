package com.pm.bankcards.service.api;

import com.pm.bankcards.entity.User;

public interface DatabaseSeeder {
    void seedCards(User owner, int count);
}
