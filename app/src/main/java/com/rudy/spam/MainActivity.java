package com.rudy.spam;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etPhone;
        private Button btnSpam;
            private TextView tvLogs;
                private int successCount = 0;
                    private int failedCount = 0;

                        @Override
                            protected void onCreate(Bundle savedInstanceState) {
                                    super.onCreate(savedInstanceState);
                                            setContentView(R.layout.activity_main);

                                                    etPhone = findViewById(R.id.et_phone);
                                                            btnSpam = findViewById(R.id.btn_spam);
                                                                    tvLogs = findViewById(R.id.tv_logs);

                                                                            btnSpam.setOnClickListener(v -> runSpam());
                                                                                }

                                                                                    private void runSpam() {
                                                                                            String phone = etPhone.getText().toString().trim();
                                                                                                    if (phone.isEmpty()) {
                                                                                                                tvLogs.append("Masukkan nomor dulu!\n");
                                                                                                                            return;
                                                                                                                                    }

                                                                                                                                            tvLogs.setText("");
                                                                                                                                                    successCount = 0;
                                                                                                                                                            failedCount = 0;
                                                                                                                                                                    tvLogs.append("> Starting Spam...\n");

                                                                                                                                                                            String cleanNum = NetworkHelper.normalizePhone(phone);

                                                                                                                                                                                    // Panggil SEMUA 14 Provider di sini
                                                                                                                                                                                            SpamEngine.sendBisatopup(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                    SpamEngine.sendTitipku(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                            SpamEngine.sendJogjakita(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                    SpamEngine.sendCandireload(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                            SpamEngine.sendSpeedcash(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                    SpamEngine.sendKerbel(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                            SpamEngine.sendMitradelta(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                                    SpamEngine.sendAgenpayment(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                                            SpamEngine.sendZ4reload(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                                                    SpamEngine.sendSinga(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                                                            SpamEngine.sendKtakilat(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                                                                    SpamEngine.sendUangme(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                                                                            SpamEngine.sendCairin(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                                                                                    SpamEngine.sendAdiraku(cleanNum, (name, success) -> updateUI(name, success));
                                                                                                                                                                                                                                                                                                        }

                                                                                                                                                                                                                                                                                                            private void updateUI(String name, boolean success) {
                                                                                                                                                                                                                                                                                                                    runOnUiThread(() -> {
                                                                                                                                                                                                                                                                                                                                if (success) {
                                                                                                                                                                                                                                                                                                                                                successCount++;
                                                                                                                                                                                                                                                                                                                                                                tvLogs.append("[✓] " + name + ": SENT\n");
                                                                                                                                                                                                                                                                                                                                                                            } else {
                                                                                                                                                                                                                                                                                                                                                                                            failedCount++;
                                                                                                                                                                                                                                                                                                                                                                                                            tvLogs.append("[✗] " + name + ": FAIL\n");
                                                                                                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                                                                                                                                                                });
                                                                                                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                                                                                                    }