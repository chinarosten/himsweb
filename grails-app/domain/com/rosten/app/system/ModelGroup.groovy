package com.rosten.app.system

import java.io.Serializable;

class ModelGroup implements Serializable{
	Model model
	Group1 group
	
	static ModelGroup create(Model model,Group1 group,boolean flush = false) {
		new ModelGroup(model: model,group: group).save(flush: flush, insert: true)
	}

	static boolean remove(Model model,Group1 group,boolean flush = false) {
		ModelGroup instance = ModelGroup.findByModelAndGroup(model,group)
		if (!instance) {
			return false
		}
		instance.delete(flush: flush)
		true
	}

	static void removeAll(Group1 group) {
		executeUpdate 'DELETE FROM ModelGroup WHERE group=:group', [group: group]
	}

	static void removeAll(Model model) {
		executeUpdate 'DELETE FROM ModelGroup WHERE model=:model', [model: model]
	}

	static mapping = {
		id composite: ['model', 'group']
		version false
		table "ROSTEN_MODEL_GROUP"
	}
}
