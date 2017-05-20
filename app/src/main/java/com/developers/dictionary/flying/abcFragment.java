package com.developers.dictionary.flying;



import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by gurtej on 16/1/17.
 */
public class abcFragment extends Fragment {

    Button btnA, btnB, btnC, btnD, btnE, btnF, btnG, btnH, btnI, btnJ, btnK, btnL, btnM, btnN, btnO, btnP, btnQ, btnR, btnS, btnT, btnU, btnV, btnW, btnX, btnY, btnZ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_abc,container,false);
        btnA = (Button) rootView.findViewById(R.id.btnA);
        btnB = (Button) rootView.findViewById(R.id.btnB);
        btnC = (Button) rootView.findViewById(R.id.btnC);
        btnD = (Button) rootView.findViewById(R.id.btnD);
        btnE = (Button) rootView.findViewById(R.id.btnE);
        btnF = (Button) rootView.findViewById(R.id.btnF);
        btnG = (Button) rootView.findViewById(R.id.btnG);
        btnH = (Button) rootView.findViewById(R.id.btnH);
        btnI = (Button) rootView.findViewById(R.id.btnI);
        btnJ = (Button) rootView.findViewById(R.id.btnJ);
        btnK = (Button) rootView.findViewById(R.id.btnK);
        btnL = (Button) rootView.findViewById(R.id.btnL);
        btnM = (Button) rootView.findViewById(R.id.btnM);
        btnN = (Button) rootView.findViewById(R.id.btnN);
        btnO = (Button) rootView.findViewById(R.id.btnO);
        btnP = (Button) rootView.findViewById(R.id.btnP);
        btnQ = (Button) rootView.findViewById(R.id.btnQ);
        btnR = (Button) rootView.findViewById(R.id.btnR);
        btnS = (Button) rootView.findViewById(R.id.btnS);
        btnT = (Button) rootView.findViewById(R.id.btnT);
        btnU = (Button) rootView.findViewById(R.id.btnU);
        btnV = (Button) rootView.findViewById(R.id.btnV);
        btnW = (Button) rootView.findViewById(R.id.btnW);
        btnX = (Button) rootView.findViewById(R.id.btnX);
        btnY = (Button) rootView.findViewById(R.id.btnY);
        btnZ = (Button) rootView.findViewById(R.id.btnZ);

        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'a');
            }
        });

        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'b');
            }
        });

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'c');
            }
        });

        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'d');
            }
        });

        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'e');
            }
        });

        btnF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'f');
            }
        });

        btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'g');
            }
        });

        btnH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'h');
            }
        });

        btnI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'i');
            }
        });

        btnJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'j');
            }
        });

        btnK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'k');
            }
        });

        btnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'l');
            }
        });

        btnM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'m');
            }
        });

        btnN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'n');
            }
        });

        btnO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'o');
            }
        });

        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'p');
            }
        });

        btnQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'q');
            }
        });

        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'r');
            }
        });

        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'s');
            }
        });

        btnT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'t');
            }
        });

        btnU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'u');
            }
        });

        btnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'v');
            }
        });

        btnW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'w');
            }
        });

        btnX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'x');
            }
        });

        btnY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'y');
            }
        });

        btnZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                WordsActivity.startNewActivity(c,'z');
            }
        });

        return rootView;
    }

}
