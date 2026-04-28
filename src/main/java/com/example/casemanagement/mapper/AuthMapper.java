package com.example.casemanagement.mapper;

import com.example.casemanagement.dto.RegisterRequest;
import com.example.casemanagement.model.Role;
import com.example.casemanagement.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper för autentiseringsrelaterad transformation.
 *
 * Ansvarar för att omvandla inkommande DTO (RegisterRequest)
 * till en User-entitet som kan lagras i databasen.
 *
 * Design:
 * - Separat mapper minskar koppling mellan lager
 * - Gör transformationer återanvändbara och testbara
 *
 * Säkerhet:
 * - Lösenord skickas in som redan krypterat (encodedPassword)
 * - Mapper ansvarar inte för kryptering, endast för mappning
 *
 * Notering:
 * - Nya användare tilldelas rollen USER som standard
 */
@Component
public class AuthMapper {

    /**
     * Omvandlar RegisterRequest till User-entitet.
     *
     * @param request inkommande registreringsdata
     * @param encodedPassword krypterat lösenord från service-lagret
     * @return User-entitet redo att sparas i databasen
     */
    public User toUser(RegisterRequest request, String encodedPassword) {
        return new User(
                request.getName(),
                request.getEmail(),
                encodedPassword,
                Role.USER
        );
    }
}