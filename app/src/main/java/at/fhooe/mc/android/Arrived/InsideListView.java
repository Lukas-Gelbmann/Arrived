package at.fhooe.mc.android.Arrived;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * this activity gets called when you click on an list entry in MainActivity
 */
public class InsideListView extends AppCompatActivity {

    private static final String TAG = "xdd";

    /**
     * this method gets called whenever the activity gets started
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inside_listview);
        //the 5 edittexts
        String savedExtra = getIntent().getStringExtra("name");
        TextView myText = findViewById(R.id.inside_listview_name);
        myText.setText("Name:\n" + savedExtra);

        savedExtra = getIntent().getStringExtra("phoneNumber");
        myText = findViewById(R.id.inside_listview_phoneNumber);
        myText.setText("Phone number:\n" + savedExtra);

        savedExtra = getIntent().getStringExtra("message");
        myText = findViewById(R.id.inside_listview_message);
        myText.setText("Message:\n" + savedExtra);

        savedExtra = getIntent().getStringExtra("place");
        myText = findViewById(R.id.inside_listview_place);
        myText.setText("Address:\n" + savedExtra);

        savedExtra = getIntent().getStringExtra("radius");
        myText = findViewById(R.id.inside_listview_radius);
        myText.setText("Radius:\n" + savedExtra + " m");

        //the delete button
        Button button = findViewById(R.id.delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "InsideListView::onCreate(): clicked on delete button");
                DeleteDialogFragment deleteDialogFragment = newInstance(getIntent().getStringExtra("name"));
                deleteDialogFragment.show(getSupportFragmentManager(), "delete");
            }
        });
        //the return button
        button = findViewById(R.id.returning);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "InsideListView::onCreate(): clicked on return button");
                returnWithoutDelete();
            }
        });
    }

    public static DeleteDialogFragment newInstance(String name) {
        DeleteDialogFragment f = new DeleteDialogFragment();
        // Supply name input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        f.setArguments(args);
        return f;
    }

    public void itemGetsDeleted(String name) {
        Intent i = new Intent();
        i.putExtra("name", name);
        setResult(RESULT_OK, i);
        finish();
    }

    public void returnWithoutDelete() {
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
    }
}
