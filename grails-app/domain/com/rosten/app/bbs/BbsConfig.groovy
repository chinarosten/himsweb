package com.rosten.app.bbs

import java.util.List;

import com.rosten.app.system.Company

class BbsConfig {
	String id
	
	//今年年代
	Integer nowYear
	
	//今年流水号
	Integer nowSN = 1
	
	//今年保留号或者废号
	List nowCancel = []
	
	//去年年代
	Integer frontYear
	
	//去年流水号
	Integer frontSN = 1
	
	//去年保留号或者废号
	List frontCancel = []
	
	//最新公告保留时间
	Integer showDays = 7
	
	static transients = ["addCancelSN","getCancelSN"]
	
	def addCancelSN ={type,sn->
		if("now".equals(type)){
			//今年
			nowCancel << sn
		}else{
			//去年
			frontCancel << sn
		}
	}
	
	//获取保留号或者废弃号
	def getCancelSN ={type->
		if("now".equals(type)){
			if(nowCancel) return nowCancel.join(",")
			else return ""
		}else{
			if(frontCancel) return frontCancel.join(",")
			else return ""
		}
	}
	
	static belongsTo = [company:Company]
	
    static constraints = {
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_BBS_CONFIG"
	}
}
