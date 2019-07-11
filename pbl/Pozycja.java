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
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyAlignment;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyLED;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyLEDSize;
import com.kuka.roboticsAPI.applicationModel.tasks.RoboticsAPICyclicBackgroundTask;

public class Pozycja extends RoboticsAPIApplication {


	private LBR lbr;
	private MediaFlangeIOGroup mediaFlange;
	private Controller kuka_Sunrise_Cabinet_1;



	private Frame ramka;
	private double flange, kula;
	private double alfa;
	private double beta;
	private double gamma;
	private double x;
	private double y; 
	private double z;
	private double vel = 0.1;
	private String positionQuestionTxt = "Czy ustawiæ efektor prostopadle?";

	private HandGuidingMotion handMotion;

	private RelativeLIN down_motion;
	private RelativeLIN up_motion;
	//lancuchy znakow

	private boolean userButton;

	private Tool ball;



	public void initialize() {
		lbr = getContext().getDeviceFromType(LBR.class);
		// inicjalizacja kontrolera
		kuka_Sunrise_Cabinet_1 = getController("KUKA_Sunrise_Cabinet_1" );
		mediaFlange = new MediaFlangeIOGroup(kuka_Sunrise_Cabinet_1);
		mediaFlange.setLEDBlue( false );

		ball = getApplicationData().createFromTemplate("kulaTool");
		ball.attachTo(lbr.getFlange());


		handMotion = new HandGuidingMotion();

		handMotion.setAxisLimitsMax(Math.toRadians(90), Math.toRadians(45), Math.toRadians(45),
				Math.toRadians(90), Math.toRadians(45), Math.toRadians(0), Math.toRadians(0))


				.setAxisLimitsMin(Math.toRadians(0), Math.toRadians(-15), Math.toRadians(-45),
						Math.toRadians(-25), Math.toRadians(-45), Math.toRadians(0), Math.toRadians(0))

						.setAxisLimitsEnabled(true, true, true, true, true, false, false)
						.setAxisLimitViolationFreezesAll(false)
						.setPermanentPullOnViolationAtStart( false );    

		//lbr.move(ptp(getApplicationData().getFrame("/P3")).setJointVelocityRel(0.3));
		/*

		IUserKeyBar pasekUzytkownika = getApplicationUI().createUserKeyBar("XYZ"); // stworzenie paska uzytkownika

		IUserKeyListener listener0 = new IUserKeyListener() {
			@Override
			public void onKeyEvent(IUserKey key, UserKeyEvent event) {
				if ( event == UserKeyEvent.KeyDown){	
					vel += 0.1;
					if( vel >= 1.0 ){
						vel = 1.0;
					}
					down_motion = linRel( 0.0 , 0.0, -50.0, World.Current.getRootFrame()).setJointVelocityRel(vel);//.setMode(impedanceControlMode);
					up_motion   = linRel( 0.0 , 0.0, 50.0 , World.Current.getRootFrame()).setJointVelocityRel(vel);//.setMode(impedanceControlMode);
					getLogger().info(Double.toString(vel));
				}
			}
		};

		IUserKeyListener listener1 = new IUserKeyListener() {
			@Override
			public void onKeyEvent(IUserKey key, UserKeyEvent event) {
				if ( event == UserKeyEvent.KeyDown){	
					vel -= 0.1;
					if ( vel <= 0.1 ){
						vel = 0.1;
					}
					down_motion = linRel( 0.0 , 0.0, -50.0, World.Current.getRootFrame()).setJointVelocityRel(vel);//.setMode(impedanceControlMode);
					up_motion   = linRel( 0.0 , 0.0, 50.0 , World.Current.getRootFrame()).setJointVelocityRel(vel);//.setMode(impedanceControlMode);
					getLogger().info(Double.toString(vel));
				}
			}
		};

		IUserKey key0 = pasekUzytkownika.addUserKey(0, listener0, true);
		IUserKey key1 = pasekUzytkownika.addUserKey(1, listener1, true);

		key0.setLED(UserKeyAlignment.Middle, UserKeyLED.Green, UserKeyLEDSize.Normal);
		key1.setLED(UserKeyAlignment.Middle, UserKeyLED.Red, UserKeyLEDSize.Normal);

		pasekUzytkownika.publish(); */
	}

