package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Inference {
	private int inferenceId; // the inference's ID in the database
	private String name;
	private String description;
	private ArrayList<InferenceMethod> methodsAvailable = null; // methods used to create this particular inference
	private static Context context;
	
	// methods is assumed to be a unique list (i.e. one obtained from our DB query which has the "DISTINCT" stipulation)
	public static ArrayList<Inference> getInferencesFromMethods (ArrayList<InferenceMethod> methods, Context _context) { // {{{
		context = _context;
		ArrayList<Inference> inferences = new ArrayList<Inference>();
		for (InferenceMethod method : methods) {
			Inference inference = new Inference(method.getInferenceId());

			Inference target = null;
			for (Inference cur : inferences) {
				if (inference.getInferenceId() == cur.getInferenceId()) {
					target = cur;
				}
			}

			if (target == null) {
				target = inference;
				inferences.add(target);
			}

			target.methodsAvailable.add(method);
		}

		return inferences;
	} // }}}

	private Inference(int inferenceId) { // {{{
		this.inferenceId = inferenceId;

		SQLiteDatabase db = null;
		DataBaseHelper myDbHelper = new DataBaseHelper(context);
        try {
        	myDbHelper.createDataBase();
			db = myDbHelper.openDataBase();
		} catch (Exception sqle) {
			sqle.printStackTrace();
		}
		
		//SQLiteDatabase db = SQLiteDatabase.openDatabase(AppFilterData.INFERENCE_DB_FILE, null, SQLiteDatabase.OPEN_READONLY);
		if (db != null) {
	        Cursor result = db.query("Inferences", new String[]{"name", "description"}, "inferenceID = ?", new String[]{Integer.toString(this.inferenceId)}, null, null, null, "1");
			result.moveToFirst();
			this.name = result.getString(0);
			this.description = result.getString(1);
			db.close();
		}

		this.methodsAvailable = new ArrayList<InferenceMethod>();
	} // }}}

	public int getInferenceId () {
		return inferenceId;
	}
	public String getName () {
		return name;
	}
	public String getDescription () {
		return description;
	}

	public ArrayList<InferenceMethod> getInferenceMethods () {
		return methodsAvailable;
	}
}
