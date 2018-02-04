package ftpfilebrow.example.com.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    HorizontalScrollSlideView horScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        horScrollView=findViewById(R.id.horScrollView);

//        horScrollView.setContentView(contanteView);
//        horScrollView.setOnSlideBottomListener(new HorizontalScrollSlideView.OnSlideBottomListener() {
//            @Override
//            public void onSlideBottom() {
//
//            }
//        });

    }
}
