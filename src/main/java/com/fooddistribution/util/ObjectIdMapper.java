package com.fooddistribution.util;

import org.bson.types.ObjectId;

public final class ObjectIdMapper {

    private ObjectIdMapper() {
    }

    public static String toString(ObjectId objectId) {
        return objectId == null ? null : objectId.toHexString();
    }

    public static ObjectId toObjectId(String id) {
        return new ObjectId(id);
    }
}
