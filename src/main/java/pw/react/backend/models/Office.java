package pw.react.backend.models;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "office")
public class Office implements Serializable {
    @Serial
    private static final long serialVersionUID = 1707345223145276600L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private float pricePerDay;
    @Column
    private boolean isActive;
    @Column
    private double lat;
    @Column
    private double lng;
    @Column
    private String address;
    @Column
    private long amenities;
    @Column
    @Enumerated(EnumType.STRING)
    private OfficeType officeType;
    @Column
    private String officePhotoThumbnailId;
    @Column
    private int rating;
    @Column
    private int officeArea;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getPricePerDay() {
        return pricePerDay;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPricePerDay(float pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getAmenities() {
        return amenities;
    }

    public OfficeType getOfficeType() {
        return officeType;
    }

    public void setAmenities(long amenities) {
        this.amenities = amenities;
    }

    public void setOfficeType(OfficeType officeType) {
        this.officeType = officeType;
    }

    public String getOfficePhotoThumbnailId() {
        return officePhotoThumbnailId;
    }

    public void setOfficePhotoThumbnailId(String officePhotoThumbnailId) {
        this.officePhotoThumbnailId = officePhotoThumbnailId;
    }

    public int getRating() {
        return rating;
    }

    public int getOfficeArea() {
        return officeArea;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setOfficeArea(int officeArea) {
        this.officeArea = officeArea;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public enum Amenities {
        WIFI(1L),
        COFFEE(2L),
        TEA(4L),
        PROJECTOR(8L),
        WHITEBOARD(16L),
        PRINTER(32L),
        SCANNER(64L),
        FAX(128L),
        PHONE(256L),
        KITCHEN(512L),
        PARKING(1024L),
        ACCESSIBLE(2048L),
        SECURITY(4096L),
        LOCKERS(8192L),
        PETS_ALLOWED(16384L),
        SMOKING_AREA(32768L),;

        private final long value;
        public long getValue(){
            return value;
        }
        Amenities(long value){
            this.value = value;
        }
        public static String[] getAmenities(long amenities){
            List<String> amenitiesCollection = new ArrayList<String>();
            for(Amenities amenity : Amenities.values()){
                if((amenity.getValue() & amenities) != 0){
                    amenitiesCollection.add(amenity.name());
                }
            }
            return amenitiesCollection.toArray(String[]::new);
        }
        public static long getAmenitiesMask(String[] amenities){
            long amenitiesMask = 0;
            for(String amenity : amenities){
                amenitiesMask |= Amenities.valueOf(amenity).getValue();
            }
            return amenitiesMask;
        }
    };

    public enum OfficeType {
        CONFERENCE_ROOM,
        COWORKING_SPACE,
        DESK,
        OFFICE;

        public long getValue(){
            return 1L << this.ordinal();
        }

    }
}

