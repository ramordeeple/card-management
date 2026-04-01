package com.pm.bankcards.service.api;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Composite annotation for standard security-related API responses.
 * <p>
 * Includes:
 * <ul>
 * <li><b>401 Unauthorized:</b> When the bearer token is missing or invalid.</li>
 * <li><b>403 Forbidden:</b> When the user lacks necessary permissions.</li>
 * </ul>
 * This annotation can be applied to both individual methods and entire controllers.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "No permission"),
})
public @interface ApiStandardResponses {
}
