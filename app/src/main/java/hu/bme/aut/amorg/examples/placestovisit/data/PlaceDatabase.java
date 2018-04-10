package hu.bme.aut.amorg.examples.placestovisit.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import hu.bme.aut.amorg.examples.placestovisit.data.converters.DateTypeConverter;
import hu.bme.aut.amorg.examples.placestovisit.data.converters.PlaceTypeConverter;

@Database(entities = {Place.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class, PlaceTypeConverter.class})
public abstract class PlaceDatabase extends RoomDatabase {
    public abstract PlaceDao placeDao();
}
