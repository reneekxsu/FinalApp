package com.example.finalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.Viewholder> {

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
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvCarRate = itemView.findViewById(R.id.tvCarRate);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
            // later add on click listener for detailed view
        }
        public void bind(Car car){
            tvCarName.setText(car.getModel());
            tvCarRate.setText("$" + car.getRate() + "/hr");
            ParseFile image = car.getImage();
            if (image != null) {
                ivCarImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(image.getUrl()).into(ivCarImage);
            } else {
                ivCarImage.setVisibility(View.GONE);
            }
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
