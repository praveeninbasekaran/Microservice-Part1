/**
 * 
 */
package com.patcher.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.patcher.user.domain.UserProfile;

/**
 * @author 91984
 *
 */
public interface UserRepository extends MongoRepository<UserProfile, Integer>{

}
