package pbl;


import sun.awt.windows.ThemeReader;

import com.kuka.common.ThreadUtil;
import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.*;
import static com.kuka.roboticsAPI.motionModel.MMCMotions.*;

import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.geometricModel.World;
import com.kuka.roboticsAPI.motionModel.CartesianPTP;
import com.kuka.roboticsAPI.motionModel.IMotionContainer;
import com.kuka.roboticsAPI.motionModel.LIN;

import com.kuka.roboticsAPI.motionModel.HandGuidingMotion;
import com.kuka.roboticsAPI.motionModel.MMCMotions;
import com.kuka.roboticsAPI.motionModel.PTP;
import com.kuka.roboticsAPI.motionModel.PositionHold;
import com.kuka.roboticsAPI.motionModel.RelativeLIN;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.uiModel.ApplicationDialogType;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyBar;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;

public class Wodzenie extends RoboticsAPIApplication {

	private LBR lbr;
	private MediaFlangeIOGroup mediaFlange;
	private Controller kuka_Sunrise_Cabinet_1;
	private boolean userKey;


	private final static String start = "Aplikacja do prowadzenia recznego uruchomiona" ; 
	private final static String ready = "Gotowy do prowadzenia recznego" ; 
	private final static String esm1 = "Stan ESM1 aktywowany, gotowy do prowadzenia recznego"; 
	private final static String esm2 = "Stan ESM2 aktywowany, rozpoczyna sie ruch"; 
	private final static String end = "Koniec programu"; 

	private String xPos;
	private String yPos;
	private String zPos;
	private String xDegree;
	private String yDegree;
	private String zDegree;


	public void initialize() {
		lbr = getContext().getDeviceFromType(LBR.class);
		// inicjalizacja kontrolera
		kuka_Sunrise_Cabinet_1 = getController("KUKA_Sunrise_Cabinet_1" );
		mediaFlange = new MediaFlangeIOGroup(kuka_Sunrise_Cabinet_1);
	}

	public void run() {

		//getLogger().info("Wyswietlanie menu");
		//getApplicationUI().displayModalDialog(ApplicationDialogType.INFORMATION, start, "OK");
		//
		//		// zapalanie diody
		//		mediaFlange.setLEDBlue(true);
		//		ThreadUtil.milliSleep(2000);
		//		mediaFlange.setLEDBlue(false);
		//		//lbr.getCurrentJointPosition();
		//		
		// odczyt stanu zielonego przycisku na kolnierzu
		//		userKey = mediaFlange.getUserButton();
		//		

		//int i = 0;
		
		//lbr.move(ptp(getApplicationData().getFrame("/P1")).setJointVelocityRel(0.30));
		
		//while( i != 20 ){
			//userKey = mediaFlange.getUserButton();
		//	if(userKey == true){
				
//				Frame cmdPos = lbr.getCurrentCartesianPosition(lbr.getFlange());
//				xPos = Double.toString(cmdPos.getX());
//				yPos = Double.toString(cmdPos.getY());
//				zPos = Double.toString(cmdPos.getZ());
//				xDegree = Double.toString(cmdPos.getAlphaRad());
//				yDegree = Double.toString(cmdPos.getBetaRad());
//				zDegree = Double.toString(cmdPos.getGammaRad());
//			
//
//				getLogger().info("Pozycja X " + xPos);
//				getLogger().info("Pozycja Y " + yPos);
//				getLogger().info("Pozycja Z " + zPos);
//				getLogger().info("K¹t X " + xDegree);
//				getLogger().info("K¹t Y " + yDegree);
//				getLogger().info("K¹t Z " + zDegree);
//				
//				ThreadUtil.milliSleep(2000);
				
			//	i++;
			//}
	//	}

	
		
		
		//DZIA£AJACY KOD	

		//		lbr.setESMState("2"); // ustawienie esm2 - monitorowanie predkosci i kolizji
		//		getLogger().info(esm2);
		//		lbr.move(ptp(getApplicationData().getFrame("/P1")).setJointVelocityRel(0.30));
		//		// PROWADZENIE RECZNE
		//		lbr.setESMState("1"); // ustawienie esm1 - urzadzenie do prowadzenia recznego nieaktywne
		//		getLogger().info(esm1);
		//		lbr.move(handGuiding());
		//		
		//		lbr.setESMState("2");
		//		getLogger().info(esm2);
		//		lbr.move(ptp(getApplicationData().getFrame("/P2")).setJointVelocityRel(0.30));
		//		getLogger().info(end);



	}

}
