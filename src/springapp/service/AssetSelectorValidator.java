package springapp.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author oliver
 * This is used to validate the form on submit
 */
public class AssetSelectorValidator implements Validator {

	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return AssetSelector.class.equals(clazz);
	}

	public void validate(Object obj, Errors errors) {
		AssetSelector pi = (AssetSelector) obj;
		if (pi == null) {
			errors.rejectValue("ticker", "error.not-specified", null, "Value required.");
		}
	}

}