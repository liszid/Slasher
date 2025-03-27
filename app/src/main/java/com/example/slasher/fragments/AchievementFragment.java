package com.example.slasher.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.slasher.R;
import com.example.slasher.modular.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @noinspection ALL*/
public class AchievementFragment extends Fragment {

    private PreferencesManager preferencesManager;
    private ListView achievementListView;
    private AchievementAdapter achievementAdapter;
    private List<String> achievementItems;
    private Map<String, Integer> itemQuantities;
    private final String[] predefinedAchievements = {
            "Hockey Mask"
            ,"William Shatner mask"
            ,"A gas mask"
            ,"A dark fisherman's oilskin"
            ,"A popcorn pan"
            ,"An advertisement for Silver Shamrock customes ripped from magazine"
            ,"A prom queen tiara"
            ,"A VHS of the movie Slaughter High"
            ,"A fake Santa Claus beard"
            ,"An old cordless telephone"
            ,"The Necronomicon"
            ,"A light blue cheerleader top embroidered with an 'L'"
            ,"A 1981 graduation diploma"
            ,"A train ticket dated December 31, 1980"
            ,"Blue sports shorts with the name 'Angela Baker' ebroidered"
            ,"A love letter from someone named Lisa addressed to Professor Reynolds"
            ,"A map of the rocky Mountains, Colorado"
            ,"A prescription for anxiolytics in the name of Tina Shepard, signed by Dr. Crews"
            ,"A key to a room of the Bates motel, numbered '1'"
            ,"A prospectus from Pendleton University"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_achievements, container, false);

        preferencesManager = new PreferencesManager(requireContext());

        achievementListView = rootView.findViewById(R.id.achievement_list);
        Button addButton = rootView.findViewById(R.id.add_button);

        achievementItems = new ArrayList<>();
        itemQuantities = preferencesManager.getInventory("achievements");

        achievementAdapter = new AchievementAdapter(achievementItems, itemQuantities);
        achievementListView.setAdapter(achievementAdapter);

        addButton.setOnClickListener(v -> showAddAchievementDialog());

        loadSavedData();

        return rootView;
    }

    private void showAddAchievementDialog() {
        List<String> availableAchievements = new ArrayList<>();
        for (String achievement : predefinedAchievements) {
            if (!itemQuantities.containsKey(achievement)) {
                availableAchievements.add(achievement);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        ListView itemList = dialogView.findViewById(R.id.item_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, availableAchievements);
        itemList.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        itemList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = availableAchievements.get(position);
            addAchievement(selectedItem);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addAchievement(String newItem) {
        int quantity = itemQuantities.getOrDefault(newItem, 0) + 1;
        itemQuantities.put(newItem, quantity);
        if (!achievementItems.contains(newItem)) {
            achievementItems.add(newItem);
        }
        achievementAdapter.notifyDataSetChanged();
        preferencesManager.saveInventory("achievements", itemQuantities);
    }

    private void useAchievement(String item) {
        int quantity = itemQuantities.getOrDefault(item, 0);
        if (quantity > 0) {
            quantity--;
            if (quantity == 0) {
                achievementItems.remove(item);
                itemQuantities.remove(item);
            } else {
                itemQuantities.put(item, quantity);
            }
            achievementAdapter.notifyDataSetChanged();
            preferencesManager.saveInventory("achievements", itemQuantities);
        }
    }

    private void loadSavedData() {
        itemQuantities.putAll(preferencesManager.getInventory("achievements"));
        achievementItems.addAll(itemQuantities.keySet());
        achievementAdapter.notifyDataSetChanged();
    }

    private class AchievementAdapter extends ArrayAdapter<String> {

        private final List<String> items;
        private final Map<String, Integer> quantities;

        public AchievementAdapter(List<String> items, Map<String, Integer> quantities) {
            super(requireContext(), android.R.layout.simple_list_item_2, android.R.id.text1, items);
            this.items = items;
            this.quantities = quantities;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.achievement_item, parent, false);
            }
            TextView itemText = convertView.findViewById(android.R.id.text1);
            TextView quantityText = convertView.findViewById(R.id.item_quantity);
            Button useButton = convertView.findViewById(R.id.use_button);

            String item = items.get(position);
            itemText.setText(item);
            quantityText.setText(String.valueOf(quantities.get(item)));
            useButton.setOnClickListener(v -> useAchievement(item));

            return convertView;
        }
    }
}
