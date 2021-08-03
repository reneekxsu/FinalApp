package com.example.wheeldeal.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.wheeldeal.R;
import com.example.wheeldeal.activities.CarDetailsActivity;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.ParcelableCar;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.Viewholder> {
    public static final String TAG = "CarAdapter";

    private Activity context;
    private List<Car> cars;

    public CarAdapter(Activity context, List<Car> cars){
        this.context = context;
        this.cars = cars;
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CarAdapter.Viewholder holder, int position) {
        Car car = cars.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{
        private TextView tvCarName;
        private TextView tvCarRate;
        private ImageView ivCarImage;
        private TextView tvCarMakeModelYear, tvNumSeats, tvAddress;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvCarName = itemView.findViewById(R.id.tvCarListingName);
            tvCarRate = itemView.findViewById(R.id.tvCarRate);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
            tvCarMakeModelYear = itemView.findViewById(R.id.tvMakeModelYear);
            tvNumSeats = itemView.findViewById(R.id.tvNumSeats);
            // later add on click listener for detailed view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        Car car = cars.get(position);
                        ParcelableCar c = new ParcelableCar(car);
                        Intent i = new Intent(context, CarDetailsActivity.class);
                        i.putExtra(ParcelableCar.class.getSimpleName(), Parcels.wrap(c));
                        Pair<View, String> p1 = Pair.create((View)tvCarMakeModelYear, "makemodelyear");
                        Pair<View, String> p2 = Pair.create((View)tvCarRate, "rate");
                        Pair<View, String> p3 = Pair.create((View)ivCarImage, "image");
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(context, p1, p2, p3);
                        context.startActivity(i, options.toBundle());
                    }
                }
            });
        }
        public void bind(Car car){
            tvCarRate.setText("$" + car.getRate() + "/day");
            ParseFile image = car.getImage();
            if (image != null) {
                ivCarImage.setVisibility(View.VISIBLE);
                MultiTransformation multiLeft = new MultiTransformation(
                        new CenterCrop(),
                        new RoundedCornersTransformation(25, 10));

                Glide.with(context)
                        .load(image.getUrl())
                        .transform(multiLeft)
                        .into(ivCarImage);
            } else {
                ivCarImage.setVisibility(View.GONE);
            }
            String name = car.getName();
            if (name != null && !name.equals("")){
                tvCarName.setVisibility(View.VISIBLE);
                tvCarName.setText(car.getName());
            } else {
                tvCarName.setVisibility(View.GONE);
            }
            tvCarMakeModelYear.setText(car.getMake() + " " + car.getModel() + " " + car.getYear());
            tvNumSeats.setText("Seats " + car.getPassengers() + " passengers");
        }
    }

    public void clear(){
        cars.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Car> list) {
        cars.addAll(list);
    }
}
