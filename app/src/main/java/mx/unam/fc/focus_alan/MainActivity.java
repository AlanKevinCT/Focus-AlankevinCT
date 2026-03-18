package mx.unam.fc.focus_alan;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.Locale;

/**
 * @author Alan Kevin Cano Tenorio - @AlanKevinCT
 */
public class MainActivity extends AppCompatActivity {

    // Creamos la vista
    private TextView tvTimerDisplay;
    private Button btnStartStop, btnReset, btnSkip;
    private ChipGroup chipGroupMode;
    private Chip chipFocus, chipBreak, chipRest;

    // Lógica del Timer
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 1500000; // 25 minutos
    private int focusSessionsCompleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vincular la vista con el xml
        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);
        btnStartStop = findViewById(R.id.btnStartStop);
        btnReset = findViewById(R.id.btnReset);
        btnSkip = findViewById(R.id.btnSkip);
        chipGroupMode = findViewById(R.id.chipGroupMode);
        chipFocus = findViewById(R.id.chipFocus);
        chipBreak = findViewById(R.id.chipBreak);
        chipRest = findViewById(R.id.chipRest);

        btnStartStop.setOnClickListener(v -> {
            if (isTimerRunning) pauseTimer();
            else startTimer();
        });

        btnReset.setOnClickListener(v -> resetTimer());
        btnSkip.setOnClickListener(v -> skipSession());

        updateCountDownText();
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
                btnStartStop.setText("START");
                handleSessionSwitch();
            }
        }.start();
        isTimerRunning = true;
        btnStartStop.setText("STOP");
    }

    private void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isTimerRunning = false;
        btnStartStop.setText("START");
    }

    private void resetTimer() {
        pauseTimer();
        if (chipFocus.isChecked()) timeLeftInMillis = 1500000;
        else if (chipBreak.isChecked()) timeLeftInMillis = 300000;
        else timeLeftInMillis = 900000;
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
    }
}