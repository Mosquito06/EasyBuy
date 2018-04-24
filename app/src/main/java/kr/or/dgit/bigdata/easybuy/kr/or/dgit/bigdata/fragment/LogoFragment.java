package kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import kr.or.dgit.bigdata.easybuy.Main_Activity;
import kr.or.dgit.bigdata.easybuy.R;

/**
 * Created by DGIT3-12 on 2018-04-18.
 */

public class LogoFragment extends Fragment implements Animation.AnimationListener{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.logo_layout, container, false);

        String text = "EASYBUY";
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        int start = text.indexOf("B");
        if(start > -1){
            int end = start + "BUY".length();

            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.mainTextColor));

            builder.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        TextView mainText = (TextView) view.findViewById(R.id.mainText);
        mainText.setText(builder);

        ImageView mainLogo = (ImageView) view.findViewById(R.id.mainLogo);

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.main_anim);
        anim.setFillAfter(true);
        anim.setAnimationListener(this);

        mainText.startAnimation(anim);
        mainLogo.startAnimation(anim);

        return view;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        SharedPreferences loginPref = getActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String userId = loginPref.getString("userId", null);
        if(userId != null){
            Intent intent = new Intent();
            intent.setClass(getActivity(), Main_Activity.class);
            startActivity(intent);
        }else{
            getActivity().getSupportFragmentManager()
                    .beginTransaction().replace(R.id.mainLayoutFrame, new LoginFragment()).commit();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
