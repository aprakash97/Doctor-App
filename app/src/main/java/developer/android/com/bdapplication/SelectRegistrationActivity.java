package developer.android.com.bdapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SelectRegistrationActivity extends AppCompatActivity {

    private Button doctorButton, patientButton;
    private TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_registration);

        doctorButton = findViewById(R.id.doctorButton);
        patientButton = findViewById(R.id.patientButton);
        backButton = findViewById(R.id.backButton);

        doctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRegistrationActivity.this, DoctorRegistrationActivity.class);
                startActivity(intent);
            }
        });

        patientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRegistrationActivity.this, PatientRegistrationActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}