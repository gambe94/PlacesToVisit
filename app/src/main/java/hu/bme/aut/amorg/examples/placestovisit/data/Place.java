package hu.bme.aut.amorg.examples.placestovisit.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

import hu.bme.aut.amorg.examples.placestovisit.R;

@Entity
public class Place implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long id = null;
    @ColumnInfo(name = "place_type")
    private PlaceType placeType;
    @ColumnInfo(name = "place_name")
    private String placeName;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "pick_up_date")
    private Date pickUpDate;

    public Place() {
    }

    @Ignore
    public Place(PlaceType placeType, String placeName, String description, Date pickUpDate) {
        this.placeType = placeType;
        this.placeName = placeName;
        this.description = description;
        this.pickUpDate = pickUpDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(Date pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public enum PlaceType {
        LANDSCAPE(0, R.drawable.landscape),
        CITY(1, R.drawable.city),
        BUILDING(2, R.drawable.building);

        private int value;
        private int iconId;

        PlaceType(int value, int iconId) {
            this.value = value;
            this.iconId = iconId;
        }

        public static PlaceType fromInt(int value) {
            for (PlaceType p : PlaceType.values()) {
                if (p.value == value) {
                    return p;
                }
            }
            return LANDSCAPE;
        }

        public int getIconId() {
            return iconId;
        }

        public int getValue() {
            return value;
        }
    }
}
