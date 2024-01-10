package pw.react.backend.web;

import pw.react.backend.models.Office;
import pw.react.backend.models.User;

public record OfficeDto(
        long id,
        String name,
        String description,
        float pricePerDay,
        boolean isActive,
        String street,
        String city,
        String state,
        String country,
        String postalCode
) {
    public static OfficeDto valueFrom(Office office) {
        return new OfficeDto(
                office.getId(),
                office.getName(),
                office.getDescription(),
                office.getPricePerDay(),
                office.isActive(),
                office.getStreet(),
                office.getCity(),
                office.getState(),
                office.getCountry(),
                office.getPostalCode()
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
        office.setStreet(officeDto.street());
        office.setCity(officeDto.city());
        office.setState(officeDto.state());
        office.setCountry(officeDto.country());
        office.setPostalCode(officeDto.postalCode());
        return office;
    }
}
