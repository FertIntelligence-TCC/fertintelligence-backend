package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyAccessRequestRepository extends JpaRepository<PropertyAccessRequestModel, Long> {

    Optional<PropertyAccessRequestModel> findByPropertyAndRequesterAndStatus(PropertyModel property, UserModel requester, AccessRequestStatus status);

    List<PropertyAccessRequestModel> findAllByProperty(PropertyModel property);

}