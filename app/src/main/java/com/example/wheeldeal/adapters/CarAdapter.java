package com.example.wheeldeal.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    private Context context;
    private List<Car> cars;

    public CarAdapter(Context context, List<Car> cars){
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
        private TextView tvItemCarOwner;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvCarRate = itemView.findViewById(R.id.tvCarRate);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
            tvItemCarOwner = itemView.findViewById(R.id.tvItemCarOwner);
            // later add on click listener for detailed view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        Log.i(TAG, "going to details view");
                        Car car = cars.get(position);
                        ParcelableCar c = new ParcelableCar(car);
                        Intent i = new Intent(context, CarDetailsActivity.class);
                        i.putExtra(ParcelableCar.class.getSimpleName(), Parcels.wrap(c));
                        context.startActivity(i);
                    }
                }
            });
        }
        public void bind(Car car){
            tvCarName.setText(car.getModel());
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
//                Glide.with(context).load(image.getUrl()).into(ivCarImage);
            } else {
                ivCarImage.setVisibility(View.GONE);
            }
            tvItemCarOwner.setText(car.getOwner().getUsername());
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
