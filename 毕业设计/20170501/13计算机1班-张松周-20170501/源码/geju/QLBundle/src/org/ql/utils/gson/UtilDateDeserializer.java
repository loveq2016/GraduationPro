package org.ql.utils.gson;

import java.lang.reflect.Type;

import org.ql.utils.debug.QLLog;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class UtilDateDeserializer implements JsonDeserializer<java.util.Date> {

	public java.util.Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		QLLog.e("UtilDateDeserializer--->");
		return new java.util.Date(json.getAsJsonPrimitive().getAsLong());
	}
}
