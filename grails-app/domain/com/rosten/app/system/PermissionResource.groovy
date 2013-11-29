package com.rosten.app.system

import java.io.Serializable;

class PermissionResource implements Serializable{
	Permission permission
	Resource resource
	
	static PermissionResource create(Resource resource, Permission permission, boolean flush = false) {
		new PermissionResource(resource: resource, permission: permission).save(flush: flush, insert: true)
	}
	static boolean remove(Resource resource, Permission permission, boolean flush = false) {
		PermissionResource instance = PermissionResource.findByResourceAndPermission(resource, permission)
		if (!instance) {
			return false
		}
		instance.delete(flush: flush)
		true
	}
	static void removeAll(Resource resource) {
		executeUpdate 'DELETE FROM PermissionResource WHERE resource=:resource', [resource: resource]
	}

	static void removeAll(Permission permission) {
		executeUpdate 'DELETE FROM PermissionResource WHERE permission=:permission', [permission: permission]
	}
	static mapping = {
		id composite: ['resource', 'permission']
		version false
		table "ROSTEN_PERMISSION_RESOURCE"
	}
}
