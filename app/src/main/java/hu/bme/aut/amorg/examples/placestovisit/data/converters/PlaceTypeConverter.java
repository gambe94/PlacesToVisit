package hu.bme.aut.amorg.examples.placestovisit.data.converters;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

import hu.bme.aut.amorg.examples.placestovisit.data.Place;

public class PlaceTypeConverter {

    @TypeConverter
    public static Place.PlaceType toPlaceType(Integer value) {
        return Place.PlaceType.fromInt(value);
    }

    @TypeConverter
    public static Integer toInteger(Place.PlaceType value) {
        return value == null ? null : value.getValue();
    }
}