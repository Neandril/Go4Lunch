package com.neandril.go4lunch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.models.RestaurantAutocompleteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for autocomplete field
 */

public class AutocompleteAdapter extends ArrayAdapter<RestaurantAutocompleteModel> {

    private List<RestaurantAutocompleteModel> restaurantListFull;

    public AutocompleteAdapter(@NonNull Context context, @NonNull List<RestaurantAutocompleteModel> restaurantList) {
        super(context, 0, restaurantList);

        restaurantListFull = new ArrayList<>(restaurantList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return restaurantFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.search_edit_text, parent, false
            );
        }

        TextView tvName = convertView.findViewById(R.id.restaurantName);
        TextView tvVicinity = convertView.findViewById(R.id.restaurantVicinity);

        RestaurantAutocompleteModel restaurantAutocompleteModel = getItem(position);

        if (restaurantAutocompleteModel != null) {
            tvName.setText(restaurantAutocompleteModel.getRestaurantName());
            tvVicinity.setText(restaurantAutocompleteModel.getRestaurantVicinity());
        }

        return convertView;
    }

    private Filter restaurantFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<RestaurantAutocompleteModel> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(restaurantListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (RestaurantAutocompleteModel item : restaurantListFull) {
                    if (item.getRestaurantName().toLowerCase().contains(filterPattern)){
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((RestaurantAutocompleteModel) resultValue).getRestaurantName();
        }
    };
}
