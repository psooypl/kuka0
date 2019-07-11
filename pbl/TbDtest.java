package pbl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.*;
import com.kuka.roboticsAPI.conditionModel.BooleanIOCondition;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.ioModel.AbstractIO;
import com.kuka.roboticsAPI.motionModel.HandGuidingMotion;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyBar;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyAlignment;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;

public class TbDtest extends RoboticsAPIApplication {

	private LBR lbr;
	private MediaFlangeIOGroup mediaFlange;
	private Controller kuka_Sunrise_Cabinet_1;
	private AbstractIO greenButton;
	private HandGuidingMotion handGuide;
	private BooleanIOCondition greenButton_active;
	private Frame nextFrame;
	private boolean flag, movingFlag;
	private boolean userButton;
	private ArrayList<Frame> frameList;
	private int licznik = 1;
	private int stiffness = 1000;
	private CartesianImpedanceControlMode impedanceControlMode;
	private int ilKrok;
	private boolean movingFlagOld;


	public void initialize() {

		lbr = getContext().getDeviceFromType(LBR.class);
		kuka_Sunrise_Cabinet_1 = getController("KUKA_Sunrise_Cabinet_1"); 
		mediaFlange = new MediaFlangeIOGroup(kuka_Sunrise_Cabinet_1);
		mediaFlange.setLEDBlue(false);
		handGuide = new HandGuidingMotion();

		impedanceControlMode = new CartesianImpedanceControlMode();
		impedanceControlMode.parametrize(CartDOF.X).setStiffness(stiffness);
		impedanceControlMode.parametrize(CartDOF.Y).setStiffness(stiffness);
		impedanceControlMode.parametrize(CartDOF.Z).setStiffness(stiffness);

		frameList = new ArrayList<Frame>();

		// stworzenie paska uzytkownika i przycisku umozliwiajacego wykonanie sekwencji
		IUserKeyBar userBar = getApplicationUI().createUserKeyBar("User bar"); 
		IUserKeyListener listMoving = new IUserKeyListener() {
			@Override
			public void onKeyEvent(IUserKey key, UserKeyEvent event) {
				if (event == UserKeyEvent.KeyDown) {
					getLogger().info("Robot rozpocznie poruszanie się po trajektorii");

					movingFlag = true;

				}
			}
		};


		IUserKey key0 = userBar.addUserKey(0, listMoving, true);
		key0.setText(UserKeyAlignment.Middle, "Move trajectory");
		userBar.publish();
	}

	@Override
	public void run() throws IOException {

		lbr.move(ptp(0,0,0,0,0,0,0 ).setJointJerkRel(0.01));

		JointPosition position = lbr.getCurrentJointPosition();
//		Buffered
//		FileWriter zapisDoPliku = new FileWriter("E:/pozycje.txt");
//		
		FileReader reader = new FileReader("E:/pozycje.txt");
		String loge;
		loge = Integer.toString(reader.read());
		getLogger().info(loge);

		//	File file = new File("C:/plik.txt");

		String[] ramkaTxt = new String[7];

		ramkaTxt[0] = Double.toString(position.get(0));
		ramkaTxt[1] = Double.toString(position.get(1));
		ramkaTxt[2] = Double.toString(position.get(2));
		ramkaTxt[3] = Double.toString(position.get(3));
		ramkaTxt[4] = Double.toString(position.get(4));
		ramkaTxt[5] = Double.toString(position.get(5));
		ramkaTxt[6] = Double.toString(position.get(6));

		//zapisDoPliku.write("sdasdas");
		
		
	}
}