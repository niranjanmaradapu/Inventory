package com.otsi.retail.inventory.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.otsi.retail.inventory.errors.ErrorResponse;

import io.netty.channel.unix.Errors.NativeIoException;
import reactor.netty.http.client.PrematureCloseException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	private Logger log = LogManager.getLogger(CustomExceptionHandler.class);

	@ExceptionHandler(value = RecordNotFoundException.class)
	public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException recordNotException) {
		ErrorResponse<?> error = new ErrorResponse<>(404, "No Records Found");
		log.error("error response is:" + error);
		return new ResponseEntity<Object>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = DuplicateRecordException.class)
	public ResponseEntity<Object> handleDuplicateRecordException(DuplicateRecordException duplicateRecordException) {
		ErrorResponse<?> error = new ErrorResponse<>(402, "duplicate record already exists");
		log.error("error response is:" + error);
		return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = InvalidDataException.class)
	public ResponseEntity<Object> handleInvalidDataException(InvalidDataException invalidDataException) {
		ErrorResponse<?> error = new ErrorResponse<>(403, "something is missing,please give valid data");
		log.error("error response is:" + error);
		return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = ServiceDownException.class)
	public ResponseEntity<Object> serviceDownException(ServiceDownException serviceDownException) {
		ErrorResponse<?> error = new ErrorResponse<>(406, "Internal calling service is down");
		log.error("error response is:" + error);
		return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = ParentBarcodeFoundException.class)
	public ResponseEntity<Object> handleParentBarcodeFoundException(
			ParentBarcodeFoundException parentBarcodeFoundException) {
		ErrorResponse<?> error = new ErrorResponse<>(404,
				"parent barcode record is no more...please enter current barcode");
		log.error("error response is:" + error);
		return new ResponseEntity<Object>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = PrematureCloseException.class)
	public ResponseEntity<Object> handlePrematureCloseException(PrematureCloseException prematureCloseException) {
		ErrorResponse<?> error = new ErrorResponse<>(500, prematureCloseException.getMessage());
		return new ResponseEntity<Object>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = NativeIoException.class)
	public ResponseEntity<Object> handleNativeIoException(NativeIoException nativeIoException) {
		ErrorResponse<?> error = new ErrorResponse<>(500, nativeIoException.getMessage());
		return new ResponseEntity<Object>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = InvalidPriceException.class)
	public ResponseEntity<Object> handleInvalidPriceException(InvalidPriceException invalidPriceException) {
		ErrorResponse<?> error = new ErrorResponse<>(400, "price is greater thean zero");
		return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = InvalidBarcodeException.class)
	public ResponseEntity<Object> handleInvalidBarcodeException(InvalidBarcodeException invalidBarcodeException) {
		ErrorResponse<?> error = new ErrorResponse<>(400, "barcode was invalid");
		log.error("error response is:" + error);
		return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = InvalidDateException.class)
	public ResponseEntity<Object> handleInvalidDateException(InvalidDateException invalidDateException) {
		ErrorResponse<?> error = new ErrorResponse<>(400, "From date is greater than To date");
		log.error("error response is:" + error);
		return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
	}
}
