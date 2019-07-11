package pbl;


import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;


import static com.kuka.roboticsAPI.motionModel.BasicMotions.*;

import com.kuka.roboticsAPI.controllerModel.Controller;

import com.kuka.roboticsAPI.deviceModel.LBR;


public class pozStart extends RoboticsAPIApplication {


	private LBR lbr;
	private MediaFlangeIOGroup mediaFlange;
	private Controller kuka_Sunrise_Cabinet_1;

	public void initialize() {
		lbr = getContext().getDeviceFromType(LBR.class);
		// inicjalizacja kontrolera
		kuka_Sunrise_Cabinet_1 = getController("KUKA_Sunrise_Cabinet_1" );
		mediaFlange = new MediaFlangeIOGroup(kuka_Sunrise_Cabinet_1);
		mediaFlange.setLEDBlue( false );

	}

	public void run() {
		lbr.move(ptp(getApplicationData().getFrame("/P3")).setJointVelocityRel(0.5));
		} 
	}




