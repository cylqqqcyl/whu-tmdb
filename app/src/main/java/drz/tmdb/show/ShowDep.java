package drz.tmdb.show;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

import drz.tmdb.R;
import drz.tmdb.memory.SystemTable.DeputyTable;

public class ShowDep extends AppCompatActivity implements Serializable {
    private final int W = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int M = ViewGroup.LayoutParams.MATCH_PARENT;
    private TableLayout show_tab;
    //private ArrayList<String> objects = new ArrayList<String> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ShowDep", "oncreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_result);

        Intent intent = getIntent();
        Bundle bundle0 = intent.getExtras();
        showDepTab((DeputyTable) bundle0.getSerializable("DeputyTable"));

    }

    private void showDepTab(DeputyTable deputyt) {
        int tabCol = 4;
        int tabH = deputyt.deputyTable.size();
        Object oj1,oj2;
        String stemp1, stemp2, stemp3, stemp4;
        String[] satemp;

        show_tab = findViewById(R.id.rst_tab);

        for (int i = 0; i <= tabH; i++) {
            TableRow tableRow = new TableRow(this);
            if (i == 0) {
                stemp1 = "originid";
                stemp2 = "deputyid";
                stemp3 = "deputyrule";
                stemp4 = "deputyname";
            } else {
                oj1 = deputyt.deputyTable.get(i-1).originid;
                oj2 = deputyt.deputyTable.get(i-1).deputyid;
                satemp = deputyt.deputyTable.get(i-1).deputyrule;
                stemp1 = oj1.toString();
                stemp2 = oj2.toString();
                stemp3 = satemp[0].toString();
                stemp4 = deputyt.deputyTable.get(i-1).deputyname;
            }
            for (int j = 0; j < tabCol; j++) {
                TextView tv = new TextView(this);
                switch (j) {
                    case 0:
                        tv.setText(stemp1);
                        break;
                    case 1:
                        tv.setText(stemp2);
                        break;
                    case 2:
                        tv.setText(stemp3);
                        break;
                    case 3:
                        tv.setText(stemp4);
                        break;
                }
                tv.setGravity(Gravity.CENTER);
                tv.setBackgroundResource(R.drawable.tab_bg);
                tv.setTextSize(28);
                tableRow.addView(tv);
            }
            show_tab.addView(tableRow, new TableLayout.LayoutParams(M, W));

        }

    }

}

