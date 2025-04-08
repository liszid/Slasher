package com.example.slasher.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.slasher.R;
import com.example.slasher.modular.PreferencesManager;

import java.util.HashMap;

public class SettingsFragment extends Fragment {

    private PreferencesManager preferencesManager;
    private int healthPointChange = 0;
    private int combatPointChange = 0;
    private int abstinencePointChange = 0;
    private int recklessnessBalanceChange = 0;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        preferencesManager = new PreferencesManager(requireContext());

        TextView settingsLabel = rootView.findViewById(R.id.settings_label);
        TextView roundLabel = rootView.findViewById(R.id.round_label);
        Button gameEndButton = rootView.findViewById(R.id.game_end_button);
        Button resetButton = rootView.findViewById(R.id.reset_button);

        settingsLabel.setText("Settings");

        int gameEndCount = preferencesManager.getInt("game_end_count", 0);
        roundLabel.setText("Number of Rounds: " + gameEndCount);

        gameEndButton.setOnClickListener(v -> {
            showGameEndDialog();
            Toast.makeText(requireContext(), "Round ended - " + gameEndCount, Toast.LENGTH_SHORT).show();
        });

        resetButton.setOnClickListener(v -> {
            resetGame();
            Toast.makeText(requireContext(), "Game has been reset", Toast.LENGTH_SHORT).show();
        });

        return rootView;
    }

    private void showGameEndDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme);
        builder.setTitle("Game End");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_game_end, null);
        builder.setView(dialogView);

        TextView healthPointChangeText = dialogView.findViewById(R.id.health_point_change);
        TextView combatPointChangeText = dialogView.findViewById(R.id.combat_point_change);
        TextView abstinencePointChangeText = dialogView.findViewById(R.id.abstinence_point_change);
        TextView recklessnessBalanceChangeText = dialogView.findViewById(R.id.recklessness_balance_change);

        Button healthPlus = dialogView.findViewById(R.id.health_plus);
        Button healthMinus = dialogView.findViewById(R.id.health_minus);
        Button combatPlus = dialogView.findViewById(R.id.combat_plus);
        Button combatMinus = dialogView.findViewById(R.id.combat_minus);
        Button abstinencePlus = dialogView.findViewById(R.id.abstinence_plus);
        Button abstinenceMinus = dialogView.findViewById(R.id.abstinence_minus);
        Button recklessnessPlus = dialogView.findViewById(R.id.recklessness_plus);
        Button recklessnessMinus = dialogView.findViewById(R.id.recklessness_minus);

        healthPlus.setOnClickListener(v -> {
            healthPointChange++;
            healthPointChangeText.setText(String.valueOf(healthPointChange));
        });
        healthMinus.setOnClickListener(v -> {
            healthPointChange--;
            healthPointChangeText.setText(String.valueOf(healthPointChange));
        });

        combatPlus.setOnClickListener(v -> {
            combatPointChange++;
            combatPointChangeText.setText(String.valueOf(combatPointChange));
        });
        combatMinus.setOnClickListener(v -> {
            combatPointChange--;
            combatPointChangeText.setText(String.valueOf(combatPointChange));
        });

        abstinencePlus.setOnClickListener(v -> {
            abstinencePointChange++;
            abstinencePointChangeText.setText(String.valueOf(abstinencePointChange));
        });
        abstinenceMinus.setOnClickListener(v -> {
            abstinencePointChange--;
            abstinencePointChangeText.setText(String.valueOf(abstinencePointChange));
        });

        recklessnessPlus.setOnClickListener(v -> {
            recklessnessBalanceChange++;
            recklessnessBalanceChangeText.setText(String.valueOf(recklessnessBalanceChange));
        });
        recklessnessMinus.setOnClickListener(v -> {
            recklessnessBalanceChange--;
            recklessnessBalanceChangeText.setText(String.valueOf(recklessnessBalanceChange));
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            updatePoints("health_point", healthPointChange);
            updatePoints("combat_point", combatPointChange);
            updatePoints("abstinence_point", abstinencePointChange);
            updatePoints("recklessness_balance", recklessnessBalanceChange);

            // Increment game end count
            int gameEndCount = preferencesManager.getInt("game_end_count", 0);
            preferencesManager.saveInt("game_end_count", gameEndCount + 1);
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void resetGame() {
        preferencesManager.saveInt("health_point", 6);
        preferencesManager.saveInt("combat_point", 0);
        preferencesManager.saveInt("abstinence_point", 6);
        preferencesManager.saveInt("recklessness_balance", 0);
        preferencesManager.saveString("character_name", "");
        preferencesManager.saveString("character_type", "Survivor");
        preferencesManager.removeKey("inventory");
        preferencesManager.removeKey("achievements");
        preferencesManager.saveMap("game_map", new HashMap<>());
        preferencesManager.saveInt("game_end_count", 0);
    }

    private void updatePoints(String key, int delta) {
        int currentPoints = preferencesManager.getInt(key, 0);
        int newPoints = currentPoints + delta;
        preferencesManager.saveInt(key, newPoints);
    }
}
