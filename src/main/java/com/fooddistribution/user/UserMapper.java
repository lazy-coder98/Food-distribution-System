package com.fooddistribution.user;

import com.fooddistribution.user.dto.UserResponse;
import com.fooddistribution.util.ObjectIdMapper;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                ObjectIdMapper.toString(user.getId()),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
