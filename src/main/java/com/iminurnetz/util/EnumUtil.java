package com.iminurnetz.util;

public class EnumUtil {
	public static <E extends Enum<E>> String getSerializationName(E e) {
		return e.name().toLowerCase().replace('_', '-');
	}

	public static <E extends Enum<E>> E getSerializedEnum(Class<E> enumType, String serializationName) {
		return Enum.valueOf(enumType, serializationName.replace('-', '_').toUpperCase());
	}
}
