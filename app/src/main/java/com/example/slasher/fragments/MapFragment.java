package com.example.slasher.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.slasher.R;
import com.example.slasher.modular.PreferencesManager;
import com.example.slasher.views.CanvasView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.slasher.modular.OnStepClickListener;

public class MapFragment extends Fragment implements OnStepClickListener {

    private CanvasView canvasView;
    private PreferencesManager preferencesManager;
    private Map<Integer, List<Integer>> stepsMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        preferencesManager = new PreferencesManager(getContext());
        stepsMap = preferencesManager.getMap("game_map");

        if (stepsMap == null) {
            stepsMap = new HashMap<>();
        }

        if (!stepsMap.containsKey(0)) {
            stepsMap.put(0, new ArrayList<>());
        }

        canvasView = view.findViewById(R.id.canvasView);
        canvasView.setStepsMap(stepsMap);
        canvasView.setOnStepClickListener(this);
        canvasView.setOnStepLongPressListener(this);

        canvasView.post(() -> {
            int screenWidth = view.getWidth();
            int screenHeight = view.getHeight();
            canvasView.setOffset(screenWidth / 2, screenHeight / 4);
        });

        return view;
    }

    @Override
    public void onStepClick(int step) {
        showEditStepDialog(step);
    }

    @Override
    public void onStepLongPress(int step) {}

    private void showEditStepDialog(int step) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        View customView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom, null);
        EditText inputField = customView.findViewById(R.id.dialogInput);
        Button addStepButton = customView.findViewById(R.id.addStepButton);

        inputField.setText(String.valueOf(step));
        builder.setView(customView);
        builder.setTitle("Lépés módosítása: " + step);

        // **Gombok hozzáadása itt!**
        builder.setPositiveButton("Mentés", (dialog, which) -> {
            saveStep(step, inputField.getText().toString());
            dialog.dismiss(); // **Automatikusan bezárjuk a dialógust!**
        });

        builder.setNegativeButton("Törlés", (dialog, which) -> {
            deleteStep(step);
            dialog.dismiss(); // **Dialógus bezárása törlés után**
        });

        builder.setNeutralButton("Mégse", (dialog, which) -> dialog.dismiss());

        // **Dialógus létrehozása, hogy a gombok hozzá legyenek adva**
        AlertDialog editStepDialog = builder.create();

        addStepButton.setOnClickListener(v -> {
            editStepDialog.dismiss();
            showAddStepDialog(step);
        });

        editStepDialog.setOnDismissListener(dialog -> {
            canvasView.setStepsMap(stepsMap);
            canvasView.setOffset(0, 0);
            canvasView.invalidate();
        });

        editStepDialog.show();
    }

    private void addNewStep(int parentStep, String newStepValueStr) {
        try {
            int newStepValue = Integer.parseInt(newStepValueStr);

            if (!stepsMap.containsKey(parentStep)) {
                stepsMap.put(parentStep, new ArrayList<>());
            }

            if (!stepsMap.containsKey(newStepValue)) {
                stepsMap.put(newStepValue, new ArrayList<>());
            }

            stepsMap.get(parentStep).add(newStepValue);
            preferencesManager.saveMap("game_map", stepsMap);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveStep(int step, String newStepValueStr) {
        try {
            int newStepValue = Integer.parseInt(newStepValueStr);
            stepsMap.put(newStepValue, stepsMap.getOrDefault(step, new ArrayList<>()));

            for (Map.Entry<Integer, List<Integer>> entry : stepsMap.entrySet()) {
                List<Integer> connections = entry.getValue();
                if (connections.contains(step)) {
                    connections.remove(Integer.valueOf(step));
                    connections.add(newStepValue);
                }
            }
            stepsMap.remove(step);
            preferencesManager.saveMap("game_map", stepsMap);
            canvasView.setStepsMap(stepsMap);
            canvasView.setOffset(0,0);
            canvasView.invalidate();

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteStep(int step) {
        if (stepsMap.containsKey(step)) {
            stepsMap.remove(step);
            for (List<Integer> connections : stepsMap.values()) {
                connections.remove(Integer.valueOf(step));
            }
            preferencesManager.saveMap("game_map", stepsMap);
            canvasView.setStepsMap(stepsMap);
            canvasView.setOffset(0,0);
            canvasView.invalidate();
        } else {
            Toast.makeText(getContext(), "Step cannot be found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddStepDialog(int parentStep) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        builder.setTitle("Add new Step to (" + parentStep + ")");

        View customView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom, null);
        EditText inputField = customView.findViewById(R.id.dialogInput);
        inputField.setHint("Add Step value");

        builder.setView(customView);

        builder.setPositiveButton("Add New", (dialog, which) -> {
            String newStepValueStr = inputField.getText().toString();
            addNewStep(parentStep, newStepValueStr);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setOnDismissListener(dialog -> {
            canvasView.post(() -> {
                canvasView.setStepsMap(stepsMap);
                canvasView.setOffset(0,0);
                canvasView.invalidate();
            });
        });

        builder.show();
    }
}
