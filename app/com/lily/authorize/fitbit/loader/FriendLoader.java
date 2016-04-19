package com.lily.authorize.fitbit.loader;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.Friend;
import com.lily.mongo.models.User;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class FriendLoader implements Loader {

	@Override
	public void load(Object obj) throws LoaderException {
		Friend friend = (Friend) obj;
		try {
			insertRecords(friend);
		} catch (Throwable e) {
			throw new LoaderException(e);
		}
	}

	/**
	 * Delete records.
	 */
	private void insertRecords(Friend friend) {
		if (friend != null) {

			Friend oldFriend = Friend.find().filter("userId", friend.userId)
					.get();

			if (oldFriend == null) {
				User.insertAll(friend.users);
				friend.insert();
			} else {
				if (friend.users != null) {
					friend.users.forEach(usr -> {
						
						List<User> findFriendList = oldFriend.users.stream()
								.filter(x -> x.encodedId.equals(usr.encodedId))
								.collect(Collectors.toList());
						
						if (findFriendList == null
								|| findFriendList.size() == 0) {
							usr.insert();
							oldFriend.users.add(usr);
						} else {
							User frUser = findFriendList.get(0);
							ObjectId id = frUser.id;
							try {
								BeanUtils.copyProperties(frUser, usr);
								frUser.id = id;
								frUser.update();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
				oldFriend.update();
			}

		}
	}
}