package forest10.exception;

/**
 * @author Forest10
 * @date 2018/9/18 下午12:28
 */
public class BizException extends RuntimeException {

	public BizException() {
	}

	public BizException(String message) {
		super(message);
	}

	public BizException(String msgTemplate, Object... args) {
		super(String.format(msgTemplate, args));
	}

	public BizException(String message, Throwable cause) {
		super(message, cause);
	}

	public BizException(Throwable cause, String msgTemplate, Object... args) {
		super(String.format(msgTemplate, args), cause);
	}

	public BizException(Throwable cause) {
		super(cause);
	}

}