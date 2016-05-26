package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

public class SerializerUtils {

	/**
	 * 初始化序列化工厂类
	 */
	private static final SerializerFactory DEFAULT_SERIALIZER_FACTORY = new SerializerFactory();

	/**
	 * 反序列化，构造原对象
	 * 
	 * @param bytes
	 *            对象字节流
	 * @return 目标对象或者null
	 */
	public static Serializable deSerialize(byte[] bytes) {
		if (bytes != null) {
			ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			Hessian2Input hi = new Hessian2Input(is);
			try {
				hi.setSerializerFactory(DEFAULT_SERIALIZER_FACTORY);
				Object target = hi.readObject();
				return (Serializable) target;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					hi.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 序列化，将原对象序列化成字节流
	 * 
	 * @param target
	 *            目标对象
	 * @return 目标直接流或者null
	 */
	public static byte[] serialize(Serializable target) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Hessian2Output ho = new Hessian2Output(os);
		try {
			ho.setSerializerFactory(DEFAULT_SERIALIZER_FACTORY);
			ho.writeObject(target);
			ho.flush();
			return os.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ho.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}

