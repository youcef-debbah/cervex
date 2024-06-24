/*
 * Copyright (c) 2018 youcef debbah (youcef-kun@hotmail.fr)
 *
 * This file is part of cervex.
 *
 * cervex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cervex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cervex.  If not, see <http://www.gnu.org/licenses/>.
 *
 * created on 2018/12/08
 * @header
 */

package com.rhcloud.cervex_jsoftware95.beans;

import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.exceptions.CannotDeleteUser;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

/**
 * General utility to manage {@link User users} in the underlying persistence unit.
 * 
 * @author youcef debbah
 */
@Local
public interface UserManager {

    public List<ArticleApply> getAllApplies();

    public List<ArticleApply> getApplies(String username);

    public List<User> getAllUsers();

    public User getUser(String username);

    /**
     * Returns information about the caller (current user) from the underlying persistence unit.
     * <p>
     * If this method could not find any user in the persistence unit who has a {@code username}
     * equals to the caller {@code name} it will throw an {@link InformationSystemException}, If any
     * other problem occur this method throw an {@link EJBException}
     * <p>
     * This method should never return null
     * 
     * @return The User object that identifies the caller
     * @throws InformationSystemException
     *         If the caller does not exist in the underlying persistence unit
     * @throws EJBException
     *         Thrown to indicate some unexpected internal error
     */
    public User getCurrentUser();

    /**
     * Set the {@code id} and {@code creationDate} of the given {@code newUser} object, then add it
     * to the underlying persistence unit.
     * <p>
     * This method will throw an {@link InformationSystemException} if:
     * <ul>
     * <li>There is already a {@link User} in the persistence unit that have the same {@code id} as
     * the given {@code newUser}</li>
     * 
     * <li>One of these getters returns {@code null} on the given {@code newUser}:
     * <ol>
     * <li>{@link User#getUsername()}</li>
     * <li>{@link User#getPassword()}</li>
     * <li>{@link User#getRole()}</li>
     * <li>{@link User#getPhoneNumber()}</li>
     * <li>{@link User#getEmail()}</li>
     * </ol>
     * </li>
     * </ul>
     * When this method throw an exception it is guaranteed that all changes has been rolled back
     * 
     * @param newUser
     *        The user to be added
     * @throws CanNotCreateUser
     *         If the {@code newUser} already exist in the persistence unit or it is missing a
     *         property or same username or email are existed
     * 
     */
    public void create(User newUser);

    /**
     * Updates the informations about the given {@code user} in the underlying persistence unit.
     * This operation is done by comparing the {@code id} of the given {@code user} with the
     * {@code id} of users stored in the persistence unit, after finding the correct user all it's
     * properties (except {@code creationDate}) will be updated
     * <p>
     * This method will throw an {@link InformationSystemException} if:
     * <ul>
     * <li>There is no {@link User} in the persistence unit that have the same {@code id} as the
     * given {@code user}</li>
     * 
     * <li>One of these getters returns {@code null} on the given {@code user}:
     * <ol>
     * <li>{@link User#getUsername()}</li>
     * <li>{@link User#getPassword()}</li>
     * <li>{@link User#getRole()}</li>
     * <li>{@link User#getPhoneNumber()}</li>
     * <li>{@link User#getEmail()}</li>
     * </ol>
     * </li>
     * </ul>
     * Note that the value of {@link User#getCreationDate()} is not used, because this method does
     * not update the {@code creatingDate} property
     * <p>
     * When this method throw an exception it is guaranteed that all changes has been rolled back
     * 
     * @param user
     *        The user to be updated
     * @throws FakeExp
     * @throws InformationSystemException
     *         If if the {@code user} does not exist in the persistence unit or it is missing a
     *         property
     * @throws EJBException
     *         Thrown to indicate some unexpected internal error
     */
    public void updateUser(User user);
    
    /**
     * Removes the {@link User} with equivalent {@code username} from the underlying persistence
     * unit and its demands and articles and messages and make his comments to null if {@code deleteComment} is false or delete 
     * comments if {@code deleteComment} is true
     * @param canDelete if true delete User else throw CannotDeleteUser Exception
     * @param username
     * @param deleteComment
     * @throws NullPointerException
     *         if {@code username} is null
     * @throws NoResultException
     *         Thrown if there is no {@code User} with the given {@code username} can be found
     * @throws CannotDeleteUser
     * 			if some error happened during deleting User or its contents
     */
    public void delete(String username,boolean deleteComment,boolean canDelete);

}
