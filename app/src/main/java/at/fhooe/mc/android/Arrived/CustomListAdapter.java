package at.fhooe.mc.android.Arrived;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * this class is responsible for displaying an entry on the MainActivity
 */
public class CustomListAdapter extends ArrayAdapter {

    private static final String TAG = "xdd";
    //to reference the Activity
    private final Activity context;
    private final ArrayList<String> names;
    private final ArrayList<String> messages;
    private final ArrayList<String> places;

    CustomListAdapter(Activity context, ArrayList<String> names, ArrayList<String> messages, ArrayList<String> places) {
        super(context, R.layout.listview_row, names);
        this.context = context;
        this.names = names;
        this.messages = messages;
        this.places = places;
    }

    /**
     * in this method the right data gets places on the right spot
     * @param position position of the entry
     * @param view empty view
     * @param parent parent
     * @return returns a view with the entry
     */
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_row, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = rowView.findViewById(R.id.nameTextView);
        TextView messageTextField = rowView.findViewById(R.id.messageTextView);
        TextView placeTextField = rowView.findViewById(R.id.placeTextView);

        //cuts the message if too long
        String message = messages.get(position);
        if(message.length()>35)
            message= message.substring(0,35)+"...";

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(names.get(position));
        messageTextField.setText(message);
        placeTextField.setText(places.get(position));
        return rowView;
    }
}
