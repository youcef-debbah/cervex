package com.rhcloud.cervex_jsoftware95.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.rhcloud.cervex_jsoftware95.util.Patterns;

public class WebsiteValidator implements ConstraintValidator<Website, String> {

	@Override
	public void initialize(Website constraintAnnotation) {
		// do nothing
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value != null)
			return Patterns.WEB_URL.matcher(value).matches();
		else
			return true;
	}

}
