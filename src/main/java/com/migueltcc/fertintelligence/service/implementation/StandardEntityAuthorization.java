package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.config.UserDataSeeder;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import org.springframework.security.access.AccessDeniedException;

import java.util.Objects;

final class StandardEntityAuthorization {

    private StandardEntityAuthorization() {
    }

    static boolean isSupremeUser(UserModel user) {
        return user != null
                && (UserDataSeeder.ADMIN_USER.equals(user.getUsername())
                || UserDataSeeder.ADMIN_USER.equals(user.getEmail()));
    }

    static boolean isOwner(UserModel resourceOwner, UserModel requester) {
        return resourceOwner != null
                && requester != null
                && resourceOwner.getId() != null
                && Objects.equals(resourceOwner.getId(), requester.getId());
    }

    static boolean isStandardEntity(UserModel resourceOwner, boolean publicEntity) {
        return publicEntity && isSupremeUser(resourceOwner);
    }

    static void assertCanRead(UserModel resourceOwner, boolean publicEntity, UserModel requester) {
        if (!isOwner(resourceOwner, requester) && !isStandardEntity(resourceOwner, publicEntity)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso.");
        }
    }

    static void assertSupremeUser(UserModel requester) {
        if (!isSupremeUser(requester)) {
            throw new AccessDeniedException("Somente o usuário supremo pode criar, editar ou remover entidades padrão.");
        }
    }
}
