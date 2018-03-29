package forest10.bean;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Forest10
 * 2017/12/7
 */
public class BeanUtil {

	/**
	 * @param entityClass
	 * @param fieldName   属性名
	 * @return
	 */
	private static boolean fieldInEntity(Object entityClass, String fieldName) {
		Class cls = entityClass.getClass();
		List<String> list = new ArrayList<>();
		Stream.of(cls.getDeclaredFields()).forEach(field -> {
			list.add(field.getName());
		});
		if (list.contains(fieldName)) {
			return true;
		}
		return false;
	}

	/**
	 * 拿到 Bean 里面field对应的值
	 *
	 * @param entityClass
	 * @param fieldName   属性名
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static Object getValueByProviedField(Object entityClass, String fieldName) throws InvocationTargetException, IllegalAccessException {

		if (!fieldInEntity(entityClass, fieldName)) {
			return null;
		}
		Class cls = entityClass.getClass();
		return Stream.of(cls.getDeclaredMethods()).filter(method -> (
						method.getName().contains("get") &&
								method.getName().substring(3).
										toLowerCase().equals(fieldName.toLowerCase())
				)
		).findAny().orElseGet(null).invoke(entityClass);
	}


}
