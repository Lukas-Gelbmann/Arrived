package at.fhooe.mc.android.Arrived;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

/**
 * this fragment gets started when somebody pressed on the deletebutton in insidelistview
 */
public class DeleteDialogFragment extends DialogFragment {

    private static final String TAG = "xdd";
    public String deleteName;

    /**
     * whenever the dialogfragment gets started
     * @param savedInstanceState saved instance
     * @return returns a dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "DeleteDialogFragment::onCreateDialog(): dialog created");
        deleteName = getArguments().getString("name");
        AlertDialog.Builder bob = new AlertDialog.Builder(getContext());
        bob.setMessage(R.string.delete_fragment_message);
        //if okay: make toast, item gets deleted in main activity
        bob.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "DeleteDialogFragment::onCreateDialog(): item gets deleted");
                Toast.makeText(getContext(), deleteName + " deleted", Toast.LENGTH_SHORT).show();
                ((InsideListView)getActivity()).itemGetsDeleted(deleteName);
            }
        });
        //if cancel: nothing happens
        bob.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "DeleteDialogFragment::onCreateDialog(): return to InsideListView");
            }
        });
        return bob.create();
    }
}
