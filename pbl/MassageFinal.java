package pbl;


import com.kuka.common.ThreadUtil;
import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.*;
import static com.kuka.roboticsAPI.motionModel.MMCMotions.*;
import com.kuka.roboticsAPI.conditionModel.BooleanIOCondition;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.ioModel.AbstractIO;
import com.kuka.roboticsAPI.motionModel.RelativeLIN;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.uiModel.ApplicationDialogType;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyBar;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyAlignment;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;


public class MassageFinal extends RoboticsAPIApplication {


    private LBR lbr;
    private MediaFlangeIOGroup mediaFlange;
    private Controller kuka_Sunrise_Cabinet_1;
    private AbstractIO greenButton;
    private BooleanIOCondition greenButton_active;
    private double velocity = 0.1;
    private double positionChange = 50;
    private Tool yellowBall;

    private boolean flag;

    private RelativeLIN downMotion;
    private RelativeLIN upMotion;

    private boolean userButton;

    private static final int stiffnessX = 1000;
    private static final int stiffnessY = 1000;
    private static final int stiffnessZ = 1000;

    //lancuchy znakow
    private String startQuestionString = "This application is a demonstration of a massage with hand guiding";
    private String startDecisionString = "What position to set?";
    private String positionQuestionString = "Do you want to set the effector perpendicular to floor?";

    public void initialize() {

        lbr = getContext().getDeviceFromType(LBR.class);
        kuka_Sunrise_Cabinet_1 = getController("KUKA_Sunrise_Cabinet_1"); // inicjalizacja kontrolera

        yellowBall = getApplicationData().createFromTemplate("kulaTool");
        yellowBall.attachTo(lbr.getFlange());

        mediaFlange = new MediaFlangeIOGroup(kuka_Sunrise_Cabinet_1);
        mediaFlange.setLEDBlue(false);
        greenButton = mediaFlange.getInput("UserButton");
        greenButton_active = new BooleanIOCondition(greenButton, true);

        IUserKeyBar userBar = getApplicationUI().createUserKeyBar("User bar"); // stworzenie paska uzytkownika

        IUserKeyListener listSpeedUp = new IUserKeyListener() {
            @Override
            public void onKeyEvent(IUserKey key, UserKeyEvent event) {
                if (event == UserKeyEvent.KeyDown) {
                    flag = true;
                    velocity += 0.1;
                    if (velocity >= 1.0) velocity = 1.0;
                    getLogger().info("\n");
                    getLogger().info("Relative velocity: " + Double.toString(velocity));
                    getLogger().info("Position change: " + Double.toString(2 * positionChange));
                }
            }
        };
        IUserKeyListener listSpeedDown = new IUserKeyListener() {
            @Override
            public void onKeyEvent(IUserKey key, UserKeyEvent event) {
                if (event == UserKeyEvent.KeyDown) {
                    flag = true;
                    velocity -= 0.1;
                    if (velocity <= 0.1) velocity = 0.1;
                    getLogger().info("\n");
                    getLogger().info("Relative velocity: " + Double.toString(velocity));
                    getLogger().info("Position change: " + Double.toString(2 * positionChange));
                }
            }
        };
        IUserKeyListener listPosUp = new IUserKeyListener() {
            @Override
            public void onKeyEvent(IUserKey key, UserKeyEvent event) {
                if (event == UserKeyEvent.KeyDown) {
                    flag = true;
                    positionChange += 5;
                    if (positionChange >= 50) positionChange = 50;
                    getLogger().info("\n");
                    getLogger().info("Relative velocity: " + Double.toString(velocity));
                    getLogger().info("Position change: " + Double.toString(2 * positionChange));
                }
            }
        };
        IUserKeyListener listPosDown = new IUserKeyListener() {
            @Override
            public void onKeyEvent(IUserKey key, UserKeyEvent event) {
                if (event == UserKeyEvent.KeyDown) {
                    flag = true;
                    positionChange -= 5;
                    if (positionChange <= 5) positionChange = 5;
                    getLogger().info("\n");
                    getLogger().info("Relative velocity: " + Double.toString(velocity));
                    getLogger().info("Position change: " + Double.toString(2 * positionChange));
                }
            }
        };

        IUserKey key0 = userBar.addUserKey(0, listSpeedUp, true);
        IUserKey key1 = userBar.addUserKey(1, listSpeedDown, true);
        IUserKey key2 = userBar.addUserKey(2, listPosUp, true);
        IUserKey key3 = userBar.addUserKey(3, listPosDown, true);

        key0.setText(UserKeyAlignment.Middle, "Speed up");
        key1.setText(UserKeyAlignment.Middle, "Speed down");
        key2.setText(UserKeyAlignment.Middle, "Position up");
        key3.setText(UserKeyAlignment.Middle, "Position down");

        userBar.publish();
    }


