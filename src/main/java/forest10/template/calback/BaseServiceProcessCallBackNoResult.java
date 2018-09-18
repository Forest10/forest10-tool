package forest10.template.calback;

/**
 * 业务处理回调
 *
 * @author Forest10
 * @date 2018/9/12 下午5:26
 */
public abstract class BaseServiceProcessCallBackNoResult extends BaseServiceProcessCallBack<Void> {

	@Override
	public Void process() {
		processNoResult();
		return null;
	}

	protected abstract void processNoResult();
}
