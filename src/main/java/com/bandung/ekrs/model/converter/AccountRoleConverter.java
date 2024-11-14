package com.bandung.ekrs.model.converter;

import com.bandung.ekrs.model.enums.AccountRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountRoleConverter implements AttributeConverter<AccountRole, String> {

    @Override
    public String convertToDatabaseColumn(AccountRole role) {
        if (role == null) {
            return null;
        }
        return role.name().toLowerCase();
    }

    @Override
    public AccountRole convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return AccountRole.valueOf(dbData.toUpperCase());
    }
} 