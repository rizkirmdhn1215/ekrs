package com.bandung.ekrs.model.converter;

import com.bandung.ekrs.model.enums.StudentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StudentStatusConverter implements AttributeConverter<StudentStatus, String> {

    @Override
    public String convertToDatabaseColumn(StudentStatus status) {
        if (status == null) {
            return null;
        }
        return status.name().toLowerCase();
    }

    @Override
    public StudentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return StudentStatus.valueOf(dbData.toUpperCase());
    }
} 