package pw.react.backend.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pw.react.backend.models.Office;
import pw.react.backend.utils.JsonDateDeserializer;
import pw.react.backend.utils.JsonDateSerializer;

import java.time.LocalDateTime;

public record OfficeDto(
        long id,
        String name,
        String description,
        float pricePerDay,
        boolean isActive,
        String address,
        @JsonProperty(required = false)
        @JsonDeserialize(using = JsonDateDeserializer.class) @JsonSerialize(using = JsonDateSerializer.class)
        LocalDateTime availableFrom,
        @JsonProperty(required = false)
        @JsonDeserialize(using = JsonDateDeserializer.class) @JsonSerialize(using = JsonDateSerializer.class)
        LocalDateTime availableTo,
        String[] amenities,
        Office.OfficeType officeType,
        int rating,
        int officeArea,
        String mainPhoto,
        String[] photos
) {
    public static OfficeDto valueFrom(Office office, String mainPhoto, String[] photos) {
        return new OfficeDto(
                office.getId(),
                office.getName(),
                office.getDescription(),
                office.getPricePerDay(),
                office.isActive(),
                office.getAddress(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                Office.Amenities.getAmenities(office.getAmenities()),
                office.getOfficeType(),
                office.getRating(),
                office.getOfficeArea(),
                mainPhoto,
                photos
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
}