	public void run() {

		AbstractIO switch_1 = mediaFlange.getInput("UserButton");
		BooleanIOCondition switch1_active = new BooleanIOCondition(switch_1,
				true);

		down_motion = linRel( 0.0 , 0.0, -50.0, World.Current.getRootFrame()).setJointVelocityRel(vel);//.setMode(impedanceControlMode);
		up_motion   = linRel( 0.0 , 0.0, 50.0 , World.Current.getRootFrame()).setJointVelocityRel(vel);//.setMode(impedanceControlMode);

		alfa = lbr.getCommandedCartesianPosition(lbr.getFlange()).getAlphaRad();
		beta = lbr.getCommandedCartesianPosition(lbr.getFlange()).getBetaRad();
		gamma = lbr.getCommandedCartesianPosition(lbr.getFlange()).getGammaRad();




		while( true ){

			userButton = mediaFlange.getUserButton();
			ball.move(batch(down_motion, up_motion).breakWhen(switch1_active)); 
//
//			if( userButton == true){
//				mediaFlange.setLEDBlue(true);
//				ball.move(handMotion);
//				mediaFlange.setLEDBlue(false);
//			}
//

			while( userButton == true ){ 
				// miganie dioda
				for( int i = 0 ; i < 6 ; i ++ ){
					mediaFlange.setLEDBlue( true );
					ThreadUtil.milliSleep(50);
					mediaFlange.setLEDBlue( false );
					ThreadUtil.milliSleep(50);
				}
				// handguiding
				getLogger().info("Rozpoczecie HG");
				mediaFlange.setLEDBlue( true );
				ball.move(handGuiding());
				mediaFlange.setLEDBlue( false );
				getLogger().info("Koniec HG");

				//ball.move(ptp(ramka).setJointVelocityRel(0.05));

				int positionQuestion = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, positionQuestionTxt, "Tak", "Nie");

				if( positionQuestion == 0 ){ // przypisanie prostopadlego polozenia efektora i ruch ptp zmieniajacy tylko katy, pozycja xyz bez zmian
					ramka = lbr.getCurrentCartesianPosition(ball.getFrame("/kulaFrame")); // pobranie aktualnej pozycji kartezjanskiej
					ramka.setAlphaRad(Math.toRadians(-90.0));
					ramka.setBetaRad(Math.toRadians(0.0));
					ramka.setGammaRad(Math.toRadians(-180));
//					ramka.setX(ball.getFrame("/kulaFrame").getY());
//					ramka.setY(ball.getFrame("/kulaFrame").getY());
//					ramka.setZ(ball.getFrame("/kulaFrame").getZ());
					
					lbr.move(lin(ramka).setJointVelocityRel(0.1));
				}

				down_motion = linRel(0.0,0.0,-50.0, 0,0,0).setJointVelocityRel(vel);//.setMode(impedanceControlMode);
				up_motion   = linRel(0.0,0.0,50.0, 0,0,0).setJointVelocityRel(vel);//.setMode(impedanceControlMode);

				getLogger().info("Pozycja zmieniona, ruch rozpocznie sie za 5 sekund" +" \n " + "wcisnij zielony przycisk w celu ponownego prowadzenia.");
				
				userButton = false;
				
				//mozliwosc zmiany 
				for( int i = 0 ; i < 501 ; i++ ){
					ThreadUtil.milliSleep(10);
					if ( i % 100 == 0 ) getLogger().info( Integer.toString(i/100) + "s" );

					if ( userButton == true ){
						getLogger().info("Uruchomiono ponowne prowadzenie");
						break;
					}


				}		
			}

		} 
	}
}



