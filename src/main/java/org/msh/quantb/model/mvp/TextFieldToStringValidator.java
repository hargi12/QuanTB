package org.msh.quantb.model.mvp;

import org.jdesktop.beansbinding.Validator;
import org.msh.quantb.services.mvp.Messages;

/**
 * Validation text field value.
 * Empty or null values are invalid.
 * @author User
 *
 */
public class TextFieldToStringValidator extends Validator<String> {
	@Override
	public Result validate(String value) {
		if (value==null || value.isEmpty()){
			return new Result(null, Messages.getString("Error.Validation.TextField.WrongValue"));
		}
		return null;
	}

}
