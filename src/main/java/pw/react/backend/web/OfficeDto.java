package pw.react.backend.web;

import jakarta.persistence.*;
import pw.react.backend.models.Office;
import pw.react.backend.models.User;

import java.io.Serial;

public record OfficeDto(
        long id,
        String name,
        String description,
        float pricePerDay,
        boolean isActive,
        String address,
        String[] amenities,
        Office.OfficeType officeType,
        int rating,
        int officeArea
) {
    public static OfficeDto valueFrom(Office office) {
        return new OfficeDto(
                office.getId(),
                office.getName(),
                office.getDescription(),
                office.getPricePerDay(),
                office.isActive(),
                office.getAddress(),
                Office.Amenities.getAmenities(office.getAmenities()),
                office.getOfficeType(),
                office.getRating(),
                office.getOfficeArea()
        );
    }
    public static Office convertToOffice(OfficeDto officeDto)
    {
        Office office = new pw.react.backend.models.Office();
        office.setId(officeDto.id());
        office.setName(officeDto.name());
        office.setDescription(officeDto.description());
        office.setPricePerDay(officeDto.pricePerDay());
        office.setActive(officeDto.isActive());
        office.setAddress(officeDto.address());
        office.setAmenities(Office.Amenities.getAmenitiesMask(officeDto.amenities()));
        office.setOfficeType(officeDto.officeType());
        office.setRating(officeDto.rating());
        office.setOfficeArea(officeDto.officeArea());
        return office;
    }

    // TODO: Add lat, lng
}
