package com.example.slasher.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.slasher.R;
import com.example.slasher.modular.PreferencesManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;
import java.util.Random;

public class FightFragment extends Fragment {

    private PreferencesManager preferencesManager;
    private LinearLayout fightResultsLayout;
    private TextView abstinencePointsTextView;
    private TextView recklessnessBalanceTextView;
    private int opponentAssaultScore;
    private int defenseScore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fight, container, false);

        preferencesManager = new PreferencesManager(requireContext());

        fightResultsLayout = rootView.findViewById(R.id.fight_results_layout);
        abstinencePointsTextView = rootView.findViewById(R.id.abstinence_points);
        recklessnessBalanceTextView = rootView.findViewById(R.id.recklessness_balance);

        Button startStandardConfrontation = rootView.findViewById(R.id.start_standard_confrontation);
        Button startSpecialConfrontation = rootView.findViewById(R.id.start_special_confrontation);
        FloatingActionButton rollDiceFab = rootView.findViewById(R.id.roll_dice_fab);

        startStandardConfrontation.setOnClickListener(v -> startStandardConfrontation(false));
        startSpecialConfrontation.setOnClickListener(v -> startSpecialConfrontation(false));
        rollDiceFab.setOnClickListener(v -> rollDiceToast());

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

    private void startStandardConfrontation(boolean isContinue) {
        if (preferencesManager.getInt("health_point", 0) == 0) {
            addFightResult("Game Over! You have no health points left.");
            return;
        }
        if (!isContinue) {
            removeAllViews();
        }
        int opponentAssaultScore = rollDice() * 2;
        int defenseScore = calculateDefenseScore();

        continueStandardConfrontation(opponentAssaultScore, defenseScore);
    }

    private void continueStandardConfrontation(int opponentAssaultScore, int defenseScore) {
        if (defenseScore > opponentAssaultScore) {
            addFightResult("Win! Your defense score: " + defenseScore + ", Opponent's assault score: " + opponentAssaultScore);
        } else {
            loseHealthPoints();
            if (preferencesManager.getInt("health_point", 0) > 0) {
                addFightResult("Lose! Your defense score: " + defenseScore + ", Opponent's assault score: " + opponentAssaultScore +
                        ". You lost 2 health points. Fight for survival! Restarting...");
                startStandardConfrontation(true);
            } else {
                addFightResult("Game Over! You lost all your health points.");
            }
        }
    }

    private void startSpecialConfrontation(boolean isContinue) {
        if (preferencesManager.getInt("health_point", 0) == 0) {
            addFightResult("Game Over! You have no health points left.");
            return;
        }
        if (!isContinue) {
            removeAllViews();
        }
        int opponentAssaultScore = rollDice() * 2;
        int defenseScore = calculateDefenseScore();
        int abstinenceScore = preferencesManager.getInt("abstinence_point", 0);
        int recklessnessBalance = preferencesManager.getInt("recklessness_balance", 0);

        defenseScore += abstinenceScore + recklessnessBalance;

        continueSpecialConfrontation(opponentAssaultScore, defenseScore);
    }

    private void continueSpecialConfrontation(int opponentAssaultScore, int defenseScore) {
        if (defenseScore > opponentAssaultScore) {
            addFightResult("Win! Your defense score: " + defenseScore + ", Opponent's assault score: " + opponentAssaultScore);
        } else {
            loseHealthPoints();
            if (preferencesManager.getInt("health_point", 0) > 0) {
                addFightResult("Lose! Your defense score: " + defenseScore + ", Opponent's assault score: " + opponentAssaultScore +
                        ". You lost 2 health points. Fight for survival! Restarting...");
                startSpecialConfrontation(true);
            } else {
                addFightResult("Game Over! You lost all your health points.");
            }
        }
    }

    private void loseHealthPoints() {
        int currentHealth = preferencesManager.getInt("health_point", 6);
        preferencesManager.saveInt("health_point", Math.max(currentHealth - 2, 0));
    }

    private int rollDice() {
        Random random = new Random();
        return random.nextInt(6) + 1;
    }

    private int calculateDefenseScore() {
        int combatPoints = preferencesManager.getInt("combat_point", 0);

        Map<String, Integer> inventory = preferencesManager.getInventory("inventory");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            String item = entry.getKey();
            int quantity = entry.getValue();

            if (item.contains("CP")) {
                String[] parts = item.split(" - ");
                int cp = Integer.parseInt(parts[1].split(" ")[0]);
                combatPoints += cp * quantity;
            }
        }

        return combatPoints + rollDice();
    }

    private void updatePoints(String key, int delta, TextView textView) {
        int currentPoints = preferencesManager.getInt(key, 0);
        int newPoints = currentPoints + delta;
        preferencesManager.saveInt(key, newPoints);
        textView.setText(String.valueOf(newPoints));
    }

    private void updateStatsDisplay() {
        int abstinencePoints = preferencesManager.getInt("abstinence_point", 0);
        int recklessnessBalance = preferencesManager.getInt("recklessness_balance", 0);

        abstinencePointsTextView.setText(String.valueOf(abstinencePoints));
        recklessnessBalanceTextView.setText(String.valueOf(recklessnessBalance));
    }

    private void addFightResult(String result) {
        TextView resultTextView = new TextView(requireContext());
        resultTextView.setText(result);
        resultTextView.setBackgroundColor(0xCC000000);
        //noinspection deprecation
        resultTextView.setTextColor(getResources().getColor(R.color.text_white));
        fightResultsLayout.addView(resultTextView);
    }

    private void removeAllViews() {
        fightResultsLayout.removeAllViews();
    }
}