    public void run() {

        int startQuestion = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, startQuestionString, "Ok", "Exit");

        if (startQuestion == 1) { // jeœli wybrano Exit, wychodzi z metody run i koñczy program
            getLogger().info("End of program");
            return;
        } else getLogger().info("Start of the program");

        int startDecision = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, startDecisionString, "Home", "Present");

        if (startDecision == 0) {
            getLogger().info("Move to home position in 1 s");
            ThreadUtil.milliSleep(1000);
            yellowBall.move(ptp(getApplicationData().getFrame("/P3")).setJointVelocityRel(0.10));
        } else {
            getLogger().info("Move will start in 1 s");
            ThreadUtil.milliSleep(1000);
        }

        // stworzenie obiektu regulatora impedancji 
        CartesianImpedanceControlMode impedanceControlMode = new CartesianImpedanceControlMode();
        impedanceControlMode.parametrize(CartDOF.X).setStiffness(stiffnessX);
        impedanceControlMode.parametrize(CartDOF.Y).setStiffness(stiffnessY);
        impedanceControlMode.parametrize(CartDOF.Z).setStiffness(stiffnessZ);

        downMotion = linRel(0, 0, positionChange, 0, 0, 0).setJointVelocityRel(velocity).setMode(impedanceControlMode);
        upMotion = linRel(0, 0, -positionChange, 0, 0, 0).setJointVelocityRel(velocity).setMode(impedanceControlMode);


        while (true) {

            if (flag == true) {
                downMotion = linRel(0, 0, positionChange, 0, 0, 0).setJointVelocityRel(velocity).setMode(impedanceControlMode);
                upMotion = linRel(0, 0, -positionChange, 0, 0, 0).setJointVelocityRel(velocity).setMode(impedanceControlMode);
                flag = false;
            }

            yellowBall.move(batch(downMotion, upMotion).breakWhen(greenButton_active));

            userButton = mediaFlange.getUserButton();

            while (userButton == true) {

                for (int i = 0; i < 6; i++) { // blinking led
                    mediaFlange.setLEDBlue(true);
                    ThreadUtil.milliSleep(50);
                    mediaFlange.setLEDBlue(false);
                    ThreadUtil.milliSleep(50);
                }

                getLogger().info("Start of the hand guiding"); // handguiding
                mediaFlange.setLEDBlue(true);
                yellowBall.move(handGuiding());
                mediaFlange.setLEDBlue(false);
                getLogger().info("End of the hand guiding");

                int positionQuestion = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, positionQuestionString, "Yes", "No");

                if (positionQuestion == 0) { // przypisanie prostopadlego polozenia efektora i ruch ptp zmieniajacy tylko katy, pozycja xyz bez zmian
                    Frame framePerpendicular;

                    framePerpendicular = lbr.getCommandedCartesianPosition(yellowBall.getFrame("/kulaFrame")); // pobranie aktualnej pozycji kartezjanskiej
                    framePerpendicular.setAlphaRad(framePerpendicular.getAlphaRad()); // k¹t alfa wziêty z obecnej ramki
                    framePerpendicular.setBetaRad(Math.toRadians(0.0));
                    framePerpendicular.setGammaRad(Math.toRadians(-180));
                    yellowBall.move(ptp(framePerpendicular).setJointVelocityRel(0.1).setMode(impedanceControlMode));
                }
                getLogger().info("Position will change in 2 seconds" + " \n " + "press green button on media flange to start hand guiding.");

                userButton = false;

                for (int i = 0; i < 201; i++) { //mozliwosc zmiany 
                    ThreadUtil.milliSleep(10);
                    if (i % 100 == 0) getLogger().info(Integer.toString(i / 100) + "s");

                    userButton = mediaFlange.getUserButton();

                    if (userButton == true) {
                        getLogger().info("Start of the hand guiding");
                        break;
                    }
                }
            }
        }
    }
}