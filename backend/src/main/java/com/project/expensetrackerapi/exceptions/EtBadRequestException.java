package com.project.expensetrackerapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EtBadRequestException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6793246589048305867L;

	public EtBadRequestException(String message) {
        super(message);
    }
}