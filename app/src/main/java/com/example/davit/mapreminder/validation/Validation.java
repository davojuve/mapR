package com.example.davit.mapreminder.validation;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * class Validation
 */
public class Validation {

    /** check the input field has any text or not
    *   return true if it contains text otherwise false
    */
    public static boolean hasText(EditText editText, String errMsg) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }



    /** return true if the input field is valid, based on the parameter passed */
    public static boolean isValid(EditText editText, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        return hasText(editText, errMsg);

    }

    /** is number ? */
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

}
