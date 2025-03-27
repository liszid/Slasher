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
import java.util.List;
import java.util.Map;

/** @noinspection ALL*/
public class InventoryFragment extends Fragment {

    private PreferencesManager preferencesManager;
    private ListView inventoryListView;
    private InventoryAdapter inventoryAdapter;
    private List<String> inventoryItems;
    private Map<String, Integer> itemQuantities;
    private String[] predefinedItems = {
            "Baseball bat - 1 CP",
            "Kitchen knife - 1 CP",
            "Chainsaw - 3 CP",
            "Revolver - 4 CP",
            "Energy drink - 1 HP",
            "Band-aid - 2 HP",
            "First-aid kit - 3 HP"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);

        preferencesManager = new PreferencesManager(requireContext());

        inventoryListView = rootView.findViewById(R.id.inventory_list);
        Button addButton = rootView.findViewById(R.id.add_button);

        inventoryItems = new ArrayList<>();
        itemQuantities = preferencesManager.getInventory("inventory");

        inventoryAdapter = new InventoryAdapter(inventoryItems, itemQuantities);
        inventoryListView.setAdapter(inventoryAdapter);

        addButton.setOnClickListener(v -> showAddItemDialog());

        loadSavedData();

        return rootView;
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        ListView itemList = dialogView.findViewById(R.id.item_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, predefinedItems);
        itemList.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        itemList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = predefinedItems[position];
            addItem(selectedItem);
            dialog.dismiss();
        });

        dialog.show();
    }


    private void addItem(String newItem) {
        int quantity = itemQuantities.getOrDefault(newItem, 0) + 1;
        itemQuantities.put(newItem, quantity);
        if (!inventoryItems.contains(newItem)) {
            inventoryItems.add(newItem);
        }
        inventoryAdapter.notifyDataSetChanged();
        preferencesManager.saveInventory("inventory", itemQuantities);
    }

    private void useItem(String item) {
        int quantity = itemQuantities.getOrDefault(item, 0);
        if (quantity > 0) {
            if (item.contains("CP")) {
                String[] parts = item.split(" - ");
                int cp = Integer.parseInt(parts[1].split(" ")[0]);
                updatePoints("combat_point", cp);
            } else if (item.contains("HP")) {
                String[] parts = item.split(" - ");
                int hp = Integer.parseInt(parts[1].split(" ")[0]);
                updatePoints("health_point", hp);
            }
            quantity--;
            if (quantity == 0) {
                inventoryItems.remove(item);
                itemQuantities.remove(item);
            } else {
                itemQuantities.put(item, quantity);
            }
            inventoryAdapter.notifyDataSetChanged();
            preferencesManager.saveInventory("inventory", itemQuantities);
        }
    }

    private void updatePoints(String key, int delta) {
        int currentPoints = preferencesManager.getInt(key, 0);

        // Limit the maximum health points to 6
        if (key.equals("health_point")) {
            int newPoints = Math.min(currentPoints + delta, 6);
            preferencesManager.saveInt(key, newPoints);
        } else {
            int newPoints = currentPoints + delta;
            preferencesManager.saveInt(key, newPoints);
        }
    }

    private void loadSavedData() {
        itemQuantities.putAll(preferencesManager.getInventory("inventory"));
        inventoryItems.addAll(itemQuantities.keySet());
        inventoryAdapter.notifyDataSetChanged();
    }

    private class InventoryAdapter extends ArrayAdapter<String> {

        private final List<String> items;
        private final Map<String, Integer> quantities;

        public InventoryAdapter(List<String> items, Map<String, Integer> quantities) {
            super(requireContext(), android.R.layout.simple_list_item_2, android.R.id.text1, items);
            this.items = items;
            this.quantities = quantities;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_item, parent, false);
            }
            TextView itemText = convertView.findViewById(android.R.id.text1);
            TextView quantityText = convertView.findViewById(R.id.item_quantity);
            Button useButton = convertView.findViewById(R.id.use_button);

            String item = items.get(position);
            itemText.setText(item);
            quantityText.setText(String.valueOf(quantities.get(item)));
            useButton.setOnClickListener(v -> useItem(item));

            return convertView;
        }
    }
}
