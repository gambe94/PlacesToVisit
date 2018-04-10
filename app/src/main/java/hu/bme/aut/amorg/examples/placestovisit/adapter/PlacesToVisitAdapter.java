package hu.bme.aut.amorg.examples.placestovisit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import hu.bme.aut.amorg.examples.placestovisit.CreatePlaceToVisitActivity;
import hu.bme.aut.amorg.examples.placestovisit.R;
import hu.bme.aut.amorg.examples.placestovisit.data.Place;
import hu.bme.aut.amorg.examples.placestovisit.data.PlaceDatabase;
import hu.bme.aut.amorg.examples.placestovisit.helper.ItemTouchHelperAdapter;

import static hu.bme.aut.amorg.examples.placestovisit.PlacesListActivity.REQUEST_EDIT_PLACE_CODE;

public class PlacesToVisitAdapter extends
        RecyclerView.Adapter<PlacesToVisitAdapter.PlacesViewHolder> implements ItemTouchHelperAdapter {

    public static final int CONTEXT_ACTION_DELETE = 10;
    public static final int CONTEXT_ACTION_EDIT = 11;

    private PlaceDatabase database;
    private List<Place> placesToVisit;
    private Context context;

    public PlacesToVisitAdapter(Context context, PlaceDatabase database,List<Place> placesToVisit) {
        this.context = context;
        this.database = database;
        this.placesToVisit = placesToVisit;
    }

    public void addPlace(Place place) {
        placesToVisit.add(place);
    }

    public void updatePlace(int index, Place place) {
        placesToVisit.set(index, place);
    }

    public void removePlace(int index) {
        placesToVisit.remove(index);
    }

    public Place getItem(int i) {
        return placesToVisit.get(i);
    }

    @Override
    public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_place, parent, false);
        return new PlacesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlacesViewHolder holder, int position) {

        final Place place = placesToVisit.get(position);
        if (place != null) {
            holder.tvPlace.setText(place.getPlaceName());
            holder.tvDescription.setText(place.getDescription());
            holder.tvDate.setText(place.getPickUpDate().toString());
            holder.ivIcon.setImageResource(place.getPlaceType().getIconId());

            holder.btnEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setEmailEvent(place);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return placesToVisit.size();
    }


    private void setEmailEvent(Place place) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"peter.ekler@aut.bme.hu"});
        i.putExtra(Intent.EXTRA_SUBJECT,
                "Place info");
        i.putExtra(Intent.EXTRA_TEXT,
                place.getPlaceName() + "\n" + place.getDescription());
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context,
                    "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(final int position) {
        Log.d("DRAG hivodik", String.valueOf(position));
        final Place place =  placesToVisit.get(position);
        final Handler handler=new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.placeDao().delete(place);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        removePlace(position);
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }


    class PlacesViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {
        ImageView ivIcon;
        TextView tvPlace;
        TextView tvDescription;
        TextView tvDate;
        ImageButton btnEmail;

        public PlacesViewHolder(View itemView) {
            super(itemView);

            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvPlace = itemView.findViewById(R.id.tvPlace);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEmail = itemView.findViewById(R.id.btnEmail);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Menu");
            MenuItem myItem1 =  menu.add(0, CONTEXT_ACTION_DELETE, 0, "Delete");
            myItem1.setOnMenuItemClickListener(this);
            MenuItem myItem2 = menu.add(0, CONTEXT_ACTION_EDIT, 0, "Edit");
            myItem2.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == CONTEXT_ACTION_DELETE) {
                final Place place =  getItem(getAdapterPosition());
                final Handler handler=new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        database.placeDao().delete(place);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                removePlace(getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        });

                    }
                }).start();

            }
            if (item.getItemId() == PlacesToVisitAdapter.CONTEXT_ACTION_EDIT) {
                Place selectedPlace = getItem(getAdapterPosition());
                Intent i = new Intent();
                i.setClass(context, CreatePlaceToVisitActivity.class);
                i.putExtra(CreatePlaceToVisitActivity.KEY_EDIT_PLACE, selectedPlace);
                i.putExtra(CreatePlaceToVisitActivity.KEY_EDIT_ID, getAdapterPosition());
                ((Activity)context).startActivityForResult(i, REQUEST_EDIT_PLACE_CODE);

            } else {
                return false;
            }
            return true;

        }
    }
}
