package com.rosten.app.system

import java.io.Serializable;

class ModelUser implements Serializable{
	
	Model model
    User user
	
	static ModelUser create(Model model,User user,boolean flush = false) {
		new ModelUser(model: model,user: user).save(flush: flush, insert: true)
	}

	static boolean remove(Model model,User user,boolean flush = false) {
		ModelUser instance = ModelUser.findByModelAndUser(model,user)
		if (!instance) {
			return false
		}
		instance.delete(flush: flush)
		true
	}

	static void removeAll(User user) {
		executeUpdate 'DELETE FROM ModelUser WHERE user=:user', [user: user]
	}

	static void removeAll(Model model) {
		executeUpdate 'DELETE FROM ModelUser WHERE model=:model', [model: model]
	}

	static mapping = {
		id composite: ['model', 'user']
		version false
		table "ROSTEN_MODEL_USER"
	}
}
