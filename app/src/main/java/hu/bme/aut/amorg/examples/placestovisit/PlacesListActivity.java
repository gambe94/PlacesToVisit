package hu.bme.aut.amorg.examples.placestovisit;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import hu.bme.aut.amorg.examples.placestovisit.adapter.PlacesToVisitAdapter;
import hu.bme.aut.amorg.examples.placestovisit.data.Place;
import hu.bme.aut.amorg.examples.placestovisit.data.PlaceDatabase;
import hu.bme.aut.amorg.examples.placestovisit.helper.OnStartDragListener;
import hu.bme.aut.amorg.examples.placestovisit.helper.SimpleItemTouchHelperCallback;
import hu.bme.aut.amorg.examples.placestovisit.view.EmptyRecyclerView;

import static android.support.v7.widget.helper.ItemTouchHelper.Callback.makeMovementFlags;

public class PlacesListActivity extends AppCompatActivity implements OnStartDragListener {

    public static final int REQUEST_NEW_PLACE_CODE = 100;
    public static final int REQUEST_EDIT_PLACE_CODE = 101;


    private PlacesToVisitAdapter adapter;
    private PlaceDatabase db;


    private EmptyRecyclerView emptyRecyclerView;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;

    private ItemTouchHelper mItemTouchHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = Room.databaseBuilder(getApplicationContext(), PlaceDatabase.class, "place-db").build();




        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Place> placesToVisit = db.placeDao().getAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        coordinatorLayout = findViewById(R.id.main_coordinator_layout);

                        emptyRecyclerView = findViewById(R.id.placesListERV);
                        emptyRecyclerView.setLayoutManager(new LinearLayoutManager((getApplicationContext())));
                        adapter = new PlacesToVisitAdapter(PlacesListActivity.this, db, placesToVisit);
                        emptyRecyclerView.setAdapter(adapter);
                        registerForContextMenu(emptyRecyclerView);

                        View emptyTV= findViewById(R.id.emptyTV);
                        emptyRecyclerView.setEmptyView(emptyTV);


                        //SET TOUCH HELPER

                        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
                        mItemTouchHelper = new ItemTouchHelper(callback);
                        mItemTouchHelper.attachToRecyclerView(emptyRecyclerView);

                        emptyTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showNewPlaceDialog();
                            }
                        });
                    }
                });


            }
        }).start();

        fab = findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPlaceDialog();
            }
        });





    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == REQUEST_NEW_PLACE_CODE) {
                    final Place place = (Place) data.getSerializableExtra(
                            CreatePlaceToVisitActivity.KEY_PLACE);
                    // when I call the save() function
                    // it will generate the id for the place

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.placeDao().insertAll(place);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.addPlace(place);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();


                    Toast.makeText(this, R.string.added, Toast.LENGTH_LONG).show();
                } else if (requestCode == REQUEST_EDIT_PLACE_CODE) {
                    final int index = data.getIntExtra(CreatePlaceToVisitActivity.KEY_EDIT_ID, -1);
                    if (index != -1) {
                       final Place place = (Place) data.getSerializableExtra(
                                CreatePlaceToVisitActivity.KEY_PLACE);
                        place.setId(adapter.getItem(index).getId());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db.placeDao().updatePlace(place);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.updatePlace(index,place);
                                        adapter.notifyDataSetChanged();
                                        //Toast.makeText(getApplicationContext(), R.string.updated, Toast.LENGTH_LONG).show();
                                        showText(getResources().getString(R.string.updated));
                                    }
                                });
                            }
                        }).start();



                    }
                }
                break;
            case RESULT_CANCELED:
                showText(getResources().getString(R.string.cancelled));

                break;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_places_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_new_place) {
//            showNewPlaceDialog();
//
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void showNewPlaceDialog() {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        PlacesListActivity.this,
                        fab,
                        "create");
        Intent i = new Intent();
        i.setClass(this, CreatePlaceToVisitActivity.class);
        startActivityForResult(i, REQUEST_NEW_PLACE_CODE, options.toBundle());
    }


    private void showText(String text) {
        Snackbar.make(coordinatorLayout,text,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
