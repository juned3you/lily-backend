package com.lily.utils;

import javax.persistence.Query;

public class JpaUtils {

	@SuppressWarnings("unchecked")
	public static <T> T getSingleResultOrElseNull(Query query, Class<T> tclazz) {
		return (T) query.getResultList().stream().findFirst().orElse(null);
	}
}
