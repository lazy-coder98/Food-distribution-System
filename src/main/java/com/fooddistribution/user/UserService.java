package com.fooddistribution.user;

import com.fooddistribution.exception.ResourceNotFoundException;
import com.fooddistribution.security.SecurityUtils;
import com.fooddistribution.user.dto.UserResponse;
import com.fooddistribution.util.ObjectIdMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public UserResponse me() {
        User user = repository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    public Page<UserResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(UserMapper::toResponse);
    }

    public UserResponse getById(String id) {
        return UserMapper.toResponse(repository.findById(ObjectIdMapper.toObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public void delete(String id) {
        repository.deleteById(ObjectIdMapper.toObjectId(id));
    }
}
