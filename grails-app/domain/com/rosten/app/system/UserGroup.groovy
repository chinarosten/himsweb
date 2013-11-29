package com.rosten.app.system

import java.io.Serializable;

class UserGroup implements Serializable{

    User user
	Group1 group
	
	static UserGroup create(User user, Group1 group, boolean flush = false) {
		new UserGroup(user: user, group: group).save(flush: flush, insert: true)
	}

	static boolean remove(User user, Group1 group, boolean flush = false) {
		UserGroup instance = UserGroup.findByUserAndGroup(user, group)
		if (!instance) {
			return false
		}
		instance.delete(flush: flush)
		true
	}

	static void removeAll(User user) {
		executeUpdate 'DELETE FROM UserGroup WHERE user=:user', [user: user]
	}

	static void removeAll(Group1 group) {
		executeUpdate 'DELETE FROM UserGroup WHERE group=:group', [group: group]
	}

	static mapping = {
		id composite: ['group', 'user']
		version false
		table "ROSTEN_USER_GROUP"
	}
}
