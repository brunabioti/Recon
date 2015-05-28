package com.example.bioti.reconbioti.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.bioti.reconbioti.R;

public class QuestionDialogFragment extends BaseDialogFragment {

	// ===========================================================
	// Public types
	// ===========================================================

	public interface QuestionDialogListener {
		void onQuestionAnswered(boolean accepted);
	}

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String EXTRA_MESSAGE = "message";

	// ===========================================================
	// Public static methods
	// ===========================================================

	public static QuestionDialogFragment newInstance(String message) {
		QuestionDialogFragment frag = new QuestionDialogFragment();
		Bundle args = new Bundle();
		args.putString(EXTRA_MESSAGE, message);
		frag.setArguments(args);
		return frag;
	}

	// ===========================================================
	// Private fields
	// ===========================================================

	private QuestionDialogListener mListener;

	// ===========================================================
	// Private constructor
	// ===========================================================

	public QuestionDialogFragment() {
	}

	// ===========================================================
	// Public methods
	// ===========================================================

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (QuestionDialogListener) getTargetFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException("Calling fragment must implement QuestionDialogListener interface");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String message = getArguments().getString(EXTRA_MESSAGE);
		return new AlertDialog.Builder(getActivity())
			.setMessage(message)
			.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					mListener.onQuestionAnswered(true);
					dialog.cancel();
				}
			})
			.setNegativeButton(R.string.msg_no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					mListener.onQuestionAnswered(false);
					dialog.cancel();
				}
			}).create();
	}

}