package hu.bme.aut.amorg.examples.placestovisit.data;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PlaceDao {

    @Insert
    void insertAll(Place... places);

    @Query("SELECT * FROM Place")
    List<Place> getAll();

    @Update
    int updatePlace(Place place);


    @Delete
    void delete(Place plave);
}
