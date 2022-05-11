package developer.android.com.bdapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplachScreenActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView title, slogan;

    Animation topAnimation, bottomAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo);
        title = findViewById(R.id.title);
        slogan = findViewById(R.id.slogan);

        //Loading created animation to a variable
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Setting animations to the respective ids
        logo.setAnimation(topAnimation);
        title.setAnimation(bottomAnimation);
        slogan.setAnimation(bottomAnimation);

        int SPLASH_SCREEN = 4300;

        //Login activity showing after animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplachScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}