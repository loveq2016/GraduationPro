package org.ql.utils.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class SQLDateDeserializer implements JsonDeserializer<java.sql.Date> {

	public java.sql.Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		return new java.sql.Date(json.getAsJsonPrimitive().getAsLong());
	}
}
