package com.example.slasher.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.slasher.R;
import com.example.slasher.modular.PreferencesManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

public class CharacterFragment extends Fragment {

    private PreferencesManager preferencesManager;
    private TextView healthPointText;
    private TextView combatPointText;
    private TextView abstinencePointText;
    private TextView recklessnessBalanceText;
    private EditText characterNameEdit;
    private EditText characterBookmark;
    private Spinner characterTypeSpinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_character, container, false);

        preferencesManager = new PreferencesManager(requireContext());

        healthPointText = rootView.findViewById(R.id.health_point_text);
        combatPointText = rootView.findViewById(R.id.combat_point_text);
        abstinencePointText = rootView.findViewById(R.id.abstinence_point_text);
        recklessnessBalanceText = rootView.findViewById(R.id.recklessness_balance_text);
        characterNameEdit = rootView.findViewById(R.id.character_name_edit);
        characterBookmark = rootView.findViewById(R.id.character_bookmark);
        characterTypeSpinner = rootView.findViewById(R.id.character_type_spinner);
        FloatingActionButton rollDiceFab = rootView.findViewById(R.id.roll_dice_fab);

        setupButtons(rootView);
        setupSpinner();

        rollDiceFab.setOnClickListener(v -> rollDiceToast());

        loadSavedData();

        return rootView;
    }

    private void rollDiceToast() {
        // Először megjelenítjük a "Dice thrown..." üzenetet
        Toast.makeText(requireContext(), "Dice thrown...", Toast.LENGTH_SHORT).show();

        // Késleltetjük az eredmény megjelenítését 1 másodperccel később
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int diceValue = rollDiceValue();
            showCustomToast(diceValue);
        }, 1000); // 1 másodperc késleltetés
    }

    private void showCustomToast(int diceValue) {
        // Egyéni elrendezés felfújása a Toast üzenethez
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // Kép beállítása a dobott érték alapján
        ImageView imageView = layout.findViewById(R.id.toast_image);
        int resId = getResources().getIdentifier("dice_" + diceValue, "drawable", requireContext().getPackageName());
        imageView.setImageResource(resId);

        // Szöveg beállítása
        TextView textView = layout.findViewById(R.id.toast_text);
        textView.setText("... and the result is: " + diceValue);

        // Toast létrehozása és megjelenítése
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private int rollDiceValue() {
        Random random = new Random();
        return random.nextInt(6) + 1;
    }

    private void setupButtons(View rootView) {
        Button healthPlusButton = rootView.findViewById(R.id.health_plus_button);
        Button healthMinusButton = rootView.findViewById(R.id.health_minus_button);
        Button combatPlusButton = rootView.findViewById(R.id.combat_plus_button);
        Button combatMinusButton = rootView.findViewById(R.id.combat_minus_button);
        Button abstinencePlusButton = rootView.findViewById(R.id.abstinence_plus_button);
        Button abstinenceMinusButton = rootView.findViewById(R.id.abstinence_minus_button);
        Button recklessnessPlusButton = rootView.findViewById(R.id.recklessness_plus_button);
        Button recklessnessMinusButton = rootView.findViewById(R.id.recklessness_minus_button);

        healthPlusButton.setOnClickListener(v -> updatePoints("health_point", 1));
        healthMinusButton.setOnClickListener(v -> updatePoints("health_point", -1));
        combatPlusButton.setOnClickListener(v -> updatePoints("combat_point", 1));
        combatMinusButton.setOnClickListener(v -> updatePoints("combat_point", -1));
        abstinencePlusButton.setOnClickListener(v -> updatePoints("abstinence_point", 1));
        abstinenceMinusButton.setOnClickListener(v -> updatePoints("abstinence_point", -1));
        recklessnessPlusButton.setOnClickListener(v -> updatePoints("recklessness_balance", 1));
        recklessnessMinusButton.setOnClickListener(v -> updatePoints("recklessness_balance", -1));
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.character_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        characterTypeSpinner.setAdapter(adapter);
    }

    private void updatePoints(String key, int delta) {
        int currentPoints = preferencesManager.getInt(key, 0);
        int newPoints = currentPoints + delta;
        preferencesManager.saveInt(key, newPoints);

        switch (key) {
            case "health_point":
                healthPointText.setText(String.valueOf(newPoints));
                break;
            case "combat_point":
                combatPointText.setText(String.valueOf(newPoints));
                break;
            case "abstinence_point":
                abstinencePointText.setText(String.valueOf(newPoints));
                break;
            case "recklessness_balance":
                recklessnessBalanceText.setText(String.valueOf(newPoints));
                break;
        }
    }

    private void loadSavedData() {
        healthPointText.setText(String.valueOf(preferencesManager.getInt("health_point", 0)));
        combatPointText.setText(String.valueOf(preferencesManager.getInt("combat_point", 0)));
        abstinencePointText.setText(String.valueOf(preferencesManager.getInt("abstinence_point", 0)));
        recklessnessBalanceText.setText(String.valueOf(preferencesManager.getInt("recklessness_balance", 0)));
        characterNameEdit.setText(preferencesManager.getString("character_name", ""));
        characterBookmark.setText(preferencesManager.getString("character_bookmark", ""));
        //noinspection unchecked
        characterTypeSpinner.setSelection(((ArrayAdapter<String>) characterTypeSpinner.getAdapter())
                .getPosition(preferencesManager.getString("character_type", "Killer")));
    }

    @Override
    public void onPause() {
        super.onPause();
        preferencesManager.saveString("character_name", characterNameEdit.getText().toString());
        preferencesManager.saveString("character_bookmark", characterBookmark.getText().toString());
        preferencesManager.saveString("character_type", characterTypeSpinner.getSelectedItem().toString());
    }
}
