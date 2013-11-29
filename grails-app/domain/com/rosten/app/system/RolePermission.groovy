package com.rosten.app.system

import java.io.Serializable;

class RolePermission implements Serializable{
	Role role
	Permission permission
	
    static RolePermission create(Role role, Permission permission, boolean flush = false) {
		new RolePermission(role: role, permission: permission).save(flush: flush, insert: true)
	}

	static boolean remove(Role role, Permission permission, boolean flush = false) {
		RolePermission instance = RolePermission.findByRoleAndPermission(role, permission)
		if (!instance) {
			return false
		}
		instance.delete(flush: flush)
		true
	}

	static void removeAll(Role role) {
		executeUpdate 'DELETE FROM RolePermission WHERE role=:role', [role: role]
	}

	static void removeAll(Permission permission) {
		executeUpdate 'DELETE FROM RolePermission WHERE permission=:permission', [permission: permission]
	}

	static mapping = {
		id composite: ['role', 'permission']
		version false
		table "ROSTEN_ROLE_PERMISSION"
	}
}
