/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.utils.db.sqlite;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;

public class WhereBuilder {

    private final List<String> whereItems;

    private WhereBuilder() {
        this.whereItems = new ArrayList<String>();
    }

    /**
     * create new instance
     *
     * @return
     */
    public static WhereBuilder b() {
        return new WhereBuilder();
    }

    /**
     * create new instance
     * @param columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value
     * @return
     */
    public static WhereBuilder b(String columnName, String op, Object value) {
        WhereBuilder result = new WhereBuilder();
        result.appendCondition(null, columnName, op, value);
        return result;
    }

    /**
     * add AND condition
     * @param columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value
     * @return
     */
    public WhereBuilder and(String columnName, String op, Object value) {
        appendCondition(whereItems.size() == 0 ? null : "AND", columnName, op, value);
        return this;
    }

    /**
     * add OR condition
     * @param columnName
     * @param op         operator: "=","<","LIKE","IN","BETWEEN"...
     * @param value
     * @return
     */
    public WhereBuilder or(String columnName, String op, Object value) {
        appendCondition(whereItems.size() == 0 ? null : "OR", columnName, op, value);
        return this;
    }

    public WhereBuilder expr(String expr) {
        whereItems.add(" " + expr);
        return this;
    }

    public WhereBuilder expr(String columnName, String op, Object value) {
        appendCondition(null, columnName, op, value);
        return this;
    }

    public int getWhereItemSize() {
        return whereItems.size();
    }

    @Override
    public String toString() {
        if (whereItems.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE ");
        for (String item : whereItems) {
            sb.append(item);
        }
        String string = sb.toString();
        return string;
    }

    private void appendCondition(String conj, String columnName, String op, Object value) {
        StringBuilder sqlSb = new StringBuilder();

        if (whereItems.size() > 0) {
            sqlSb.append(" ");
        }

        // append conj
        if (!TextUtils.isEmpty(conj)) {
            sqlSb.append(conj + " ");
        }

        // append columnName
        sqlSb.append(columnName);

        // convert op
        if ("!=".equals(op)) {
            op = "<>";
        } else if ("==".equals(op)) {
            op = "=";
        }

        // append op & value
        if (value == null) {
            if ("=".equals(op)) {
                sqlSb.append(" IS NULL");
            } else if ("<>".equals(op)) {
                sqlSb.append(" IS NOT NULL");
            } else {
                sqlSb.append(" " + op + " NULL");
            }
        } else {
            sqlSb.append(" " + op + " ");

            if ("IN".equalsIgnoreCase(op)) {
                Iterable<?> items = null;
                if (value instanceof Iterable) {
                    items = (Iterable<?>) value;
                } else if (value.getClass().isArray()) {
                    ArrayList<Object> arrayList = new ArrayList<Object>();
                    int len = Array.getLength(value);
                    for (int i = 0; i < len; i++) {
                        arrayList.add(Array.get(value, i));
                    }
                    items = arrayList;
                }
                
                if (items != null) {
                    StringBuffer stringBuffer = new StringBuffer("(");
                    for (Object item : items) {
                    	if(item instanceof CharSequence){
                    		stringBuffer.append("'" + item + "'");
                    	}else{
                    		stringBuffer.append(item);
                    	}
                        stringBuffer.append(",");
                    }
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    stringBuffer.append(")");
                    sqlSb.append(stringBuffer.toString());
                } else {
                    throw new IllegalArgumentException("value must be an Array or an Iterable.");
                }
            } else if ("BETWEEN".equalsIgnoreCase(op)) {
                Iterable<?> items = null;
                if (value instanceof Iterable) {
                    items = (Iterable<?>) value;
                } else if (value.getClass().isArray()) {
                    ArrayList<Object> arrayList = new ArrayList<Object>();
                    int len = Array.getLength(value);
                    for (int i = 0; i < len; i++) {
                        arrayList.add(Array.get(value, i));
                    }
                    items = arrayList;
                }
                if (items != null) {
                    Iterator<?> iterator = items.iterator();
                    if (!iterator.hasNext()) throw new IllegalArgumentException("value must have tow items.");
                    	Object start = iterator.next();
                    if (!iterator.hasNext()) throw new IllegalArgumentException("value must have tow items.");
                    	Object end = iterator.next();

                    if(start instanceof CharSequence || end instanceof CharSequence){
                    	 sqlSb.append("'" + start.toString() + "'");
                         sqlSb.append(" AND ");
                         sqlSb.append("'" + end.toString() + "'");
                    }else{
                    	 sqlSb.append(start);
                         sqlSb.append(" AND ");
                         sqlSb.append(end);
                    }
                } else {
                    throw new IllegalArgumentException("value must be an Array or an Iterable.");
                }
            } else {
            	if(value instanceof CharSequence){
            		 sqlSb.append("'" + value.toString() + "'");
            	}else{
            		sqlSb.append(value);
            	}
            }
        }
        whereItems.add(sqlSb.toString());
    }
}
