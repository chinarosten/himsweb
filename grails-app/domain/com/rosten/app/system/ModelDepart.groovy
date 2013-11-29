package com.rosten.app.system

import java.io.Serializable;

class ModelDepart implements Serializable{

    Model model
    Depart depart
	
	static ModelDepart create(Model model,Depart depart,boolean flush = false) {
		new ModelDepart(model: model,depart: depart).save(flush: flush, insert: true)
	}

	static boolean remove(Model model,Depart depart,boolean flush = false) {
		ModelDepart instance = ModelDepart.findByModelAndDepart(model,depart)
		if (!instance) {
			return false
		}
		instance.delete(flush: flush)
		true
	}

	static void removeAll(Depart depart) {
		executeUpdate 'DELETE FROM ModelDepart WHERE depart=:depart', [depart: depart]
	}

	static void removeAll(Model model) {
		executeUpdate 'DELETE FROM ModelDepart WHERE model=:model', [model: model]
	}

	static mapping = {
		id composite: ['model', 'depart']
		version false
		table "ROSTEN_MODEL_DEPART"
	}
}
