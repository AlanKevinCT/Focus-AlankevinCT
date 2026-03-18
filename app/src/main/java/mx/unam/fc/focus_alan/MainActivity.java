package mx.unam.fc.focus_alan;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.Locale;

/**
 * @author Alan Kevin Cano Tenorio - @AlanKevinCT
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvTimerDisplay;
    private Button btnStartStop, btnReset, btnSkip;
    private ChipGroup chipGroupMode;
    private Chip chipFocus, chipBreak, chipRest;
    private LinearLayout sessionDotsContainer;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 1500000; // 25 minutos
    private int focusSessionsCompleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vinculamos la vista con el xml
        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);
        btnStartStop = findViewById(R.id.btnStartStop);
        btnReset = findViewById(R.id.btnReset);
        btnSkip = findViewById(R.id.btnSkip);
        chipGroupMode = findViewById(R.id.chipGroupMode);
        chipFocus = findViewById(R.id.chipFocus);
        chipBreak = findViewById(R.id.chipBreak);
        chipRest = findViewById(R.id.chipRest);
        sessionDotsContainer = findViewById(R.id.sessionDotsContainer);

        btnStartStop.setOnClickListener(v -> {
            if (isTimerRunning) pauseTimer();
            else startTimer();
        });

        btnReset.setOnClickListener(v -> resetTimer());
        btnSkip.setOnClickListener(v -> skipSession());

        chipGroupMode.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty() && !isTimerRunning) {
                int id = checkedIds.get(0);
                if (id == R.id.chipFocus) timeLeftInMillis = 1500000;
                else if (id == R.id.chipBreak) timeLeftInMillis = 300000;
                else if (id == R.id.chipRest) timeLeftInMillis = 900000;
                updateCountDownText();
            }
        });

        updateCountDownText();
        updateSessionDots();
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                isTimerRunning = false;
                btnStartStop.setText(getString(R.string.btn_start));
                handleSessionSwitch();
            }
        }.start();
        isTimerRunning = true;
        btnStartStop.setText(getString(R.string.btn_stop));
    }

    private void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isTimerRunning = false;
        btnStartStop.setText(getString(R.string.btn_start));
    }

    private void resetTimer() {
        pauseTimer();
        if (chipFocus.isChecked()) timeLeftInMillis = 1500000;
        else if (chipBreak.isChecked()) timeLeftInMillis = 300000;
        else if (chipRest.isChecked()) timeLeftInMillis = 900000;
        updateCountDownText();
    }

    private void skipSession() {
        pauseTimer();
        handleSessionSwitch();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvTimerDisplay.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void handleSessionSwitch() {
        if (chipFocus.isChecked()) {
            focusSessionsCompleted++;
            if (focusSessionsCompleted % 4 == 0) {
                chipRest.setChecked(true);
                timeLeftInMillis = 900000;
            } else {
                chipBreak.setChecked(true);
                timeLeftInMillis = 300000;
            }
        } else {
            chipFocus.setChecked(true);
            timeLeftInMillis = 1500000;
        }
        updateCountDownText();
        updateSessionDots();
    }

    private void updateSessionDots() {
        if (sessionDotsContainer == null) return;
        sessionDotsContainer.removeAllViews();

        for (int i = 1; i <= 4; i++) {
            View dot = new View(this);
            int size = (int) (12 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);

            int currentRound = focusSessionsCompleted % 4;
            if (currentRound == 0 && focusSessionsCompleted > 0) currentRound = 4;

            if (i <= currentRound) {
                dot.setBackgroundResource(R.drawable.dot_session_completed);
            } else {
                dot.setBackgroundResource(android.R.drawable.presence_invisible);
                dot.setAlpha(0.3f);
            }
            sessionDotsContainer.addView(dot);
        }
    }
}