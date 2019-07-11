package pbl;


import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import sun.awt.windows.ThemeReader;
import com.kuka.common.ThreadUtil;
import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.applicationModel.tasks.CycleBehavior;

import static com.kuka.roboticsAPI.motionModel.BasicMotions.*;
import static com.kuka.roboticsAPI.motionModel.MMCMotions.*;

import com.kuka.roboticsAPI.conditionModel.BooleanIOCondition;
import com.kuka.roboticsAPI.conditionModel.ICondition;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.AbstractFrame;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.geometricModel.World;
import com.kuka.roboticsAPI.ioModel.AbstractIO;
import com.kuka.roboticsAPI.motionModel.CartesianPTP;
import com.kuka.roboticsAPI.motionModel.IMotionContainer;
import com.kuka.roboticsAPI.motionModel.LIN;

import com.kuka.roboticsAPI.motionModel.HandGuidingMotion;
import com.kuka.roboticsAPI.motionModel.IMotion;
import com.kuka.roboticsAPI.motionModel.MMCMotions;
import com.kuka.roboticsAPI.motionModel.Motion;
import com.kuka.roboticsAPI.motionModel.PTP;
import com.kuka.roboticsAPI.motionModel.PositionHold;
import com.kuka.roboticsAPI.motionModel.RelativeLIN;
import com.kuka.roboticsAPI.motionModel.SplineOrientationType;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.uiModel.ApplicationDialogType;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyBar;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;
import com.kuka.roboticsAPI.applicationModel.tasks.RoboticsAPICyclicBackgroundTask;
public class MasazWlasny extends RoboticsAPIApplication {


	private LBR lbr;
	private MediaFlangeIOGroup mediaFlange;
	private Controller kuka_Sunrise_Cabinet_1;

	private Frame ramka;

	private RelativeLIN down;
	private RelativeLIN up;

	private boolean userButton;

	private static final int stiffnessX = 1000;
	private static final int stiffnessY = 1000;
	private static final int stiffnessZ = 1000;



	//lancuchy znakow
	private String startInfoTxt = "Aplikacja do masazu z mozliwoscia ustawiania recznego";
	private String startDecisionTxt = "Do jakiej pozycji przejsc?";
	private String positionQuestionTxt = "Czy ustawiæ efektor prostopadle?";




	public void initialize() {
		lbr = getContext().getDeviceFromType(LBR.class);
		// inicjalizacja kontrolera
		kuka_Sunrise_Cabinet_1 = getController("KUKA_Sunrise_Cabinet_1" );
		mediaFlange = new MediaFlangeIOGroup(kuka_Sunrise_Cabinet_1);
		mediaFlange.setLEDBlue( false );

	}




	public void run() {

		int startQuestion = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, startInfoTxt, "Start", "WyjdŸ");

		if (startQuestion == 1){
			getLogger().info("Koniec programu");
			return;
		}
		else getLogger().info("Start programu");

		int startDecision = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, startDecisionTxt, "Startowa", "Obecna");

		if( startDecision == 0){
			getLogger().info("Ruch do pozycji startowej");
			ThreadUtil.milliSleep(2000);
			lbr.move(ptp(getApplicationData().getFrame("/P3")).setJointVelocityRel(0.10));
		} else{
			ThreadUtil.milliSleep(2000);
		}

		// stworzenie obiektu regulatora impedancji 
		CartesianImpedanceControlMode impedanceControlMode = 	new CartesianImpedanceControlMode();
		impedanceControlMode.parametrize(CartDOF.X).setStiffness(stiffnessX);
		impedanceControlMode.parametrize(CartDOF.Y).setStiffness(stiffnessY);
		impedanceControlMode.parametrize(CartDOF.Z).setStiffness(stiffnessZ);


		down = linRel( 0 , 0,  50, 0,0,0).setJointVelocityRel(0.1).setMode(impedanceControlMode);
		up   = linRel( 0 , 0, -50 ,0,0,0).setJointVelocityRel(0.1).setMode(impedanceControlMode);



		while( true ){

			userButton = mediaFlange.getUserButton();
			lbr.move(batch(down, up));

			while( userButton == true ){

				for( int i = 0 ; i < 6 ; i ++ ){ // miganie dioda
					mediaFlange.setLEDBlue( true );
					ThreadUtil.milliSleep(50);
					mediaFlange.setLEDBlue( false );
					ThreadUtil.milliSleep(50);
				}


				getLogger().info("Rozpoczecie HG"); // handguiding
				mediaFlange.setLEDBlue( true );
				lbr.move(handGuiding());
				mediaFlange.setLEDBlue( false );
				getLogger().info("Koniec HG");


				int positionQuestion = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, positionQuestionTxt, "Tak", "Nie");

				if( positionQuestion == 0 ){ // przypisanie prostopadlego polozenia efektora i ruch ptp zmieniajacy tylko katy, pozycja xyz bez zmian
					ramka = lbr.getCommandedCartesianPosition(lbr.getFlange()); // pobranie aktualnej pozycji kartezjanskiej
					ramka.setAlphaRad(Math.toRadians(-90.0));
					ramka.setBetaRad(Math.toRadians(0.0));
					ramka.setGammaRad(Math.toRadians(-180));
					lbr.move(ptp(ramka).setJointVelocityRel(0.1));
				}

				down = linRel(0.0,0.0,-50.0, 0, 0, 0).setJointVelocityRel(0.1).setMode(impedanceControlMode);
				up   = linRel(0.0,0.0,50.0,  0, 0, 0).setJointVelocityRel(0.1).setMode(impedanceControlMode);

				getLogger().info("Pozycja zmieniona, ruch rozpocznie sie za 5 sekund" +" \n " + "wcisnij zielony przycisk w celu ponownego prowadzenia.");

				userButton = false;

				//mozliwosc zmiany 
				for( int i = 0 ; i < 501 ; i++ ){
					ThreadUtil.milliSleep(10);
					if ( i % 100 == 0 ) getLogger().info( Integer.toString(i/100) + "s" );

					userButton = mediaFlange.getUserButton();

					if ( userButton == true ){
						getLogger().info("Uruchomiono ponowne prowadzenie");
						break;
					}
				}		
			}

		} 
	}
}



