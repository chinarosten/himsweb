package com.rosten.app.system

import com.rosten.app.system.SerialNo
import com.rosten.app.util.Base64Util
import com.rosten.app.util.Util
import java.sql.Timestamp

class SerialNoService {
	
	private def serialBetaArray= [
		"UlItT08tU1MtVFQtRUUtTk4=",
		"UlItT08tU1MtTk4tVFQtRUU=",
		"UlItT08tRUUtTk4tVFQtU1M=",
		"T08tUlItU1MtVFQtRUUtTk4=",
		"T08tUlItU1MtRUUtVFQtTk4=",
		"T08tUlItU1MtTk4tVFQtRUU="
	]
	
	def addOrChangeSerial(String serialNoStr){
		def serialNo = SerialNo.first()
		if(!serialNo){
			serialNo = new SerialNo()
			serialNo.macAddress = Util.getMacAddress()
		}
		serialNo.serialNo = serialNoStr
		serialNo.createdDate = new Date()
		serialNo.save(flush:true)
	}
	/*
	 * * 试用版允许使用30天
	 * -1:表示试用版序列号不正确
	 * 0：表示试用版序列号正确
	 * 1：表示试用版序列号已过期
	 * 2：表示试用版序列号时间被强行修改
	 */
	def checkSerialBeta(){
		int result = -1;
		
		def serialNo = SerialNo.first()
		if(serialNo){
			//判断序列号是否正确
			boolean _isOk = false;
			
			for(int i = 0 ;i<serialBetaArray.size();i++){
				if(serialNo.serialNo.equals(serialBetaArray[i])){
					_isOk = true;
					break;
				}
			}
			if(_isOk){
				Timestamp nowTime = new Timestamp(System.currentTimeMillis());
				int _day = Util.getBetweenDayNumber(serialNo.getFormattedCreateDate(),Util.obj2str(nowTime));
				if(_day > 30){
					result = 1;
				}else if(_day<0){
					result = 2;
				}else{
					result = 0;
				}
			}else{
				result = -1;
			}
		}
		return result;
	}
	
	/*
	 * * 检查序列号是否正确
	 */
	def checkSerial(){
		def serialNo = SerialNo.first()
		if(serialNo){
			def serialStr = serialNo.serialNo
			Integer index = serialStr.indexOf("r1o2s3t4e5n6");
			if (index > -1) {
				String decodeString = serialStr.substring(0, index);
				String nowString = new String(Base64Util.decode(decodeString));
				if (nowString.equals(serialNo.macAddress)) {
					return true;
				}
			}
		}
		return false
	}
	
    def serviceMethod() {

    }
}
