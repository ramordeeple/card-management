package com.pm.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;


@Converter(autoApply = false)
public class СryptoStringConverter implements AttributeConverter<String, String> {
    private static CryptoService staticCryptoService;

    /// Lazy access to bean
    private CryptoService crypto() {
        return SpringBeans.getBean(CryptoService.class);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;

        return staticCryptoService.encrypt(attribute);
    }

    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;

        return staticCryptoService.decrypt(dbData);
    }
}
