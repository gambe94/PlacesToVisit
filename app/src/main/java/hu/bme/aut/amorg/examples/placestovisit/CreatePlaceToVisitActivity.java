package hu.bme.aut.amorg.examples.placestovisit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;

import hu.bme.aut.amorg.examples.placestovisit.data.Place;

public class CreatePlaceToVisitActivity extends Activity {

    public static final String KEY_EDIT_PLACE = "KEY_EDIT_PLACE";
    public static final String KEY_EDIT_ID = "KEY_EDIT_ID";
    public static final String KEY_PLACE = "KEY_PLACE";

    private boolean inEditMode = false;
    private Spinner spinnerPlaceType;
    private EditText etPlace;
    private EditText etPlaceDesc;
    private int placeToEditId = 0;
    private Place placeToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_place_to_visit);

        spinnerPlaceType = findViewById(R.id.spinnerPlaceType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.placetypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaceType.setAdapter(adapter);

        etPlace =  findViewById(R.id.etPlaceName);
        etPlaceDesc = findViewById(R.id.etPlaceDesc);

        if (getIntent().getExtras() != null &&
                getIntent().getExtras().containsKey(KEY_EDIT_PLACE)) {
            inEditMode = true;

            placeToEdit = (Place) getIntent().getSerializableExtra(KEY_EDIT_PLACE);
            placeToEditId = getIntent().getIntExtra(KEY_EDIT_ID, -1);

            spinnerPlaceType.setSelection(placeToEdit.getPlaceType().getValue());
            etPlace.setText(placeToEdit.getPlaceName());
            etPlaceDesc.setText(placeToEdit.getDescription());
        }

        Button btnSave =  findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inEditMode) {
                    updatePlace();
                } else {
                    savePlace();
                }
            }
        });
    }

    private void updatePlace() {
        placeToEdit.setPlaceName(etPlace.getText().toString());
        placeToEdit.setDescription(etPlaceDesc.getText().toString());
        placeToEdit.setPlaceType(Place.PlaceType.fromInt(spinnerPlaceType.getSelectedItemPosition()));
        placeToEdit.setPickUpDate(new Date(System.currentTimeMillis()));

        Intent intentResult = new Intent();
        intentResult.putExtra(KEY_PLACE,placeToEdit);
        intentResult.putExtra(KEY_EDIT_ID,placeToEditId);
        setResult(RESULT_OK,intentResult);
        finish();
    }

    private void savePlace() {
        Intent intentResult = new Intent();
        intentResult.putExtra(KEY_PLACE,
                new Place(Place.PlaceType.fromInt(spinnerPlaceType.getSelectedItemPosition())
                        ,etPlace.getText().toString(),etPlaceDesc.getText().toString(),
                        new Date(System.currentTimeMillis())));
        setResult(RESULT_OK,intentResult);
        finish();
    }
}
