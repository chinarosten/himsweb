
package com.rosten.app.system
import java.io.Serializable;

class UserDepart implements Serializable {
	
	User user
	Depart depart
	
	static UserDepart create(User user, Depart depart, boolean flush = false) {
		new UserDepart(user: user, depart: depart).save(flush: flush, insert: true)
	}

	static boolean remove(User user, Depart depart, boolean flush = false) {
		UserDepart instance = UserDepart.findByUserAndDepart(user, depart)
		if (!instance) {
			return false
		}
		instance.delete(flush: flush)
		true
	}

	static void removeAll(User user) {
		executeUpdate 'DELETE FROM UserDepart WHERE user=:user', [user: user]
	}

	static void removeAll(Depart depart) {
		executeUpdate 'DELETE FROM UserDepart WHERE depart=:depart', [depart: depart]
	}

	static mapping = {
		id composite: ['depart', 'user']
		version false
		table "ROSTEN_USER_DEPART"
	}
}
