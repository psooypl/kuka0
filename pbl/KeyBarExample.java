package pbl;

import javax.inject.Inject;
import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyBar;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyAlignment;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyLED;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyLEDSize;

public class KeyBarExample extends RoboticsAPIApplication {

	private LBR lbr;
	@Inject
	MediaFlangeIOGroup mediaFlange;

	public void initialize() {
		lbr = getContext().getDeviceFromType(LBR.class);
		// inicjalizacja kontrolera

//
//		IUserKeyBar barExample = getApplicationUI().createUserKeyBar("BarExample"); // stworzenie paska uzytkownika o nazwie XYZ
//
//		IUserKeyListener listener0 = new IUserKeyListener() {  // stworzenie "lisetnera", onKeyEvent - zadanie wykonywane po okreœlonej akcji przycisku 
//			@Override
//			public void onKeyEvent(IUserKey key, UserKeyEvent event) {
//				if ( event == UserKeyEvent.KeyDown){	
//					mediaFlange.setLEDBlue(true);  // w tym przypadku zapalaja sie niebieskie diody na manipulatorze po nacisnieciu przycisku
//				}
//			}
//		};
//
//		IUserKeyListener listener1 = new IUserKeyListener() {
//			@Override
//			public void onKeyEvent(IUserKey key, UserKeyEvent event) {
//				if ( event == UserKeyEvent.KeyDown){	
//					mediaFlange.setLEDBlue(false); // gasza sie diody po nacisnieciu przycisku
//				}
//			}
//		};
//
//		IUserKey key0 = barExample.addUserKey(0, listener0, true); // stworzenie przyciskow  i dodanie ich do odpowiednich slotow stworzonego wczesniej paska
//		IUserKey key1 = barExample.addUserKey(1, listener1, true);
//
//		key0.setLED(UserKeyAlignment.Middle, UserKeyLED.Green, UserKeyLEDSize.Normal); // stworzenie graficznej reprezentacji na pasku w tym przypadku diody zielona i czerwona
//		key1.setLED(UserKeyAlignment.Middle, UserKeyLED.Red, UserKeyLEDSize.Normal);
//
//		barExample.publish(); // opublikowanie paska
	}

	public void run() {

	}
}



