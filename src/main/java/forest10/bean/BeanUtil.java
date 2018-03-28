package forest10.bean;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Forest10
 * 2017/12/7
 */
public class BeanUtil {

	public static boolean fieldInEntity(Object entityClass, String fieldName) {
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

	public static Object getValueByProviedField(Object entityClass, String fieldName) {

		if (!fieldInEntity(entityClass, fieldName)) {
			return null;
		}
		Class cls = entityClass.getClass();
		Object object = null;
		try {
			object = Stream.of(cls.getDeclaredMethods()).filter(method -> (
							method.getName().contains("get") &&
									method.getName().substring(3).
											toLowerCase().equals(fieldName.toLowerCase())
					)
			).findAny().get().invoke(entityClass);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return object;

	}


}
