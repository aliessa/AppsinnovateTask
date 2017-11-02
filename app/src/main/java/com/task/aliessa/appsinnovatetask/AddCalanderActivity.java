package com.task.aliessa.appsinnovatetask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddCalanderActivity extends AppCompatActivity implements View.OnClickListener {
@BindView(R.id.Btn_AddEvent)
Button Btn_Add;
@BindView(R.id.Etxt_Title)
    EditText Etxt_Title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calander);
        ButterKnife.bind(this);

        Btn_Add.setOnClickListener(this);




    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Btn_AddEvent:

                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("allDay", true);
                intent.putExtra("rrule", "FREQ=YEARLY");
                intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                intent.putExtra("title", Etxt_Title.getText());
                startActivity(intent);
                break;
        }
    }
}
