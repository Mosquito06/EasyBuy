package kr.or.dgit.bigdata.easybuy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String text = "EASYBUY";
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        int start = text.indexOf("B");
        if(start > -1){
            int end = start + "BUY".length();

            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.maingTextColor));

            builder.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        TextView mainText = (TextView) findViewById(R.id.mainText);
        mainText.setText(builder);

    }
}
