package erc._core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ERC_Logger {
	 
	public static Logger logger = LogManager.getLogger("ERC");
 
	/*
	 * �ȉ��̃��\�b�h�͂킴�킴�Ăяo���Ȃ��Ă��A
	 * TutorialLogger.logger.trace(msg);���ŌĂяo���Ύ��������̂ł͂���B
	 * �o�͂��郍�O�ɁA��^����G���[���O�Ȃǂ��܂߂ďo�������ꍇ�́A�ȉ��̂悤�ȃ��\�b�h���D���ɃJ�X�^�}�C�Y���āA
	 * ���O���o�������ꏊ�ł��̃N���X�̃��\�b�h���ĂԂ悤�ɂ���ƁA������Ԃ��ȗ��ł���B
	 */
	public static void error(String msg) {
		ERC_Logger.logger.error(msg);
	}
 
	public static void info(String msg) {
		ERC_Logger.logger.info(msg);
	}
 		
	public static void warn(String msg) {
		ERC_Logger.logger.warn(msg);
	}
	
	public static void debugInfo(String msg) {
		info(msg);
	}
 
}