package pbl;


import java.util.ArrayList;
import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.*;
import com.kuka.roboticsAPI.conditionModel.BooleanIOCondition;
import com.kuka.roboticsAPI.controllerModel.Controller;
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

public class TeachingByDemonstration extends RoboticsAPIApplication {

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
					getLogger().info("Robot rozpocznie poruszanie siê po trajektorii");
					
					movingFlag = true;

				}
			}
		};

		IUserKey key0 = userBar.addUserKey(0, listMoving, true);
		key0.setText(UserKeyAlignment.Middle, "Move trajectory");
		userBar.publish();
	}


	public void run() {

		lbr.move(ptp(0,0,0,0,0,0,0).setJointJerkRel(0.05));
		
		getLogger().info("PrzejdŸ do pozycji pocz¹tkowej");
		lbr.move(handGuide);

		while (true) {
			userButton = mediaFlange.getUserButton(); //czytanie co pêtle stanu zielonego przycisku na ko³nierzu

			//na poczatku wszystkie flagi ustawione na false
			//pierwszy if
			if(userButton){ // jezeli zielony wcisniety

				/*if do zapisu punktu. !movingFlagOld czyli jezeli zosta³a wykonana sekwencja ruchow to za pierwszym
				 *  nacisnieciem zielonego przycisku nie zapisze punktu, zeby nie nadpisywac dwa razy ostatniego punktu
				 */
				if(!flag && !movingFlagOld && (ilKrok < 2500)){ 
					getLogger().info("Ramka nr: " + Integer.toString(licznik) + " zosta³a dodana."); //wyswietlanie na smartpadzie
					nextFrame = lbr.getCurrentCartesianPosition(lbr.getFlange()); //zapis aktualnej pozycji w ramce 
					frameList.add(nextFrame);// dodanie biezacej ramki do array list
					licznik++;	
					flag = true; // ustawienie flagi umozliwiajacej ruch robotem
				} else if (movingFlagOld){  //jezeli poprzednio wykonany byla sekwencja zezwala na ruch robota
					flag = true;
				}

				movingFlagOld = false;

				if(flag && ilKrok > 5000){ // jezeli przytrzyma siê zielony przycisk dluzej, zezwala na wykonanie sekwencji
					flag = false;
					getLogger().info("Robot gotowy do wykonania ruchu");
				}
				ilKrok++; //inkrementacja licznika umozliwiajacego wykonanie sekwencji
			}
			//drugi if
			if(movingFlag && !flag){
				//petla wykonujaca sekwencje ruchow ptp
				for (int i = 0; i < licznik - 1; i++){
					lbr.move(ptp(frameList.get(i)).setJointVelocityRel(0.5).setMode(impedanceControlMode));
				}
				
				movingFlagOld =  movingFlag; //flagOld przyjmuje stary stan flagi zeby mozna bylo nienadpisywac ostatniego punktu
				movingFlag = false;
				ilKrok = 0;  //zerowanie ilosci krokow
				getLogger().info("Flaga ruchu wylaczona mFo: " + Boolean.toString(movingFlagOld));
			}
			//trzeci if
			if (!userButton){ // if do przesuwania recznego, "flaga" ustawiona w pierwszym ifie
				if(flag && ilKrok < 2500){
					lbr.move(handGuide);
					getLogger().info(Integer.toString(ilKrok));
					flag = false;
					ilKrok = 0;
				}
			}

		}
	}
}