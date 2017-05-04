package org.ql.utils.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class UtilDateSerializer implements JsonSerializer<java.util.Date> {

	public JsonElement serialize(java.util.Date src, Type typeOfSrc,
			JsonSerializationContext context) {
		return new JsonPrimitive(src.getTime());
	}
}
