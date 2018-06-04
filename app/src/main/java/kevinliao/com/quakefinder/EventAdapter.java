package kevinliao.com.quakefinder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kevinliao.com.quakefinder.network.Earthquake;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Earthquake> mDataSet = new ArrayList<>();
    private OnBottomReachedListener onBottomReachedListener;

    public void setDataset(List<Earthquake> dataset) {
        mDataSet = dataset;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Earthquake event = mDataSet.get(position);
        holder.bind(event);
        if (position == mDataSet.size() - 1) {
            onBottomReachedListener.onBottomReached(event.getTimeStamp());
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {

        this.onBottomReachedListener = onBottomReachedListener;
    }

    public interface OnBottomReachedListener {

        void onBottomReached(long timestamp);

    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView place, time, latitude, longitude;
        String url;

        EventViewHolder(View itemView) {
            super(itemView);
            place = itemView.findViewById(R.id.place_text);
            time = itemView.findViewById(R.id.time_text);
            latitude = itemView.findViewById(R.id.latitude_text);
            longitude = itemView.findViewById(R.id.longitude_text);
        }

        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            Context context = itemView.getContext();
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(context.getString(R.string.earthquake_url_key), url);
            context.startActivity(intent);
            v.setEnabled(true);
        }

        void bind(Earthquake event) {
            url = event.getUrl();
            place.setText(event.getPlace());
            time.setText(getDate(event.getTimeStamp()));
            latitude.setText(String.valueOf(event.getLatitude()));
            longitude.setText(String.valueOf(event.getLongitude()));
            itemView.setOnClickListener(this);
        }

        private String getDate(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            return DateFormat.format("MM-dd-yyyy hh:mm:ss", cal).toString();
        }
    }


}
